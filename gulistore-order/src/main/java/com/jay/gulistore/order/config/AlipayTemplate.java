package com.jay.gulistore.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.jay.gulistore.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000122675927";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCdC9aavo9vPYFOSBo+oK/KITZpCTP1f3qkVavNnXDvdto9OJakkQPsyjdsoHzYpQWfGSzzU93hqI7ej12jUP2KY46kNmJZEEYZ8/X3dNxt1v0+bUd6iv9cO9BnmxZ8V+LBzXGmpsagZsMle6A0JguBvbNg+DozHy3FBVs9yIa70KBwyyNYkY3G9yet6MOJPkuXdOB1ilcTsbYZaI+jwXUYGtQqNTJsOc6c45Yxrz0fxi1wHu4QSsUcYl4rQGzQ+SCjnU1QQBZofoFNCgvcXSv/EGCh7pW2VO+9qy8PWXXY5FYdNESBo/S25DvbTHgedt4HD2hbYI86F1iIxjrRAW+HAgMBAAECggEAJ5C29W9y9YSaUh2KNyMGhrcNXTMLWxtNy58l1HyI2luoKepa0YUTZxGfGwfnzDDvFKEkGP2VFegAboPSBHZAGiBw7GAjMOsPrjQvULOpYSbb0GF5s57XLOaI38FLzfHaWBcpPeIggoLBQW0+9ERte7dOPOzsxLpjLpLl5M/jWeeTUWfO8VYCgHy5CBGVpr0o5fu8c9AEMWIAYBDumTabKBBFomipjZh+X/8awZ+7IKBc8SVeVC+idbwI51wYGVziyD0of2LK8oKFg37qU03PJ2Dc1VI+tqmVR3JGHZtGXb4AGCPikDfVEVrl3FBywCmz6hi0sZycYxw2QP3gSPEgCQKBgQD4uFnQY5l9HJr2Uz/mC9EtBw6Qwbge8wLezzggS1+9GBuPYJYGUUYpf9jQWd90gXfWuWsTC8At28heEAIOYtHqjtWMlk+/KAQ3fMzY9sh5DKlfubZG7VvwKGMEnSGbLQtCKM7VhHE8SMowIkuAqla1IHPfjVTF+nPo0TXE3U6xXQKBgQChpJQ8SWDBkVT1ogDUDtlNVwGprxHF/ZO6D+rWyoZlvI9gCNSOUwQ0Xs5auXYPp+lQ6DBp8rImK4XZijI0uxKiVXegxyjxmPzGyC923dOMZiVChUhS3T+Twx795xZhNM/S+MjO+k7fa/plqa4jjiqru8oFZYny5hq1lB5HbMDiMwKBgQCSZnD3aKkAK8ZLF9WU43/MsUqxilWGYiReYjr3R2ajN32Bd6jXLx/NbaOBgGU3fr27dojAPTpxw9y9AU80iJZnKowopJFsQ3G3943cZ+a42esYnYfdV/m18w/Vt2gAnxLxbnPQ2pSMl4vrqUQoCBvhoaiS/e3YWwO/FuY/asTXQQKBgAyILvnxkm/Pba/RjJDkSdKmMTnPmegovcBKJaHbd8oBcXg3lwslwoiw5PzBrjaOhRdBuSctqsQs0abJARV3f31YdPrhFfqNKuPad1uW3STgcnkSzkWmY5x4Hzn+JY0JxCcOJLtvSpglzfb9zcNx7Tr9Ry9d7PEJB/8V6bcN5nJjAoGANpbiZDrsIOIiaeHaL8Rxd7Z1gDV4OTXlUliuWoTz6W0Q8Xc2Puu6cTlG9k70/XQ+tiIq9UZSMFy9AZu/RQbKEBo9wxBbKYQOw+LIJbFUpsqZryGBzfth85zvawoeuH7g+tbI8Xk7HHeYfalCTaZu8CxOJRHD23yWW2PIYdAcJ98=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiwi2lwNoUnwFLJ/hSb7G8aOdUQDvMkLK6g7eMsZXw/CcP+Wq+PBXHa63WA3jqvrjsaDSu6ZgtYBPARPka9FjhXgmvPjcj6RBaMToCfUUXnDYCuklE8Zp8hJjRatPBs2UKOZU8IwVy6lHAoHogWhBsrCXcEZMOZSzkd+FHhOaMECV7mhPIFXxUKi75gLzTLAQyGKLccyOInH9m+FP4YLYKESZy9vENhr9zriV3zderbRNvI5g55flMwwvxNl1NQ8pJ7RPnSAjAhipsNgwj0O6bk00OjrwQMft91Fzqa+W+bn5FwYvICMXmUJ5mF2F+F2nyocqHv4oXRA6zJUmh5d7LQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://order.sukestore.com/list.html";
//    private  String notify_url = "http://member.sukestore.com/memberOrder.html";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
//    private  String return_url = "http://member.sukestore.com/memberOrder.html";
    private  String return_url = "http://order.sukestore.com/list.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
