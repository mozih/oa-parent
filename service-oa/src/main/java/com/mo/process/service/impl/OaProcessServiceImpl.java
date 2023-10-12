package com.mo.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.auth.service.SysUserService;
import com.mo.common.config.execption.MoException;
import com.mo.model.process.ProcessRecord;
import com.mo.model.process.ProcessTemplate;
import com.mo.model.system.SysUser;
import com.mo.process.mapper.OaProcessMapper;
import com.mo.process.service.OaProcessRecordService;
import com.mo.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.process.service.OaProcessTemplateService;
import com.mo.security.custom.LoginUserInfoHelper;
import com.mo.vo.process.ApprovalVo;
import com.mo.vo.process.ProcessFormVo;
import com.mo.vo.process.ProcessQueryVo;
import com.mo.vo.process.ProcessVo;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mo.model.process.Process;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author mo
 * @since 2023-10-11
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private OaProcessTemplateService processTemplateService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private OaProcessRecordService  processRecordService;
    @Autowired
    private HistoryService historyService;

    //审批管理列表
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        return pageModel;
    }
    //部署流程定义
    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //部署
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //启动流程实例
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //用户信息
        SysUser sysUser = userService.getById(LoginUserInfoHelper.getUserId());
        //模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        //保存提交审批信息到oa_process
        Process process = new Process();
        //将processFormVo复制到process中
        BeanUtils.copyProperties(processFormVo,process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);//审批中
        baseMapper.insert(process);

        //启动流程实例,流程定义key 业务key processId，定义流程参数form表单json数据转换为map集合
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();

        JSONObject jsonObject = JSON.parseObject(processFormVo.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        formData.entrySet().stream().forEach(entry->{
            map.put(entry.getKey(),entry.getValue());
        });
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("data",map);
        //启动
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, String.valueOf(process.getId()), variables);

        //查询下一个审批人可能有多个
        List<String> nameList = new ArrayList<>();
        //查询当前任务列表
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        taskList.stream().forEach(task -> {
            String assigneeName = task.getAssignee();
            SysUser user = userService.getUserByUserName(assigneeName);
            nameList.add(user.getName());
            //todo 推送消息
        });
        //业务流程关联，更新oa_process
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray(),",") +"审批");
        baseMapper.updateById(process);

        //记录操作审批信息记录
        processRecordService.record(process.getId(),1,"发起申请");
    }
    //查询待处理任务列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        //封装条件
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime().desc();
        //调用方法分页条件查询
        //第一个参数开始位置，第二个参数 每页显示记录数
        int begin = (int) ((pageParam.getCurrent()-1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> taskList = query.listPage(begin, size);
        long totalCount = query.count();

        //封装数据为processVo
        List<ProcessVo> processVoList = new ArrayList<>();
        taskList.stream().forEach(task -> {
            //获取流程实例id获取流程实例对象，获取业务key再获取process
//            String processInstanceId = task.getProcessInstanceId();
//            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
//                    .singleResult();
//            String businessKey1 = processInstance.getBusinessKey();
            String businessKey = task.getBusinessKey();
            if (null != businessKey){
                //获取process对象
                long processId = Long.parseLong(businessKey);
                Process process = baseMapper.selectById(processId);
                //复制Vo对象
                ProcessVo processVo = new ProcessVo();
                BeanUtils.copyProperties(process,processVo);
                processVo.setTaskId(task.getId());
                processVoList.add(processVo);
            }
        });
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        page.setRecords(processVoList);
        return page;
    }
    //查询审批详情信息
    @Override
    public Map<String, Object> show(Long id) {
        //获取流程信息process
        Process process = baseMapper.selectById(id);
        //获取流程记录信息
        List<ProcessRecord> processRecordList = processRecordService.list(new LambdaQueryWrapper<ProcessRecord>().eq(ProcessRecord::getProcessId, id));
        //获取模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        //判断是否可以审批，不能重复审批
        boolean isApprove = false;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();

        for (Task task : taskList) {
            //判断任务审批人是否是当前用户
            if (task.getAssignee().equals(LoginUserInfoHelper.getUsername())){
                isApprove = true;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }
    //审批任务
    @Override
    public void approve(ApprovalVo approvalVo) {
        //从流程任务中获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);

        variables.entrySet().forEach(entry->{
            System.out.println("流程变量");
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        });
        String description = "";
        //判断审批状态值，=1审批通过 -1=驳回，流程直接结束
        if(approvalVo.getStatus() == 1){
            //可以设置自定义特殊的流程变量、
            HashMap<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
            description = "已通过";
        }else {
            //结束流程
            this.endTask(taskId);
            description = "审批驳回";
        }
        //记录审批过程相关信息oa_process_record
        processRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);

        //查询下一个审批人，更新process记录
        //查询当前任务列表
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        if (!CollectionUtils.isEmpty(taskList)){
            List<String> assignList = new ArrayList<>();
            taskList.stream().forEach(task -> {
                String assignee = task.getAssignee();
                SysUser sysUser = userService.getUserByUserName(assignee);
                if (sysUser == null){
                    throw new RuntimeException("系统无下一个审批人用户信息");
                }
                assignList.add(sysUser.getName());
                //todo 公众号消息推送
            });
            //更新流程信息
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        }else {
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);

    }
    //结束流程
    private void endTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //获取流程定义模型 BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        //获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isEmpty(endEventList)){
            return;
        }
        FlowNode endFlowNode = endEventList.get(0);
        //当前流向节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());
        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();

        //创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);

        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        //  完成当前任务
        taskService.complete(task.getId());
    }

    //查询已处理任务
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime().desc();
        //分页查询
        int begin = (int) ((pageParam.getCurrent()-1)*pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();

        //封装成List<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        list.stream().forEach(historicTaskInstance -> {
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
            Process process = baseMapper.selectOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVoList.add(processVo);
        });
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        pageModel.setRecords(processVoList);
        return pageModel;
    }
    //查询已发起任务
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        return baseMapper.selectPage(pageParam, processQueryVo);
    }


}
