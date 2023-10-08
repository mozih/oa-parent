package com.mo.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.auth.service.SysRoleService;
import com.mo.common.config.execption.MoException;
import com.mo.common.result.Result;
import com.mo.model.system.SysRole;
import com.mo.vo.system.AssginRoleVo;
import com.mo.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * author mozihao
 * create 2023-09-28 16:32
 * Description
 */
@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        return Result.ok(sysRoleService.findRoleDataByUserId(userId));
    }

    @ApiOperation(("为用户分配角色"))
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    //查询所有角色
//    @GetMapping("/findAll")
//    public List<SysRole> findAll(){
//        return sysRoleService.list();
//    }

    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result findAll(){
        //手动制造异常
        try {
            int a = 1/0;
        }catch (Exception e){
            //抛出自定义异常
            throw new MoException(20001,"执行了自定义异常处理");
        }

        return Result.ok(sysRoleService.list());
    }

    //条件分页查询
    //page 当前页  limit 每页显示记录
    //条件对象
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page, @PathVariable Long limit, SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> pageParam = new Page<>(page,limit);
        LambdaQueryWrapper<SysRole> wapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            wapper.like(SysRole::getRoleName,roleName);
        }
        return Result.ok(sysRoleService.page(pageParam,wapper));
    }

    //添加角色
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole sysRole){
        return sysRoleService.save(sysRole) ? Result.ok() : Result.fail();
    }

    //修改角色-根据id查询
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        return Result.ok(sysRoleService.getById(id));
    }

    //修改角色-最终修改
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole sysRole){
        return sysRoleService.updateById(sysRole) ? Result.ok() : Result.fail();
    }

    //根据id删除
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id){
        return sysRoleService.removeById(id) ? Result.ok() : Result.fail();
    }

    //批量删除
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        return sysRoleService.removeByIds(ids) ? Result.ok() : Result.fail();
    }
}
