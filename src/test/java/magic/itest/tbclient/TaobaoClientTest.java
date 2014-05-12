package magic.itest.tbclient;

import java.util.HashMap;
import java.util.Map;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.util.RequestParametersHolder;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.internal.util.TaobaoUtils;
import com.taobao.api.internal.util.WebUtils;
import com.taobao.api.request.UserSellerGetRequest;
import com.taobao.api.response.UserSellerGetResponse;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月10日 下午4:04:28
 */
public class TaobaoClientTest {
	protected static String url = "http://gw.api.tbsandbox.com/router/rest";// 沙箱环境调用地址
	// 正式环境需要设置为:http://gw.api.taobao.com/router/rest
	protected static String appkey = "1021784951";
	protected static String appSecret = "sandboxb4d2fbacf3ba99e534ccd903a";
	// 测试沙箱帐号sandbox_b_00:taobao1234
	protected static String sessionkey = "test"; // 如
													// 沙箱测试帐号sandbox_c_1授权后得到的sessionkey

	public static void testUserGet() {
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);// 实例化TopClient类
		UserSellerGetRequest req = new UserSellerGetRequest();// 实例化具体API对应的Request类
		req.setFields("nick,user_id,type");
		// req.setNick("sandbox_c_1");
		UserSellerGetResponse response;
		try {
			response = client.execute(req/* , sessionkey */); // 执行API请求并打印结果
			System.out.println("result:" + response.getBody());
		} catch (ApiException e) {
			// deal error
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// TaobaoClientTest.testUserGet();
		Map<String, String> param = new HashMap<String, String>();
		Long timestamp = System.currentTimeMillis();
		param.put("appkey", appkey);
		param.put("post_uri", "http://localhost/clientCMD");
		param.put("timestamp", timestamp.toString());
		TaobaoHashMap applicationParams = new TaobaoHashMap(param);
		RequestParametersHolder requestHolder = new RequestParametersHolder();
		requestHolder.setApplicationParams(applicationParams);
		String _sign = TaobaoUtils.signTopRequestNew(requestHolder, "e3b07708529b3f1e9fba7c193649c931", false);
		System.out.println("_sign = " + _sign);
		param.put("sign", _sign);
		String responseJson = WebUtils.doPost("http://container.api.taobao.com/container/token", param, "utf-8", 3000,
				3000);
		System.out.println(responseJson);
	}
}
