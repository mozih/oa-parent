package com.mo.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author mozihao
 * create 2023-10-10 15:58
 * Description
 */
@SpringBootTest
public class ProcessTestDemo1 {
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

    ////////////////////////////////////////
    // 监听器分配任务
    @Test
    public void deployProcess02(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban02.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程02")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Test
    public void startProcessInstance02(){
        ProcessInstance jiaban01 = runtimeService.startProcessInstanceByKey("jiaban02");
        System.out.println(jiaban01.getProcessDefinitionId());
        System.out.println(jiaban01.getId());
    }


    ////////////////////////////////////////
    // uel-method
    @Test
    public void deployProcess01(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban01.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程01")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Test
    public void startProcessInstance01(){
        ProcessInstance jiaban01 = runtimeService.startProcessInstanceByKey("jiaban01");
        System.out.println(jiaban01.getProcessDefinitionId());
        System.out.println(jiaban01.getId());
    }



    ////////////////////////////////////////
    // uel-value
    //部署流程定义加班
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance(){
        Map<String, Object> map = new HashMap<>();
        //设置任务人
        map.put("assignee1","lucy");
        map.put("assignee2","mary");

        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban", map);

        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getId());
    }

    //查询个人待办任务
    @Test
    public void findTaskList(){
        String assign = "jack";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.stream().forEach(item->{
            System.out.println("流程实例id：" + item.getProcessInstanceId());
            System.out.println("任务id：" + item.getId());
            System.out.println("任务负责人：" + item.getAssignee());
            System.out.println("任务名称：" + item.getName());
        });
    }
}
