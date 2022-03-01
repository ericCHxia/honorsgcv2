package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.ResultInfo;
import cn.honorsgc.honorv2.core.GlobalException;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("SimpleAccessDeniedHandle")
public class SimpleAccessDeniedHandle implements AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;

    final private Logger logger = LoggerFactory.getLogger(SimpleAccessDeniedHandle.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException, ServletException {
        logger.info("Error Handle");
        PrintWriter out = httpServletResponse.getWriter();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        out.print(objectMapper.writeValueAsString(new GlobalResponseEntity<>(ResultInfo.unauthorized("Token Failed"))));
        out.flush();
    }
}
