package com.hpuswl.springboot.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.hpuswl.springboot.authentication.SecurityAuthenticationFailureHandler;
import com.hpuswl.springboot.authentication.SecurityAuthenticationSuccessHandler;
import com.hpuswl.springboot.config.authentication.MyInvalidSessionStrategy;
import com.hpuswl.springboot.filter.JwtFilter;
import com.hpuswl.springboot.filter.JwtLoginFilter;
import com.hpuswl.springboot.filter.MyAuthenticationFailureHandler;
import com.hpuswl.springboot.filter.VerificationCodeFilter;
import com.hpuswl.springboot.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启security注解
public class WebSecurityConfig<S extends Session> extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private SpringSessionBackedSessionRegistry redisSessionRegistry;

//    @Autowired
//    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> myWebAuthenticationDetailsSource;

//    @Autowired
//    private AuthenticationProvider authenticationProvider;

    @Autowired
    private MyUserDetailService userDetailService;


    @Autowired
    private DataSource dataSource;

    /* https://my.oschina.net/u/3857854/blog/3010262
    加入下面测试失败
    @Autowired
    private FindByIndexNameSessionRepository<S> sessionRepository;

    @Bean
    public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<S>(sessionRepository);
    }*/
    /**
     * 注册bean sessionRegistry
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 应用 AuthenticationProvider
        auth.authenticationProvider(authenticationProvider);
    }*/

    @Bean
    public Producer captcha(){
        // 配置图形验证码的基础参数
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", "150");
        // 图片高度
        properties.setProperty("kaptcha.image.height", "50");
        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // session key
        properties.setProperty("kaptcha.session.key", "code");
        // 字符集
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        // 使用默认的图形验证码实现，当然也可以自定义实现
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
         return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }

    // httpSession 的事件监听， 改用 session 提供的会话注册表
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /*@Bean
    @Override
    protected UserDetailsService userDetailsService() {
        *//*InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username("user").password("123").roles("USER").build());
        manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("123").roles("USER", "ADMIN").build());
        return manager;*//*

        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        if(!manager.userExists("user")){
            manager.createUser(User.withUsername("user").password(passwordEncoder().encode("123")).roles("USER").build());
        }
        if(!manager.userExists("admin")){
            manager.createUser(User.withUsername("admin").password(passwordEncoder().encode("123")).roles("USER", "ADMIN").build());
        }
        if(!manager.userExists("demo")){
            manager.createUser(User.withUsername("demo").password("123").roles("TEST").build());
        }

        return manager;
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        http.authorizeRequests()
                    .antMatchers("/admin/api/**").hasRole("ADMIN")
                    .antMatchers("/user/api/**").hasRole("USER")
                    // 开放 captcha.jpg 的访问权限
                    .antMatchers("/app/api/**", "/captcha.jpg").permitAll()
                    .anyRequest().authenticated()
                    .and()
//                .csrf().disable()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .formLogin()
//                    .authenticationDetailsSource(myWebAuthenticationDetailsSource)
//                    .loginPage("/login")
                    .loginProcessingUrl("/auth/login")
                    .permitAll()
//                .failureHandler(new MyAuthenticationFailureHandler())
                .and()
                // 增加自动登陆功能，默认为简单的散列加密
                .rememberMe().userDetailsService(userDetailService)
                // 1. 散列加密方案
                .key("solvaysphere")
                // 2. 持久化令牌方案
                .tokenRepository(jdbcTokenRepository)
                // 7天有效期
                .tokenValiditySeconds(60 * 60 * 24 * 7)
//                .and()
//                .sessionManagement()
//                .maximumSessions(1)
                   /* .loginPage("/login")
                    .permitAll()
                    .loginProcessingUrl("/login")
                    .successHandler(new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            response.setContentType("application/json;charset=UTF-8");
                            PrintWriter out = response.getWriter();
                            out.write("{\"error_code\":\"0\",\"message\":\"欢迎登陆系统\"}");
                        }
                    })
                    .failureHandler(new AuthenticationFailureHandler() {
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            PrintWriter out = response.getWriter();
                            out.write("{\"error_code\":\"401\",\"message\":\""+exception.getMessage()+"\"}");
                        }
                    })*/
                    .and()
                .logout()
                .logoutUrl("/logout")
                // 注销成功，重定向到该路径下
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .and()
                .sessionManagement()
                // 最大会话数设置为1
                .maximumSessions(1)
                // 阻止新会话登录，默认为 false
                .maxSessionsPreventsLogin(true)
                // 使用 session 提供的会话注册表
//                .sessionRegistry(sessionRegistry())
//                .invalidSessionStrategy(new MyInvalidSessionStrategy())

        ;

        /*ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
                expressionInterceptUrlRegistry = http.authorizeRequests();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl
                authorizedUrl = expressionInterceptUrlRegistry.anyRequest();
        authorizedUrl.authenticated();

        FormLoginConfigurer<HttpSecurity> httpSecurityFormLoginConfigurer = http.formLogin();
        httpSecurityFormLoginConfigurer.loginPage("/login");
        httpSecurityFormLoginConfigurer.permitAll();*/

        // 将过滤器添加在UsernamePasswordAuthenticationFilter之前
//        http.addFilterBefore(new VerificationCodeFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(new JwtLoginFilter("/auth/login",authenticationManager()),UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new JwtFilter(),UsernamePasswordAuthenticationFilter.class)
                ;

    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }*/
}
