package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public enum JWTErrorEnum implements ErrorEnum {
    expired(40100, "Token过期"),
    unsupported(40101,"Token签名失败"),
    signatureException(40102,"Token格式错误"),
    illegalArgument(40103,"Token非法参数异常"),
    accessDenied(40104,"Token非法参数异常"),
    malformed(40105,"Token没有被正确构造")
    ;
    private final Integer errorCode;
    private final String errorMsg;

    JWTErrorEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public GlobalResponseEntity<String> responseEntity(){
        return new GlobalResponseEntity<>(errorCode,errorMsg);
    }

    public int getStatus() {
        return errorCode/100;
    }

    public void setupResponse(HttpServletResponse response) throws IOException {
        response.setStatus(getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(new ObjectMapper().writeValueAsString(responseEntity()));
        out.flush();
    }
}
