package cn.honorsgc.honorv2.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    final private Logger logger= LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final UserDetailsService userDetailsService;


    public CustomAuthenticationProvider(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    /**
     *执行与以下合同相同的身份验证
     * {@link org.springframework.security.authentication.AuthenticationManager＃authenticate（Authentication）}
     *。
     *
     * @param authentication 身份验证请求对象。
     *
     * @返回包含凭证的经过完全认证的对象。 可能会回来
     * <code> null </ code>（如果<code> AuthenticationProvider </ code>无法支持）
     * 对传递的<code> Authentication </ code>对象的身份验证。 在这种情况下，
     * 支持所提供的下一个<code> AuthenticationProvider </ code>
     * 将尝试<code> Authentication </ code>类。
     *
     * @throws AuthenticationException 如果身份验证失败。
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证的用户名 & 密码
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 认证逻辑
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);

        if (null != userDetails) {
            if (password.equals(userDetails.getPassword())) {
                return new UsernamePasswordAuthenticationToken(name, password, userDetails.getAuthorities());
            } else {
                throw new BadCredentialsException("密码错误");
            }
        } else {
            throw new UsernameNotFoundException("用户不存在");
        }
    }

    /**
     * 是否可以提供输入类型的认证服务
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
