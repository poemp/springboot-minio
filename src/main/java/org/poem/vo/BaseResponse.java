package org.poem.vo;

import lombok.Data;
import org.poem.config.SystemEnums;


@Data
public class BaseResponse<T> {
    /**
     * 业务状态码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     *有效数据
     */
    private T data;

    public BaseResponse() {
        this.code = SystemEnums.SUCCESS.getCode();
        this.msg = SystemEnums.SUCCESS.getMsg();
    }

    public BaseResponse(T data) {
        this.code = SystemEnums.SUCCESS.getCode();
        this.msg = SystemEnums.SUCCESS.getMsg();
        this.data = data;
    }

    public BaseResponse(SystemEnums enums) {
        this.code = enums.getCode();
        this.msg = enums.getMsg();
    }

    public BaseResponse(SystemEnums enums, T data) {
        this.code = enums.getCode();
        this.msg = enums.getMsg();
        this.data = data;
    }

    public BaseResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public BaseResponse(SystemEnums enums, String msg, T data) {
        this.code = enums.getCode();
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
