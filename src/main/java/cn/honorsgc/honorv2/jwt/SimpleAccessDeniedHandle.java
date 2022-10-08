package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.ResultInfo;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("SimpleAccessDeniedHandle")
@Slf4j
public class SimpleAccessDeniedHandle implements AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException {
        log.info("Error Handle");
        PrintWriter out = httpServletResponse.getWriter();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        out.print(objectMapper.writeValueAsString(new GlobalResponseEntity<>(ResultInfo.unauthorized("Token Failed"))));
        out.flush();
    }
}
