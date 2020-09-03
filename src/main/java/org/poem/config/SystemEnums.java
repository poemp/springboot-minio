package org.poem.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wei cao on 2020/8/11 9:38
 */
@Getter
@AllArgsConstructor
public enum SystemEnums {
    SUCCESS("success", "00000"),
    FAIL("fail","00001"),
    SYSTEM_ERROR("系统错误","00002"),
    SYSTEM_ROUTE_ERROR("联系管理员，微服务路径不正确","00003"),
    SYSTEM_NOT_READY("系统暂时不可用，请稍后再试.","00004"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR("未知错误", "00005"),
    //登录相关
    LOGIN_FAIL("账号或密码错误","10001"),
    NOT_LOGIN("用户未登录，请登录后重试","10002"),
    LOGIN_INVALID("用户登录失效，请重新登录","10003"),
    ADD_USER_ACCOUNT_INFO_FAIL("添加用户账号信息失败","10004"),
    //用户管理相关
    USER_AUTHOR_FAIL("用户赋权失败,该组织机构暂未绑定角色","20001"),
    UPDATE_ORG_AUTH_FAIL("调整组织机构权限失败,该组织机构暂未绑定角色","20002"),
    ;
    private String msg;
    private String code;
}
