package com.dahuntun.wxshop.service;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroRealmService extends AuthorizingRealm {
    private final  VerificationCodeCheckService verificationCodeCheckService;

    @Autowired
    public ShiroRealmService(VerificationCodeCheckService verificationCodeCheckService) {
        this.verificationCodeCheckService = verificationCodeCheckService;

        this.setCredentialsMatcher((authenticationToken, authenticationInfo) -> new String((char[]) authenticationToken.getCredentials()).equals(authenticationInfo.getCredentials()));
    }

    /**
     * 权限验证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 身份验证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String tel = (String) authenticationToken.getPrincipal();
        String correctCode = verificationCodeCheckService.getCorrectCode(tel);
        System.out.println("tel:" + tel);
        System.out.println("correctCode:" + correctCode);
        return new SimpleAuthenticationInfo(tel, correctCode, getName());
    }
}
