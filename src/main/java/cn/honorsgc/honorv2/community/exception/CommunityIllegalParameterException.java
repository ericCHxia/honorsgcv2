package cn.honorsgc.honorv2.community.exception;

public class CommunityIllegalParameterException extends CommunityException {
    public CommunityIllegalParameterException() {
        super("共同体参数错误");
    }

    public CommunityIllegalParameterException(String message) {
        super(message);
    }
}
