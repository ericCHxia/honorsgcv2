package cn.honorsgc.honorv2;

import lombok.Getter;

public class ResultInfo {
    @Getter
    private final int code;
    @Getter
    private final String message;
    public static ResultInfo unauthorized(String message){
        return new ResultInfo(502, message);
    }
    public static ResultInfo authorizedFailed(String message){
        return new ResultInfo(1000, message);
    }

    public ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    static public ResultInfo ok(String message) {
        return new ResultInfo(0,message);
    }
    static public ResultInfo ok() {
        return new ResultInfo(0,"request successfully");
    }
}
