package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpAuthenticationEntryPoint httpAuthenticationEntryPoint;
    @Autowired
    private SimpleAccessDeniedHandle accessDeniedHandle;
    @Autowired
    private JWTHelper jwtHelper;

    @Resource
    private DataSource dataSource;
    private static final String[] AUTH_WHITELIST = {
            "/login",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/expiredJwtException",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/upload/**",
            "/image/**",
            "/hduhelper",
            "/logout"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()  // ??????????????????????????????
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandle) // ??????????????????????????????
                .authenticationEntryPoint(httpAuthenticationEntryPoint)
                .and()
                .addFilter(new JWTLoginFilter(authenticationManager(), userService,jwtHelper))
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), userService, jwtHelper))
                .logout() // ?????????????????????logout???????????????????????????????????????
                .logoutUrl("/logout")
                .permitAll();
    }
    @Bean
    protected PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //tokenRepository.setCreateTableOnStartup(true); // ??????????????????????????????????????????
        return tokenRepository;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        // ?????????????????????????????????
        auth.authenticationProvider(new CustomAuthenticationProvider(userService));
    }
}
