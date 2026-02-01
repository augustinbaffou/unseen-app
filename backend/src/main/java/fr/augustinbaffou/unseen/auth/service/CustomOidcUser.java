package fr.augustinbaffou.unseen.auth.service;

import fr.augustinbaffou.unseen.auth.entity.User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;

public class CustomOidcUser extends CustomOAuth2User implements OidcUser {
    private final OidcUser oidcUser;

    public CustomOidcUser(OidcUser oidcUser, User user) {
        super(oidcUser, user);
        this.oidcUser = oidcUser;
    }

    @Override
    public Map<String, Object> getClaims() { return oidcUser.getClaims(); }

    @Override
    public OidcUserInfo getUserInfo() { return oidcUser.getUserInfo(); }

    @Override
    public OidcIdToken getIdToken() { return oidcUser.getIdToken(); }
}