package cn.honorsgc.honorv2.user.exception;

public class UserIllegalParameterException extends UserException{
    public UserIllegalParameterException(){super("用户参数错误");}
    public UserIllegalParameterException(String message){super(message);}
}
