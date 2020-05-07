package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.entity.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static com.dahuntun.wxshop.service.TelVerificationServiceTest.VALID_PARAMETER;
import static com.dahuntun.wxshop.service.TelVerificationServiceTest.VALID_PARAMETER_CODE;
import static java.net.HttpURLConnection.HTTP_OK;

public class AbstractIntegrationTest {
    @Autowired
    Environment environment;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @BeforeEach
    public void initDatabase() {
        //在每个测试开始前执行一次flyway:clean flyway:migrate
        ClassicConfiguration config = new ClassicConfiguration();
        config.setDataSource(datasourceUrl, datasourceUsername, datasourcePassword);
        Flyway flyway = new Flyway(config);
        flyway.clean();
        flyway.migrate();
    }

    public static ObjectMapper objectMapper = new ObjectMapper();

    public String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    public UserLoginResponse loginAndGetCookie() throws JsonProcessingException {
        //最开始默认情况下，访问/api/status 处于未登录状态
        String statusResponse = testHttpRequest("/api/status", "GET", null, null).body;

        LoginResponse statusResponseData = objectMapper.readValue(statusResponse, LoginResponse.class);

        Assertions.assertFalse(statusResponseData.isLogin());

        //发送验证码
        int responseCode = testHttpRequest("/api/code", "POST", VALID_PARAMETER, null).code;

        Assertions.assertEquals(HTTP_OK, responseCode);

        //带着验证码进行登录 得到Cookie
        TestHttpResponse loginResponse = testHttpRequest("/api/login", "POST", VALID_PARAMETER_CODE, null);

        List<String> setCookie = loginResponse.headers.get("Set-Cookie");
        String cookie = getSessionIdFromSetCookie(setCookie.stream().filter(c->c.contains("JSESSIONID"))
                .findFirst()
                .get());

        statusResponse = testHttpRequest("/api/status", "GET", null, cookie).body;
        statusResponseData = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertNotNull(setCookie);

        return new UserLoginResponse(cookie, statusResponseData.getUser());

    }

    private String getSessionIdFromSetCookie(String setCookie) {
        //JSESSIONID=315cbe63-1d15-4ee5-9622-81ba2c3c5962; Path=/; HttpOnly; SameSite=lax -> JSESSIONID=315cbe63-1d15-4ee5-9622-81ba2c3c5962
        int semicolonIndex = setCookie.indexOf(";");

        return setCookie.substring(0, semicolonIndex);

    }

    public static class UserLoginResponse {
        String cookie;
        User user;

        public UserLoginResponse(String cookie, User user) {
            this.cookie = cookie;
            this.user = user;
        }
    }

    public static class TestHttpResponse{
        int code;
        String body;
        Map<String, List<String>> headers;

        TestHttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.code = code;
            this.body = body;
            this.headers = headers;
        }

        public <T> T asJsonObject(TypeReference<T> typeReference) throws JsonProcessingException {
            return objectMapper.readValue(body, typeReference);
        }
    }

    public TestHttpResponse testHttpRequest(String apiName, String httpMethod, Object requestBody, String cookie) throws JsonProcessingException {
        HttpRequest httpRequest = new HttpRequest(getUrl(apiName), httpMethod);
        if (cookie != null) {
            httpRequest.header("Cookie", cookie);
        }
        httpRequest.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE);
        if (requestBody != null) {
            httpRequest.send(objectMapper.writeValueAsString(requestBody));
        }
        return new TestHttpResponse(httpRequest.code(), httpRequest.body(), httpRequest.headers());
    }
}
