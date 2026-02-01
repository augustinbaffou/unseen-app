package fr.augustinbaffou.unseen.auth.service;

import fr.augustinbaffou.unseen.auth.entity.User;
import fr.augustinbaffou.unseen.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OidcUserService oidcUserService = new OidcUserService();

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(oauth2User);
    }

    @Transactional
    public OidcUser loadOidcUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);
        return (OidcUser) processOAuth2User(oidcUser);
    }

    public OAuth2User processOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        String picture = oauth2User.getAttribute("picture"); // Photo de profil Google

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User(email, name, googleId, picture);
                    return userRepository.save(newUser);
                });

        // Mettre à jour les informations si elles ont changé
        boolean updated = false;

        if (!name.equals(user.getName())) {
            user.setName(name);
            updated = true;
        }

        if (picture != null && !picture.equals(user.getPicture())) {
            user.setPicture(picture);
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
        }

        if (oauth2User instanceof OidcUser oidcUser) {
            return new CustomOidcUser(oidcUser, user);
        }

        return new CustomOAuth2User(oauth2User, user);
    }
}