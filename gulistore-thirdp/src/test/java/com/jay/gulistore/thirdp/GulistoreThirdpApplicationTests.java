package com.jay.gulistore.thirdp;

import com.aliyun.oss.OSSClient;
import com.jay.gulistore.thirdp.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulistoreThirdpApplicationTests {

	@Test
	void contextLoads() {
	}

    @Autowired
    OSSClient ossClient;

	@Autowired
	SmsComponent smsComponent;

	@Test
	public void testSendCode(){
        smsComponent.sendSmsCode("13063763520","000000");
    }

    @Test
    public void testUpload() throws FileNotFoundException {

        //上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\YELLOW\\Documents\\Tencent Files\\279368894\\FileRecv\\MobileFile\\qw.jpg");
        ossClient.putObject("skdstore", "qw.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功.");
    }


}
