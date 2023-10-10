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
public class ProcessTestDemo3 {
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

    //1 部署流程定义加班
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban04.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程04")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //启动流程实例
    @Test
    public void startProcessInstance(){
        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("jiaban04");

        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getId());
    }
    //2 查询组任务
    @Test
    public void findGroupTaskList(){
        List<Task> list = taskService.createTaskQuery().taskCandidateUser("tom01").list();
        list.stream().forEach(task -> {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
    }
    //3 拾取组任务 拾取后其他人不能拾取
    @Test
    public void claimTask(){
        //拾取任务,即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
        //校验该用户有没有拾取任务的资格
        Task task = taskService.createTaskQuery().taskCandidateUser("tom01").singleResult();//根据候选人查询
        if (null != task){
            //拾取任务
            taskService.claim(task.getId(),"tom01");
            System.out.println("任务拾取成功");
        }
    }
    //查询个人待办任务
    @Test
    public void findTaskList(){
        String assign = "tom01";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.stream().forEach(item->{
            System.out.println("流程实例id：" + item.getProcessInstanceId());
            System.out.println("任务id：" + item.getId());
            System.out.println("任务负责人：" + item.getAssignee());
            System.out.println("任务名称：" + item.getName());
        });
    }
    //个人执行任务
    @Test
    public void completTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("tom01")  //要查询的负责人
                .singleResult();//返回一条
        //完成任务,参数：任务id
        taskService.complete(task.getId());
    }

}
