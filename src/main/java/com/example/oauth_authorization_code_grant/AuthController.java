package com.example.oauth_authorization_code_grant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class AuthController {

    String clientId = System.getenv("GOOGLE_CLIENT_ID");
    String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
    String authEndpoint = "https://accounts.google.com/o/oauth2/v2/auth";
    String tokenEndpoint = "https://oauth2.googleapis.com/token";
    String redirectUri = "http://localhost:8080/callback";

    RestClient defaultClient = RestClient.create();

    @GetMapping("/auth/login")
    public RedirectView authLogin() {
        return redirectToGoogleAuthService();
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, @RequestParam String state) {
        String accessToken = exchangeAuthCodeForAccessToken(code);
        String userInfo = getUserProfileInfo(accessToken);
        return userInfo;
        // return "callback called \nheres your authorization code: " + code + "\nand
        // your code challenge: "+ state;
    }

    public String exchangeAuthCodeForAccessToken(String code) {
        String grantType = "authorization_code";
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(tokenEndpoint);
        uri.queryParam("grant_type", grantType);
        uri.queryParam("redirectUri", redirectUri);
        uri.queryParam("client_id", clientId);
        uri.queryParam("client_secret", clientSecret);
        uri.queryParam("code", code);
        String response = defaultClient.post().uri(uri.toUriString()).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve().body(String.class);
        System.out.println(response);
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        System.out.println("\n access_token = " + jsonObject.get("access_token").getAsString());
        return jsonObject.get("access_token").getAsString();
    }

    public RedirectView redirectToGoogleAuthService() {
        // https://example-app.com/pkce
        // base64urlencode(sha256(input))
        // code verifier = "f2ad551c7dac4b5ddde83d694785e3c4a7a008b3537d1f2bd0e95569";
        String codeChallenge = "GdWLIPxx_WCf91jMt2jNH0eSenz2bhW7HuLhT_BpSDM";
        String responseType = "code";
        String scope = "https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile";
        String accessType = "offline";
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(authEndpoint);
        uri.queryParam("response_type", responseType);
        uri.queryParam("client_id", clientId);
        uri.queryParam("scope", scope);
        uri.queryParam("redirect_uri", redirectUri);
        uri.queryParam("state", codeChallenge);
        uri.queryParam("access_type", accessType);
        System.out.println(uri.toUriString());
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(uri.toUriString());
        return redirectView;
    }

    public String getUserProfileInfo(String accessToken) {
        String uri = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
        String response = defaultClient.get().uri(uri).header("Authorization", "Bearer " + accessToken).retrieve()
                .body(String.class);
        return response;
    }

}
