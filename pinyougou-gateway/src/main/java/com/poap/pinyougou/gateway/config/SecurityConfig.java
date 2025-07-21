package com.poap.pinyougou.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebFluxSecurity // 【注意】在WebFlux项目(Gateway基于它)中，要用这个注解
@EnableReactiveMethodSecurity // 【修正】使用@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    // @Autowired
    // private JwtSecurityContextRepository jwtSecurityContextRepository;

    /**
     * 【已修正】WebFlux环境下的安全规则链配置
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // 禁用CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 【推荐】直接在这里配置CORS
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // 禁用HttpBasic
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 禁用表单登录
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll())
                // .authorizeExchange(exchange -> exchange
                // .pathMatchers("/api/auth/login").permitAll() // 登录接口公开
                // .pathMatchers(HttpMethod.OPTIONS).permitAll() // 预检请求公开
                // .anyExchange().authenticated() // 其他所有请求都需要认证
                // )
                // .securityContextRepository(jwtSecurityContextRepository)
                .build();
    }

    /**
     * 【修正】CORS配置源的Bean，现在直接在安全链中使用
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> originsList = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(originsList);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
