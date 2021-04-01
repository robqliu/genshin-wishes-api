package com.uf.genshinwishes.security;

import com.uf.genshinwishes.model.User;
import com.uf.genshinwishes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        Map attributes = oAuth2User.getAttributes();

        Object email = attributes.get("email");

        if (email == null) throw new OAuth2AuthenticationException(new OAuth2Error("email-permission-required"));

        User user = retrieveOrInsertUser((String) email);

        return UserPrincipal.create(user);
    }

    private User retrieveOrInsertUser(String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            user = userService.insertUser(email);
        } else if (user.getKey() == null) {
            userService.createKey(user);
        }

        return user;
    }
}
