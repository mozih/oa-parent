package com.mo.auth.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.security.core.parameters.P;

/**
 * author mozihao
 * create 2023-10-10 16:45
 * Description
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if (delegateTask.getName().equals("经理审批")){
            //分配任务
            delegateTask.setAssignee("jack");
        }else if (delegateTask.getName().equals("人事审批")){
            delegateTask.setAssignee("tom");
        }
    }
}
