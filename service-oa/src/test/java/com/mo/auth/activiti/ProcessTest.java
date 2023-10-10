package com.mo.auth.activiti;

import com.mo.model.system.SysUser;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * author mozihao
 * create 2023-10-10 11:12
 * Description
 */
//@RunWith(SpringRunner.class)//junit4需要加此注解，5不用
@SpringBootTest
public class ProcessTest {
    //流程定义部署，注入RepositoryService接口
    @Autowired
    private RepositoryService repositoryService;
    //启动流程实例注入RuntimeService
    @Autowired
    private RuntimeService runtimeService;
    //查询任务注入TaskService
    @Autowired
    private TaskService taskService;
    //查询已处理任务注入HistoryService
    @Autowired
    private HistoryService historyService;

    //单个文件的部署
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //zip文件部署



    //启动流程实例，可启动多个实例，一个人请假就是一个实例
    @Test
    public void startProcess(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println("流程定义id："+qingjia.getProcessDefinitionId());
        System.out.println("流程实例id："+qingjia.getId());
        System.out.println("当前活动id："+qingjia.getActivityId());
    }
    //创建流程实例，指定业务BusinessKey
    @Test
    public void startUpProcessAddBusinessKey(){
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println(instance.getBusinessKey());
        System.out.println(instance.getId());
    }


    //查询个人待办任务
    @Test
    public void findTaskList(){
        String assign = "zhangsan";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.stream().forEach(item->{
            System.out.println("流程实例id：" + item.getProcessInstanceId());
            System.out.println("任务id：" + item.getId());
            System.out.println("任务负责人：" + item.getAssignee());
            System.out.println("任务名称：" + item.getName());
        });
    }

    //处理当前任务
    @Test
    public void completeTask(){
        //查询一条负责人需要处理的任务
        Task task = taskService.createTaskQuery().taskAssignee("zhangsan").singleResult();
        //完成任务,参数为任务id
        taskService.complete(task.getId());
        //完成后，任务自动到下一个节点
    }

    //查询已处理任务
    @Test
    public void findCompleteTaskList(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("zhangsan")
                .finished().list();
        list.stream().forEach(item->{
            System.out.println("流程实例id：" + item.getProcessInstanceId());
            System.out.println("任务id：" + item.getId());
            System.out.println("任务负责人：" + item.getAssignee());
            System.out.println("任务名称：" + item.getName());
        });
    }

    //流程实例挂起/暂停
    //全部实例挂起
    @Test
    public void suspendProcessInstanceAll(){
        //获取流程定义对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia")
                .singleResult();
        //判断当前状态，挂起或激活,isSuspended值为true表示挂起
        if (qingjia.isSuspended()) {
            //如果挂起就激活
            //第一个参数流程定义id，第二个参数true表示激活，第三个参数时间点
            repositoryService.activateProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId()+"已激活");
        }else {
            repositoryService.suspendProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId()+"已挂起");
        }
    }
    //单个流程实例挂起
    @Test
    public void SingleSuspendProcessInstance(){
        String processInstanceId = "740fa682-673e-11ee-900b-2016b9895bd0";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance.isSuspended()) {
            //激活
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println(processInstanceId+"已激活");
        }else{
            //挂起
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println(processInstanceId+"已挂起");

        }
    }



}
