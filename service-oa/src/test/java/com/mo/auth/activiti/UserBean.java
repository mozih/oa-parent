package com.mo.auth.activiti;

import org.springframework.stereotype.Component;

/**
 * author mozihao
 * create 2023-10-10 16:19
 * Description
 */
@Component
public class UserBean {

    public String getUsername(int id){
        if(id == 1){
            return "lilei";
        }else if (id == 2){
            return "hanmeimei";
        }else {
            return "admin";
        }
    }
}
