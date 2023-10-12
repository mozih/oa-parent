package com.mo.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.auth.service.SysUserService;
import com.mo.model.process.ProcessTemplate;
import com.mo.model.system.SysUser;
import com.mo.process.mapper.OaProcessMapper;
import com.mo.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.process.service.OaProcessTemplateService;
import com.mo.security.custom.LoginUserInfoHelper;
import com.mo.vo.process.ProcessFormVo;
import com.mo.vo.process.ProcessQueryVo;
import com.mo.vo.process.ProcessVo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mo.model.process.Process;

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
    }

}
