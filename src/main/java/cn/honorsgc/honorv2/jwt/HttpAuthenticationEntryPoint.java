package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("HttpAuthenticationEntryPoint")
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {
    final private Logger logger= LoggerFactory.getLogger(AuthenticationEntryPoint.class);
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        out.print(objectMapper.writeValueAsString(new GlobalResponseEntity<>(1000, authException.getLocalizedMessage())));
        out.flush();
    }

}