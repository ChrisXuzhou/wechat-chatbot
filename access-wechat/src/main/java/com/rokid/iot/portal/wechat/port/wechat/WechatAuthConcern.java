package com.rokid.iot.portal.wechat.port.wechat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.rokid.iot.portal.wechat.inf.ByteHelper;
import com.rokid.iot.portal.wechat.inf.wxtools.AesException;
import com.rokid.iot.portal.wechat.inf.wxtools.WXBizMsgCrypt;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public interface WechatAuthConcern {

    default Auth checkSignAndGetAuth(HttpServletRequest request,
                                     SecretPackage secretPackage) {

        try {
            request.setCharacterEncoding("UTF-8");

            Auth auth = Auth.builder()
                    .secretPackage(secretPackage)
                    .signature(request.getParameter("signature"))
                    .timestamp(request.getParameter("timestamp"))
                    .nonce(request.getParameter("nonce"))
                    .build()
                    .legal();

            Preconditions.checkArgument(
                    auth.isValidSigned(),
                    "signature check error[" + auth.inString()
            );

            return auth;

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    default AuthAndRequestBody authCheckAndDecrypt(
            HttpServletRequest request,
            SecretPackage secretPackage) {

        try {
            request.setCharacterEncoding("UTF-8");
            Auth auth = checkSignAndGetAuth(request, secretPackage);

            auth.legal();
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(
                                    request.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String msgSignature = request.getParameter("msg_signature");

            return AuthAndRequestBody.builder()
                    .auth(auth)
                    .requestBody(
                            auth.decrypt(msgSignature, sb.toString()))
                    .build()
                    .legal();

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Getter
    @Builder
    class AuthAndRequestBody {

        private Auth auth;
        private String requestBody;

        AuthAndRequestBody legal() {
            Preconditions.checkArgument(Objects.nonNull(auth));
            auth.legal();

            Preconditions.checkArgument(StringUtils.isNotBlank(requestBody));
            return this;
        }
    }

    @Getter
    @Builder
    class Auth {
        private SecretPackage secretPackage;

        private String signature;
        private String timestamp;
        private String nonce;

        String inString() {
            return "signature:" + signature +
                    "timestamp:" + timestamp +
                    "nonce:" + nonce;
        }


        Auth legal() {
            Preconditions.checkArgument(Objects.nonNull(secretPackage));
            secretPackage.legal();

            Preconditions.checkArgument(StringUtils.isNotBlank(signature), "SIGNATURE_IS_EMPTY");
            Preconditions.checkArgument(StringUtils.isNotBlank(timestamp), "TIMESTAMP_IS_EMPTY");
            Preconditions.checkArgument(StringUtils.isNotBlank(nonce), "NONCE_IS_EMPTY");

            return this;
        }

        boolean isValidSigned() throws NoSuchAlgorithmException {

            List<String> infoList =
                    Lists.newArrayList(
                            getSecretPackage().legal().getToken(),
                            timestamp,
                            nonce);

            infoList.sort(Comparator.naturalOrder());
            String content = infoList.stream().map(Object::toString).reduce((t, u) -> t + u).orElse("");

            String signed =
                    ByteHelper.byteToStr(
                            MessageDigest.getInstance("SHA-1").digest(content.getBytes()));

            String inUpperL = signed.toUpperCase();
            String inUpperR = getSignature().toUpperCase();

            return StringUtils.equals(inUpperL, inUpperR);
        }

        public String decrypt(String msgSignature, String encrypted) throws AesException {
            return getSecretPackage().ofWechatCrypt()
                    .decryptMsg(msgSignature, timestamp, nonce, encrypted);
        }


        public String encrypt(String content) throws AesException {
            return getSecretPackage().ofWechatCrypt()
                    .encryptMsg(content, timestamp, nonce);
        }
    }

    @Data
    @Service
    class SecretPackage {

        @Value("${wechat.auth.appId}")
        private String appId;
        @Value("${wechat.auth.token}")
        private String token;
        @Value("${wechat.auth.aesKey}")
        private String encodingAesKey;

        SecretPackage legal() {
            Preconditions.checkArgument(StringUtils.isNotBlank(getAppId()));
            Preconditions.checkArgument(StringUtils.isNotBlank(getToken()));
            Preconditions.checkArgument(StringUtils.isNotBlank(getEncodingAesKey()));

            return this;
        }

        WXBizMsgCrypt ofWechatCrypt() throws AesException {
            return new WXBizMsgCrypt(
                    legal().getToken(),
                    getEncodingAesKey(),
                    getAppId()
            );
        }

    }

}
