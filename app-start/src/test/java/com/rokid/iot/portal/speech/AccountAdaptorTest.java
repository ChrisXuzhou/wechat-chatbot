package com.rokid.iot.portal.speech;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.Application;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.wechat.domain.DialogHandler;
import com.rokid.iot.portal.wechat.port.account.AccountAdaptor;
import com.rokid.open.devicemanager.facade.DeviceManagerGrpc;
import com.rokid.open.devicemanager.facade.DeviceManagerOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AccountAdaptorTest extends TestConcern {

    @Resource
    private AccountAdaptor accountAdaptor;

    AccountAdaptor.Account account =
            AccountAdaptor.Account.builder()
                    .masterId("7FD0F16E0BC04FE981C7AD4DD5C73667")
                    .build();


    DialogHandler.User user = DialogHandler.User.builder().openId("oVcpRwwWBjlcCnbQuk40BUFCSk_U").build();

    @Test
    public void testAccount() {

        System.out.println(
                JsonHelper.toJson(
                        accountAdaptor.of(
                                user
                        )
                )
        );
    }

    @Test
    public void testNotExistMockDevice() {

        System.out.println(
                accountAdaptor.notExistMockDevice(user)
        );
    }

    @Test
    public void testCreateMockDevice() {

        accountAdaptor.createMockDevice(
                AccountAdaptor.Account.builder()
                        .masterId("7FD0F16E0BC04FE981C7AD4DD5C73667")
                        .build(),
                user
        );
    }

    @Test
    public void testOfAccount() {
        System.out.println(
                JsonHelper.toJson(
                        accountAdaptor.ofAccount(user))
        );
    }

    @Test
    public void testBondAccount() {
        System.out.println(
                JsonHelper.toJson(
                        accountAdaptor.bondTo(account, user))
        );
    }

    @Test
    public void testAccountGet() {

        MockDevice mockDevice = mock();
        Tracer tracer = Tracer.of(log, "AccountAdaptor.existMockDevice", null);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(
                            "device-manager.rpc.rokid.com",
                            30000)
                    .usePlaintext()
                    .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            DeviceManagerOuterClass.GetDeviceMasterRequest request =
                    DeviceManagerOuterClass.GetDeviceMasterRequest.newBuilder()
                            .setDeviceTypeId(mockDevice.getDeviceTypeId())
                            .setDeviceId(mockDevice.getDeviceId())
                            .build();
            DeviceManagerOuterClass.GetDeviceMasterResponse response = blockingStub.getDeviceMaster(request);

            tracer.result(response);
            Preconditions.checkArgument(Objects.nonNull(response), "GRpc response is null");

            String userId = response.getUserId();
            if (StringUtils.isEmpty(userId)) {
                throw new IllegalStateException("master id is null!" + JsonHelper.toJson(response));
            }

            String msg = response.getMessage();
            System.out.println(msg);

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

    @Test
    public void testBondToAccount() {

        MockDevice mockDevice = mock();

        Map<String, Object> param = Maps.newHashMap();
        param.put("mockDevice", mockDevice);

        Tracer tracer = Tracer.of(log, "Account.testBondToAccount", param);


        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(
                            "device-manager.rpc.rokid.com",
                            30000)
                    .usePlaintext()
                    .build();


            DeviceManagerOuterClass.BindMasterRequest request =
                    DeviceManagerOuterClass.BindMasterRequest.newBuilder()
                            .setUserId(mockDevice.getMasterId())
                            .setDeviceId(mockDevice.getDeviceId())
                            .setDeviceTypeId(mockDevice.getDeviceTypeId())
                            .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);
            DeviceManagerOuterClass.BindMasterResponse response = blockingStub.bindMaster(request);
            tracer.result(response);

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

    @Test
    public void testGetDeviceList() {

        Tracer tracer = Tracer.of(log, "testGetDeviceList", account);
        ManagedChannel managedChannel = null;
        try {
            managedChannel = ManagedChannelBuilder
                    .forAddress(
                            "device-manager.rpc.rokid.com",
                            30000)
                    .usePlaintext()
                    .build();

            DeviceManagerGrpc.DeviceManagerBlockingStub blockingStub = DeviceManagerGrpc.newBlockingStub(managedChannel);

            DeviceManagerOuterClass.QueryDeviceListRequest request =
                    DeviceManagerOuterClass.QueryDeviceListRequest.newBuilder()
                            .setUserId(account.getMasterId())
                            .build();

            DeviceManagerOuterClass.QueryDeviceListResponse response = blockingStub.queryDeviceList(request);

            tracer.result(response);

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
