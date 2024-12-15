package com.leave.framework.web.service;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.leave.common.constant.CacheConstants;
import com.leave.common.constant.Constants;
import com.leave.common.constant.UserConstants;
import com.leave.common.core.domain.entity.SysUser;
import com.leave.common.core.domain.model.LoginUser;
import com.leave.common.core.redis.RedisCache;
import com.leave.common.exception.ServiceException;
import com.leave.common.exception.user.BlackListException;
import com.leave.common.exception.user.CaptchaException;
import com.leave.common.exception.user.CaptchaExpireException;
import com.leave.common.exception.user.UserNotExistsException;
import com.leave.common.exception.user.UserPasswordNotMatchException;
import com.leave.common.utils.DateUtils;
import com.leave.common.utils.MessageUtils;
import com.leave.common.utils.StringUtils;
import com.leave.common.utils.ip.IpUtils;
import com.leave.framework.manager.AsyncManager;
import com.leave.framework.manager.factory.AsyncFactory;
import com.leave.framework.security.context.AuthenticationContextHolder;
import com.leave.system.service.ISysConfigService;
import com.leave.system.service.ISysUserService;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    // 令牌秘钥
    @Value("${token.secret}")
    private String jwtSecret;

    private final RestTemplate restTemplate;

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SysLoginService(RestTemplate restTemplate, UserDetailsServiceImpl userDetailsService) {
        this.restTemplate = restTemplate;
        this.userDetailsService = userDetailsService;
    }


    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        System.out.println("密码登录验证：" + loginUser);
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 微信登录验证
     *
     * @param code
     * @return 结果
     */
    public String wechatLogin(String code){
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",appid,secret,code);
        String response = restTemplate.getForObject(url, String.class);

        // json转map
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String,String>>(){}.getType();
        Map<String, String> result = gson.fromJson(response, mapType);
        System.out.println("result："+result);
        if (Objects.equals(result.get("errcode"), "40163")){
            throw new ServiceException(StringUtils.format("获取微信授权信息失败"));
        }else {
            SysUser user = userService.selectUserByOpenId(result.get("openid"));
            System.out.println("测试："+user);
            UserDetails userDetail = userDetailsService.createLoginUser(user);
            LoginUser loginUser = BeanUtil.copyProperties(userDetail, LoginUser.class);
            recordLoginInfo(loginUser.getUserId());
            return tokenService.createToken(loginUser);
        }
    }


    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }


    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }

}
