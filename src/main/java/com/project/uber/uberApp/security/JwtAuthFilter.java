package com.project.uber.uberApp.security;


import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

// ALL Api Calls passes through this Filter

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            // getting Bearer Token From header "Authorization" contains algo info, token, userinfo
            final String requestTokenHeader = request.getHeader("Authorization");

            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer"))
            {
                filterChain.doFilter(request,response);
                return;
            }

            // token contains Algo info, Token, userInfo
            String token = requestTokenHeader.split("Bearer ")[1];

            //call function of JWT service, where we get userId from Headers by Token
            Long userId = jwtService.getUserIdFromToken(token);

            //check if userId is not null and securityContextHolder not contains any authentication
            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                User user = userService.getUserById(userId);

                //Create principle, Username Password Authentication Object (username, password, rules)
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                //add some details in authentication token
                authenticationToken.setDetails(
                        //stores ip address, session related info
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //now put this user in Spring Security Context Holder for further Authentication APi
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            //go to next filter
            filterChain.doFilter(request,response);
        }catch (Exception ex)
        {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
