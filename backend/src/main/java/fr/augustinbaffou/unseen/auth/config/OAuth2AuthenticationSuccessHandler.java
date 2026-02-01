
package fr.augustinbaffou.unseen.auth.config;

import fr.augustinbaffou.unseen.auth.service.CustomOAuth2User;
import fr.augustinbaffou.unseen.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();

        String token = jwtService.generateToken(oauth2User.getUser());

        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("token", token);
        tokenResponse.put("type", "Bearer");
        tokenResponse.put("expiresIn", jwtService.getExpirationTime());
        tokenResponse.put("user", Map.of(
                "id", oauth2User.getUser().getId(),
                "name", oauth2User.getUser().getName(),
                "email", oauth2User.getUser().getEmail(),
                "role", oauth2User.getUser().getRole()
        ));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}