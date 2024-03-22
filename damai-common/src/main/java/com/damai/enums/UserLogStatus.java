package com.damai.enums;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: 用户登录状态
 * @author: 阿宽不是程序员
 **/
public enum UserLogStatus {
    /**
     * 用户登录状态
     * */
    IN(1,"登录"),
    
    OUT(0,"退出")
    ;

    private Integer code;

    private String msg;

    UserLogStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg == null ? "" : this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String getMsg(Integer code) {
        for (UserLogStatus re : UserLogStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re.msg;
            }
        }
        return "";
    }

    public static UserLogStatus getRc(Integer code) {
        for (UserLogStatus re : UserLogStatus.values()) {
            if (re.code.intValue() == code.intValue()) {
                return re;
            }
        }
        return null;
    }
}
