package com.itheima.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {
    public static final String VALIDATE_CODE = "传智健康";
    public static final String TEMPLATE_CODE = "SMS_200194039";
	/**
	 * 发送短信
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String phoneNumbers,String param){
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G54ArTUJpj5Tacgv876", "2Mdu8XXLH7G40t8iBBUHgaGYKXs9BW");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(VALIDATE_CODE);
		request.setTemplateCode(TEMPLATE_CODE);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
            System.out.println(response.getMessage());
			//return response.getMessage();
		}catch (ClientException e) {
			e.printStackTrace();
			//return null;
		}
	}

   /* public static void main(String[] args) {
        sendMessage("18382844583","1234");
    }*/

}
