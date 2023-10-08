package com.mo.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.auth.mapper.SysRoleMapper;
import com.mo.auth.service.SysRoleService;
import com.mo.auth.service.SysUserRoleService;
import com.mo.model.system.SysRole;
import com.mo.model.system.SysUserRole;
import com.mo.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author mozihao
 * create 2023-09-28 16:21
 * Description
 */
@Service
public class SysRoleServicImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    //mp已自动注入
//    @Autowired
//    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    //查询所有角色和当前用户所属角色
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        List<SysRole> allRoleList = baseMapper.selectList(null);
        //从用户关系表中查询用户的所有角色
        List<SysUserRole> exitUserRoleList = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId));

        List<Long> exitRoleIdList = exitUserRoleList.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
        List<SysRole> assingRoleList = new ArrayList<>();
        for (SysRole sysRole : allRoleList) {
            if(exitRoleIdList.contains(sysRole.getId())){
                assingRoleList.add(sysRole);
            }
        }
        HashMap<String, Object> roleMap = new HashMap<>();
        roleMap.put("allRolesList",allRoleList);
        roleMap.put("assginRoleList",assingRoleList);
        return roleMap;
    }
    //为用户分配角色
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //根据用户id删除表中已分配的角色
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,assginRoleVo.getUserId()));
        //重新插入
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if(!StringUtils.isEmpty(roleId)){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(assginRoleVo.getUserId());
                sysUserRoleService.save(sysUserRole);
            }
        }
    }
}
