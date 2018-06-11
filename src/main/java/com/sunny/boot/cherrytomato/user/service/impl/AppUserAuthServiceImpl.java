/**
 * Copyright(C) 2018 Hangzhou sunny Technology Co., Ltd. All rights reserved.
 */
package com.sunny.boot.cherrytomato.user.service.impl;

import com.sunny.boot.cherrytomato.common.result.Response;
import com.sunny.boot.cherrytomato.user.controller.form.UserForm;
import com.sunny.boot.cherrytomato.user.mapper.AppUserByEmailMapper;
import com.sunny.boot.cherrytomato.user.mapper.AppUserByMobileMapper;
import com.sunny.boot.cherrytomato.user.mapper.AppUserByUsernameMapper;
import com.sunny.boot.cherrytomato.user.mapper.AppUserMapper;
import com.sunny.boot.cherrytomato.user.model.AppUser;
import com.sunny.boot.cherrytomato.user.model.AppUserByEmail;
import com.sunny.boot.cherrytomato.user.model.AppUserByMobile;
import com.sunny.boot.cherrytomato.user.model.AppUserByUsername;
import com.sunny.boot.cherrytomato.user.model.vo.AppUserVo;
import com.sunny.boot.cherrytomato.user.service.AppUserAuthService;
import com.sunny.boot.cherrytomato.util.CookieUtil;
import com.sunny.boot.cherrytomato.util.Md5Util;
import com.sunny.boot.cherrytomato.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author sunny
 * @class: com.sunny.boot.cherrytomato.user.service.impl.AppUserAuthServiceImpl
 * @date: 2018-06-11 9:38
 * @des: 用户认证模块
 */
@Service
public class AppUserAuthServiceImpl implements AppUserAuthService {
  @Autowired
  private AppUserByUsernameMapper appUserByUsernameMapper;
  @Autowired
  private AppUserByEmailMapper appUserByEmailMapper;
  @Autowired
  private AppUserByMobileMapper appUserByMobileMapper;
  @Autowired
  private AppUserMapper appUserMapper;

  /**
   * 根据用户名密码登录
   *
   * @param res
   * @param userName 用户名/邮箱/手机号
   * @param password 密码
   * @return
   */
  @Override
  public Response login(HttpServletResponse res, String userName, String password) {
    Long userId;

    if (StringUtil.isEmail(userName)) {
      userId = appUserByEmailMapper.selectUserIdByPrimaryKey(userName);
    } else if (StringUtil.isMobile(userName)) {
      userId = appUserByMobileMapper.selectUserIdByPrimaryKey(Long.valueOf(userName));
    } else {
      userId = appUserByUsernameMapper.selectUserIdByPrimaryKey(userName);
    }

    if (null == userId) {
      return new Response<Response.Result>(Response.Result.USERNAME_DOES_NOT_EXIST_ERROR);
    }
    //根据用户id获取用户信息
    AppUserVo appUserVo = appUserMapper.selectAppUserByPrimaryKey(userId);

    if (null == appUserVo) {
      return new Response<Response.Result>(Response.Result.USERNAME_DOES_NOT_EXIST_IN_APP_USER_ERROR);
    }

    //验证密码
    if (!StringUtil.equals(Md5Util.encrypt(password), appUserVo.getPassword())) {
      return new Response<Response.Result>(Response.Result.PASSWORD_NOT_EQUALS_ERROR);
    }

    //保存用户信息，生成token 用户id+md5 方便以后管理员修改用户信息
    String token = Md5Util.encrypt(String.valueOf(appUserVo.getId()));
    CookieUtil.setCookie(res, "token", token);
    appUserVo.setToken(token);
    //去除敏感信息
    appUserVo.setPassword(null);
    appUserVo.setStatus(null);
    appUserVo.setCreateDate(null);
    appUserVo.setCreator(null);
    appUserVo.setModifier(null);
    appUserVo.setModifyDate(null);

    return new Response<>(Response.Result.LOGIN_SUCCESS, appUserVo);
  }

  /**
   * 根据邮箱和密码注册
   *
   * @param res
   * @param email
   * @param password
   * @return
   */
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public Response registerByEmail(HttpServletResponse res, String email, String password) {
    //验证邮箱是否注册过
    if (emailIsExist(email)) {
      String msg = Response.Result.EMAIL_IS_EXIST_ERROR.getMsg();
      Response.Result.EMAIL_IS_EXIST_ERROR.setMsg(String.format(msg, email));
      return new Response<Response.Result>(Response.Result.EMAIL_IS_EXIST_ERROR);
    }
    //注册
    AppUser user = new AppUser();
    user.setEmail(email);
    user.setPassword(Md5Util.encrypt(password));
    appUserMapper.insertSelective(user);

    //添加根据邮箱查询
    AppUserByEmail appUserByEmail = new AppUserByEmail();
    appUserByEmail.setEmail(email);
    appUserByEmail.setUserId(user.getId());
    appUserByEmailMapper.insertSelective(appUserByEmail);
    //登陆
    return login(res, email, password);
  }

  /**
   * 邮箱是否存在
   *
   * @param email
   * @return
   */
  @Override
  public boolean emailIsExist(String email) {

    if (null != appUserByEmailMapper.selectUserIdByPrimaryKey(email)) {
      return true;
    }
    return false;
  }
}
