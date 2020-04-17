package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.WxshopApplication;
import com.dahuntun.wxshop.entity.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static com.dahuntun.wxshop.service.TelVerificationServiceTest.VALID_PARAMETER;
import static com.dahuntun.wxshop.service.TelVerificationServiceTest.VALID_PARAMETER_CODE;
import static java.net.HttpURLConnection.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WxshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    private static class TestHttpResponse{
        int code;
        String body;
        Map<String, List<String>> headers;

        TestHttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }
    }

    private TestHttpResponse testHttpResponse(String apiName, boolean isGet, Object requestBody, String cookie) throws JsonProcessingException {
        HttpRequest httpRequest = isGet ? HttpRequest.get(getUrl(apiName)) : HttpRequest.post(getUrl(apiName));
        if (cookie != null) {
            httpRequest.header("Cookie", cookie);
        }
        httpRequest.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE);
        if (requestBody != null) {
            httpRequest.send(objectMapper.writeValueAsString(requestBody));
        }
        return new TestHttpResponse(httpRequest.code(), httpRequest.body(), httpRequest.headers());
    }

    @Test
    public void loginLogoutTest() throws JsonProcessingException {

        //最开始默认情况下，访问/api/status 处于未登录状态
        String statusResponse = testHttpResponse("/api/status", true, null, null).body;

        LoginResponse response = objectMapper.readValue(statusResponse, LoginResponse.class);

        Assertions.assertFalse(response.isLogin());

        //发送验证码
        int responseCode = testHttpResponse("/api/code", false, VALID_PARAMETER, null).code;

        Assertions.assertEquals(HTTP_OK, responseCode);

        //带着验证码进行登录 得到Cookie
        Map<String, List<String>> responseHeaders =
                testHttpResponse("/api/login", false, VALID_PARAMETER_CODE, null).headers;


        List<String> setCookie = responseHeaders.get("Set-Cookie");

        Assertions.assertNotNull(setCookie);

        String sessionId = getSessionIdFromSetCookie(setCookie.stream().filter(cookie->cookie.contains("JSESSIONID"))
                .findFirst()
                .get());

        //带着Cookie访问/api/status 应该处于登录状态
        statusResponse = testHttpResponse("/api/status", true, null, sessionId).body;

        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertEquals(VALID_PARAMETER.getTel(), response.getUser().getTel());

        //调用/api/logout 登出
        //注意！登出也需要带Cookie
        testHttpResponse("/api/logout", false, null, sessionId);

        //再次带着Cookie访问/api/status 恢复成未登录状态
        statusResponse = testHttpResponse("/api/status", true, null, sessionId).body;

        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

    private String getSessionIdFromSetCookie(String setCookie) {
        //JSESSIONID=315cbe63-1d15-4ee5-9622-81ba2c3c5962; Path=/; HttpOnly; SameSite=lax -> JSESSIONID=315cbe63-1d15-4ee5-9622-81ba2c3c5962
        int semicolonIndex = setCookie.indexOf(";");

        return setCookie.substring(0, semicolonIndex);

    }

    @Test
    public void returnHttpOKWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(VALID_PARAMETER))
                .code();
        Assertions.assertEquals(HTTP_OK, responseCode);
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.EMPTY_TEL))
                .code();
        Assertions.assertEquals(HTTP_BAD_REQUEST, responseCode);
    }

    @Test
    public void returnUnauthorizedIfNotLogin() {
        int responseCode = HttpRequest.post(getUrl("/api/any"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .code();
        Assertions.assertEquals(HTTP_UNAUTHORIZED, responseCode);
    }

    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

}
