package com.rokid.iot.portal.wechat.port.account;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.account.RokidAccountGrpc;
import com.rokid.account.RokidAccountV2;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.wechat.domain.DialogHandler;
import com.rokid.iot.portal.share.wechat.WechatConcern;
import com.rokid.open.devicemanager.facade.DeviceManagerGrpc;
import com.rokid.open.devicemanager.facade.DeviceManagerOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class AccountAdaptor implements WechatConcern {

    @Value("${rokid.device.grpc.host}")
    private String deviceHost;
    @Value("${rokid.device.grpc.port}")
    private Integer devicePort;

    @Value("${rokid.account.grpc.host}")
    private String accountHost;
    @Value("${rokid.account.grpc.port}")
    private Integer accountPort;


    @Value("${wechat.auth.appId}")
    private String appId;

    /**
     * <p>
     * mock a Wechat User temporarily
     *
     * @param weChatUser 用户
     * @return Account 账户
     */
    public Account of(DialogHandler.User weChatUser) {
        Account account = queryAccount(weChatUser);
        if (Objects.isNull(account)) {
            throw new AccountNotFoundException(
                    JsonHelper.toJson(weChatUser));
        }
        return account;
    }


    private Account queryAccount(DialogHandler.User wechatUser) {

        Tracer tracer = Tracer.of(log, "AccountAdaptor.queryAccount", wechatUser);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(accountHost, accountPort)
                    .usePlaintext()
                    .build();

            RokidAccountGrpc.RokidAccountBlockingStub blockingStub = RokidAccountGrpc.newBlockingStub(managedChannel);

            RokidAccountV2.GetUserInfoByOpenIdRequest request =
                    RokidAccountV2.GetUserInfoByOpenIdRequest.newBuilder()
                            .setAppId(appId)
                            .setOpenId(wechatUser.getOpenId())
                            .build();

            tracer.addParam("rpcParams", request);
            RokidAccountV2.GetUserInfoByOpenIdResponse response = blockingStub.getUserInfoByOpenId(request);
            tracer.result(response);

            RokidAccountV2.StatusResponse status = response.getStatusInfo();
            Preconditions.checkArgument(Objects.nonNull(status));
            Preconditions.checkArgument(status.getSuccess(), "ACCOUNT_INFO_SERVER_ERROR");

            String masterId = response.getUserId();
            if (StringUtils.isBlank(masterId)) {
                return null;
            }

            return Account.builder().masterId(masterId)
                    .build();

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


    @Getter
    @Builder
    public static class Account {
        private String masterId;

        Account legal() {
            Preconditions.checkArgument(Objects.nonNull(getMasterId()));
            return this;
        }

        public boolean equalsWith(Account another) {
            if (Objects.isNull(another)) {
                return false;
            }
            if (StringUtils.equals(another.getMasterId(), getMasterId())) {
                return true;
            }
            return false;
        }
    }

    private void createMockDeviceIfNotExist(
            Account account,
            DialogHandler.User wechatUser) {

        account.legal();

        /*
         * 若不存在 Wechat用户 的【虚拟设备】
         */
        if (notExistMockDevice(wechatUser)) {
            createMockDevice(account, wechatUser);
        }
        Account bond = ofAccount(wechatUser);
        /*
         * 1、未绑定账户
         * 2、绑定账户 和openId查询的账户【不匹配】
         */
        if (Objects.isNull(bond)
                || !account.equalsWith(bond)) {
            bondTo(account, wechatUser);
        }
    }

    public boolean notExistMockDevice(DialogHandler.User wechatUser) {

        Tracer tracer = Tracer.of(log, "AccountAdaptor.existMockDevice", wechatUser);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(deviceHost, devicePort)
                    .usePlaintext()
                    .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            String mockedDeviceId = asDeviceId(wechatUser.getOpenId());

            DeviceManagerOuterClass.QueryDeviceListRequest request =
                    DeviceManagerOuterClass.QueryDeviceListRequest.newBuilder()
                            .setDeviceId(mockedDeviceId)
                            .build();

            DeviceManagerOuterClass.QueryDeviceListResponse response = blockingStub.queryDeviceList(request);
            tracer.result(response);
            Preconditions.checkArgument(Objects.nonNull(response), "GRpc response is null");
            int resultCode = response.getResultCode();
            Preconditions.checkArgument((0 == resultCode), "DEVICE_INFO_SERVER_ERROR");

            List<DeviceManagerOuterClass.QueryDeviceInfoOutput> deviceInfoOutputList = response.getQueryDeviceInfoOutputList();
            if (!CollectionUtils.isEmpty(deviceInfoOutputList)) {

                String deviceId = asDeviceId(wechatUser.getOpenId());
                String deviceTypeId = wechatDeviceTypeId();

                for (DeviceManagerOuterClass.QueryDeviceInfoOutput each : deviceInfoOutputList) {
                    /*
                     * 存在该设备绑定在名下
                     */
                    if (StringUtils.equals(each.getDeviceId(), deviceId)
                            && StringUtils.equals(each.getDeviceTypeId(), deviceTypeId)) {
                        return false;
                    }
                }
            }
            /*
             * 不存在该设备绑定在名下
             */
            return true;

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

    public void createMockDevice(
            Account account,
            DialogHandler.User wechatUser) {

        account.legal();

        Map<String, Object> interfaceParams = Maps.newHashMap();
        interfaceParams.put("account", account);
        interfaceParams.put("wechatUser", wechatUser);

        Tracer tracer = Tracer.of(log, "AccountAdaptor.createMockDeviceInfo", interfaceParams);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(deviceHost, devicePort)
                    .usePlaintext()
                    .build();
            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            String mockedDeviceId = asDeviceId(wechatUser.getOpenId());
            DeviceManagerOuterClass.AddRokidRequest request = DeviceManagerOuterClass.AddRokidRequest.newBuilder()
                    .setRokiId(mockedDeviceId)
                    .setDeviceTypeID(wechatDeviceTypeId())
                    .setUuid(mockedDeviceId)
                    .setSecret(mockedDeviceId)
                    .setSource(appId)
                    .build();

            DeviceManagerOuterClass.AddRokidResponse response = blockingStub.addRokid(request);
            tracer.result(response);

            Preconditions.checkArgument(Objects.nonNull(response), "GRpc response is null");
            int resultCode = response.getResultCode();
            Preconditions.checkArgument((0 == resultCode), "DEVICE_INFO_SERVER_ERROR");

            Preconditions.checkArgument(
                    StringUtils.equals("success", response.getMessage()), "CREATE_ROKID_ERROR" + response.getMessage());

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

    public boolean bondTo(Account account,
                          DialogHandler.User wechatUser) {

        Map<String, Object> param = Maps.newHashMap();
        param.put("account", account);
        param.put("wechatUser", wechatUser);

        Tracer tracer = Tracer.of(log, "Account.bondToMaster", param);


        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(deviceHost, devicePort)
                    .usePlaintext()
                    .build();


            DeviceManagerOuterClass.BindMasterRequest request =
                    DeviceManagerOuterClass.BindMasterRequest.newBuilder()
                            .setUserId(account.getMasterId())
                            .setDeviceId(asDeviceId(wechatUser.getOpenId()))
                            .setDeviceTypeId(wechatDeviceTypeId())
                            .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            DeviceManagerOuterClass.BindMasterResponse response = blockingStub.bindMaster(request);
            tracer.result(response);

            int resultCode = response.getResultCode();
            return (0 == resultCode);

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

    public Account ofAccount(DialogHandler.User wechatUser) {

        Tracer tracer = Tracer.of(log, "AccountAdaptor.ofAccount", null);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(deviceHost, devicePort)
                    .usePlaintext()
                    .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            DeviceManagerOuterClass.GetDeviceMasterRequest request =
                    DeviceManagerOuterClass.GetDeviceMasterRequest.newBuilder()
                            .setDeviceTypeId(wechatDeviceTypeId())
                            .setDeviceId(asDeviceId(wechatUser.getOpenId()))
                            .build();
            DeviceManagerOuterClass.GetDeviceMasterResponse response = blockingStub.getDeviceMaster(request);

            tracer.result(response);
            Preconditions.checkArgument(Objects.nonNull(response), "GRpc response is null");
            Preconditions.checkArgument(0 == response.getResultCode(), "DEVICE_INFO_SERVER_ERROR");

            String userId = response.getUserId();

            if (StringUtils.isBlank(userId)) {
                return null;
            }
            return Account.builder().masterId(userId)
                    .build();

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
