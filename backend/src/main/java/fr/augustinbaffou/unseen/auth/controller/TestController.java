package fr.augustinbaffou.unseen.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public/home")
    public String publicPage(@AuthenticationPrincipal OAuth2User principal) {
        return "page public";
    }

    @GetMapping("/admin/test")
    public String adminPage(@AuthenticationPrincipal OAuth2User principal) {
        return "page admin";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        return "page d'acceuil";
    }
}
