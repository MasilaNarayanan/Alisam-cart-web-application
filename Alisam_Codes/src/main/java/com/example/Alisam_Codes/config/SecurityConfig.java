package com.example.Alisam_Codes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.example.Alisam_Codes.models.User;
import com.example.Alisam_Codes.services.UserService;
import jakarta.servlet.http.HttpSession;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Keeping CSRF disabled to match previous native session logic
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Standard routes continue to rely on manual session interceptors/checks
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(this.oAuth2UserService()))
                .successHandler(successHandler())
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            );
        return http.build();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oAuth2User = delegate.loadUser(request);
            return oAuth2User;
        };
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            User user = userService.findByEmail(email).orElse(null);
            
            // If user doesn't exist, register them via Google seamlessly
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPassword("OAUTH2_LOGGED_IN_NO_PASSWORD"); // Dummy password since they log in via Google
                user.setRole("USER");
                userService.registerUser(user);
            }

            // Bind the authenticated user to the standard session variable expected by the UI templates
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);

            response.sendRedirect("/profile");
        };
    }
}
