package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.ResultInfo;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserService;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    final private Logger logger= LoggerFactory.getLogger(JWTLoginFilter.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public JWTLoginFilter(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    /**
     * 尝试身份认证(接收并解析用户凭证)
     * @param req
     * @param res
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(req.getInputStream(), User.class);
            logger.info(user.getPassword());
            UsernamePasswordAuthenticationToken usernameToken =  new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
            return authenticationManager.authenticate(usernameToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 认证成功(用户成功登录后，这个方法会被调用，我们在这个方法里生成token)
     * @param request
     * @param response
     * @param chain
     * @param auth
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        // builder the token
        String token;
        try {
            User user = userService.loadUserByUsername(auth.getName());

            // 生成token start
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            // 设置签发时间
            calendar.setTime(new Date());
            // 设置过期时间
            calendar.add(Calendar.MINUTE, 5);// 5分钟
            Date time = calendar.getTime();
            token = Jwts.builder()
                    .setSubject(user.getId().toString())
                    .setIssuedAt(now)//签发时间
                    .setExpiration(time)//过期时间
                    .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY) //采用什么算法是可以自己选择的，不一定非要采用HS512
                    .compact();
            // 生成token end

            // 登录成功后，返回token到header里面
            /*response.addHeader("Authorization", "Bearer " + token);*/

            // 登录成功后，返回token到body里面
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            response.getWriter().write(JSON.toJSONString(new GlobalResponseEntity<>(ResultInfo.ok(),resultMap)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
