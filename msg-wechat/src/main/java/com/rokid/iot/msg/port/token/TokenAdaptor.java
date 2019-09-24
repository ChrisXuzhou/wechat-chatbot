package com.rokid.iot.msg.port.token;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.msg.port.MsgHttpConcern;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.thirdoauth.ThirdOauthPartyGrpc;
import com.rokid.thirdoauth.ThirdOauthPartyOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class TokenAdaptor implements MsgHttpConcern {

    private String appId = "wx85851e71de3ae99e";
    private String secret = "3f90b65a6e1dfbf6a6e90fce85a15fc5";

    private String url = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取微信公众号的token
     *
     * @return 【微信公众号accessToken】
     */

    public String of() {

        //return ofForeignToken();

        Map<String, String> params = Maps.newHashMap();
        params.put("grant_type", "client_credential");
        params.put("appid", appId);
        params.put("secret", secret);
        Map<String, Object> resp = get(url, params);

        String accessToken = (String) resp.get("access_token");
        Preconditions.checkArgument(
                StringUtils.isNotBlank(accessToken),
                "access token not found!" + JsonHelper.toJson(resp));

        return accessToken;

    }

    @Value("${rokid.oauth.token.host}")
    private String authHost;
    @Value("${rokid.oauth.token.port}")
    private String authPort;

    /**
     * 可能会存在失效的情况，失效后【重试】
     *
     * @return ACCESS_TOKEN
     */
    public String ofForeignToken() {

        Tracer tracer = Tracer.of(log).method("TokenAdaptor.ofForeignToken");
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(
                            authHost,
                            Integer.valueOf(authPort))
                    .usePlaintext()
                    .build();

            ThirdOauthPartyGrpc.ThirdOauthPartyBlockingStub blockingStub = ThirdOauthPartyGrpc.newBlockingStub(managedChannel);

            ThirdOauthPartyOuterClass.GetWxBaseAccessTokenRequest request =
                    ThirdOauthPartyOuterClass.GetWxBaseAccessTokenRequest.newBuilder()
                            .setAppId(appId)
                            .build();

            tracer.addParam("rpcParam", request);
            ThirdOauthPartyOuterClass.GetWxBaseAccessTokenResponse response = blockingStub.getWxBaseAccessToken(request);
            tracer.result(response);

            Preconditions.checkState(response.getSuccess());

            return response.getAccessToken();

        } catch (Exception e) {
            tracer.error(e);
            throw e;
        } finally {
            tracer.print();
            if (managedChannel != null
                    && (!managedChannel.isTerminated() || !managedChannel.isShutdown())) {
                managedChannel.shutdownNow();
            }
        }

    }
}
