package com.mo.auth;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.auth.mapper.SysRoleMapper;
import com.mo.auth.service.SysRoleService;
import com.mo.model.system.SysRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * author mozihao
 * create 2023-09-27 16:23
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMpDemo1 {
    //注入
    @Autowired
    private SysRoleMapper mapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void getAll(){
//        SysRole sysRole1 = new SysRole();
//        sysRole1.setRoleCode("admin");
//        sysRole1.setRoleName("管理员");
//        sysRole1.setDescription("1");
//        sysRole1.setId(1l);
//        SysRole sysRole2 = new SysRole();
//        sysRole2.setRoleCode("manage");
//        sysRole2.setRoleName("总经理");
//        sysRole2.setDescription("2");
//        sysRole2.setId(2l);
//        mapper.insert(sysRole1);
//        mapper.insert(sysRole2);
        List<SysRole> sysRoles = mapper.selectList(null);
        System.out.println(sysRoles);
        //使用mp已实现的list方法
        System.out.println("mp实现的service的list方法:"+sysRoleService.list());
    }

    @Test
    public void add(){
        SysRole sysRole1 = new SysRole();
        sysRole1.setRoleCode("role1");
        sysRole1.setRoleName("角色管理员1");
        sysRole1.setDescription("角色管理员1");
        int row = mapper.insert(sysRole1);
        System.out.println(row);
        System.out.println(sysRole1.getId());
    }

    @Test
    public void update(){
        SysRole sysRole = mapper.selectById(12);
        sysRole.setRoleName("mo角色管理员");
        int rows = mapper.updateById(sysRole);
        System.out.println(rows);
    }

    //逻辑删除
    @Test
    public void deleteId(){
        int rows = mapper.deleteById(12);
        System.out.println(rows);
    }

    //批量删除
    @Test
    public void testDeleteBatchIds(){
        int result = mapper.deleteBatchIds(Arrays.asList(1, 2));
        System.out.println(result);
    }

    //条件查询
    @Test
    public void testQuery1(){
//        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
//        sysRoleQueryWrapper.eq("role_name","总经理");
        LambdaQueryWrapper<SysRole> sysRoleQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleQueryWrapper.eq(SysRole::getRoleName,"总经理");
        List<SysRole> sysRoles = mapper.selectList(sysRoleQueryWrapper);
        System.out.println(sysRoles);
    }
}
