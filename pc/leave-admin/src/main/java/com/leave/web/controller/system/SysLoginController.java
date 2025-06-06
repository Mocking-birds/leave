package com.leave.web.controller.system;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


import com.leave.common.core.domain.model.LoginUser;
import com.leave.framework.web.service.UserDetailsServiceImpl;
import com.leave.system.service.ISysUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.leave.common.constant.Constants;
import com.leave.common.core.domain.AjaxResult;
import com.leave.common.core.domain.entity.SysMenu;
import com.leave.common.core.domain.entity.SysUser;
import com.leave.common.core.domain.model.LoginBody;
import com.leave.common.utils.SecurityUtils;
import com.leave.framework.web.service.SysLoginService;
import com.leave.framework.web.service.SysPermissionService;
import com.leave.system.service.ISysMenuService;


/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;


    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 微信登录
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/wechat/login")
    public AjaxResult wechatLogin(@RequestBody LoginBody loginBody){
        AjaxResult ajax = AjaxResult.success();

        System.out.println("微信登录信息：" + loginBody.getJsCode());

        String token = loginService.wechatLogin(loginBody.getJsCode());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 短信验证码发送
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    @GetMapping("/smscode")
    public AjaxResult getSmscode(String phonenumber) throws Exception {

        SysUser user1 = userService.selectUserByPhone(phonenumber);
        if(user1 != null){
            Boolean res = loginService.getSmscode(phonenumber);
            if(res){
                return AjaxResult.success();
            }else {
                return AjaxResult.error("短信发送失败");
            }
        }else {
            return AjaxResult.error("手机号没有对应用户");
        }
    }

    /**
     * 短信验证码登录
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/phone/login")
    public AjaxResult phoneLogin(@RequestBody LoginBody loginBody) throws Exception {
        AjaxResult ajax = AjaxResult.success();

        loginService.phoneLogin(loginBody);

        String token = loginService.phoneLogin(loginBody);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
