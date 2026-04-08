package com.starter.app.security;

import com.starter.app.modules.user.User;
import com.starter.app.modules.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String[] PUBLIC_PATHS = {
    "/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health"
  };

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      String userId = jwtTokenProvider.getUserIdFromToken(token);
      User user = userRepository.findById(UUID.fromString(userId)).orElse(null);

      if (user == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
        return;
      }

      if (!user.isEnabled()) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account is disabled");
        return;
      }

      UserPrincipal userPrincipal = UserPrincipal.fromUser(user);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userPrincipal, null, userPrincipal.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    for (String publicPath : PUBLIC_PATHS) {
      if (pathMatcher.match(publicPath, path)) {
        return true;
      }
    }
    return false;
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
