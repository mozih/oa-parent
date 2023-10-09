package com.mo.auth.service.impl;

import com.mo.auth.service.SysMenuService;
import com.mo.auth.service.SysUserService;
import com.mo.model.system.SysUser;
import com.mo.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author mozihao
 * create 2023-10-09 11:50
 * Description
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUserName(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }

        //根据userId查询用户操作权限数据
        List<String> userBtnList = sysMenuService.findUserBtnByUserId(sysUser.getId());
        //存放最终权限数据
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
//        for (String btn : userBtnList) {
//            authList.add(new SimpleGrantedAuthority(btn.trim()));
//        }
        userBtnList.stream().forEach(item -> authList.add(new SimpleGrantedAuthority(item.trim())));
        return new CustomUser(sysUser, authList);
    }
}
