package cn.honorsgc.honorv2.community.exception;

public class CommunityException extends Exception{
    public CommunityException() {
    }

    public CommunityException(String message) {
        super(message);
    }

    public CommunityException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunityException(Throwable cause) {
        super(cause);
    }
}
