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
public class ProcessTestGateway {
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

    //1 部署流程定义
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/qingjia003.bpmn20.xml")
                .addClasspathResource("process/qingjia003.png")
                .name("请假申请流程003")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //启动流程实例
    @Test
    public void startProcessInstance(){
        Map<String, Object> map = new HashMap<>();
        //设置请假天数
//        map.put("day","3");

        ProcessInstance jiaban = runtimeService.startProcessInstanceByKey("qingjia003");

        System.out.println(jiaban.getProcessDefinitionId());
        System.out.println(jiaban.getId());
    }

    //查询个人待办任务
    @Test
    public void findTaskList(){
//        String assign = "wang5";
//        String assign = "gouwa";
        String assign = "xiaoli";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.stream().forEach(item->{
            System.out.println("流程实例id：" + item.getProcessInstanceId());
            System.out.println("任务id：" + item.getId());
            System.out.println("任务负责人：" + item.getAssignee());
            System.out.println("任务名称：" + item.getName());
        });
    }

    //执行任务时设置流程变量，作用域为整个流程实例变量
    @Test
    public void completTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("gouwa")  //要查询的负责人
//                .taskAssignee("wang5")
                .singleResult();//返回一条
        //完成任务,参数：任务id
        taskService.complete(task.getId());
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

}
