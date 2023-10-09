package com.mo.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mo.auth.service.SysMenuService;
import com.mo.auth.service.SysUserService;
import com.mo.common.config.execption.MoException;
import com.mo.common.jwt.JwtHelper;
import com.mo.common.result.Result;
import com.mo.common.utils.MD5;
import com.mo.model.system.SysMenu;
import com.mo.model.system.SysUser;
import com.mo.vo.system.LoginVo;
import com.mo.vo.system.RouterVo;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author mozihao
 * create 2023-10-04 17:58
 * Description
 */
@Api(tags = "后台登录接口")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    //登录接口
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        //获取输入的用户名和密码
        String username = loginVo.getUsername();
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (sysUser == null) {
            throw new MoException(201,"用户不存在");
        }
        //判断密码是否正确
        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())){
            throw new MoException(201,"密码错误");
        }
        //判断用户是否冻结
        if (sysUser.getStatus().intValue() == 0) {
            throw new MoException(201,"用户已冻结");
        }
        //若都正确则生成token
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    @GetMapping("info")
    public Result info(HttpServletRequest request){
        //获取请求头中的token
        String token = request.getHeader("token");
        //从token获取用户信息
        Long userId = JwtHelper.getUserId(token);
        SysUser SysUser = sysUserService.getById(userId);
        //用户可操作的菜单
        List<RouterVo> routerList =  sysMenuService.findUserMenuListByUserId(userId);
        //用户可操作的按钮
        List<String> buttonList =  sysMenuService.findUserBtnByUserId(userId);

        Map<String,Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",SysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");

        map.put("routers",routerList);
        map.put("buttons",buttonList);

        return Result.ok(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
