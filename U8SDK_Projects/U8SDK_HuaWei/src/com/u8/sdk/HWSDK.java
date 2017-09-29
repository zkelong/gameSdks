package com.u8.sdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.gameservice.sdk.GameServiceSDK;
import com.huawei.gameservice.sdk.control.GameEventHandler;
import com.huawei.gameservice.sdk.model.PayResult;
import com.huawei.gameservice.sdk.model.Result;
import com.huawei.gameservice.sdk.model.UserResult;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;

public class HWSDK {
	private static HWSDK instance;

	private HWSDK() {

	}

	public static HWSDK getInstance() {
		if (instance == null) {
			instance = new HWSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		Log.d("mmddt", "u8sdk:" + params.toString());
		GameServiceSDK.init(context, params.getString("appid"), params.getString("cpid"), null, new GameEventHandler() {

			@Override
			public void onResult(Result result) {
				Log.d("mmddt", "init rseult:" + result.toString());
				if (result.rtnCode != Result.RESULT_OK) {
					Log.d("mmddt",
							"init the game service SDK failed:" + result.rtnCode + "desc:" + result.description == null
									? "" : result.description);
				} else {
					Log.d("mmddt", "init success");
					checkUpdate();
				}
			}

			@Override
			public String getGameSign(String appId, String cpId, String ts) {
				return createGameSign(appId + cpId + ts);
			}

		});
	}

	/**
	 * 生成游戏签名 generate the game sign
	 */
	private String createGameSign(String data) {

		// 为了安全把浮标密钥放到服务端，并使用https的方式获取下来存储到内存中，CP可以使用自己的安全方式处理
		// For safety, buoy key put into the server and use the https way to get
		// down into the client's memory.
		// By the way CP can also use their safe approach.

		String str = data;
		Log.d("mmddt", "sign:" + str);
		try {
			String result = HWRSAUtil.sha256WithRsa(str.getBytes("UTF-8"),
					"MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL4EBdNaxTWXPjBE0Y6GXuoNv2DuB1k0jtfiGrV1whUG+LCoGvltuwsszKASh4uR/rSImD9Aixp20xKli6rUYNbhIoSQ+4FIivq5ds9K3q6RmdsRdHD+Bp2JA5huM0Sb+hpgwQK5Wh1G2bj/IxVXshgqwXm9YJCmb6HgqH4nyQ7FAgMBAAECgYEAsKw+2mhQauaoDOs/yPwI7ihAJBjNPlUO5400djn6beCghGiZRAeR3O8Xh1ITM2NYZnWG0XDqpX2L/Y9s0DZNnoLKvwpPii1CUwgs2hNFxuqnWGOSqcMoyf1muyyTJhCd4TkP/+bplhXdefNFEIB5q2lP+Ij5vpk7eFWFHRci3GECQQDpHyOPOxNEi5fBj5kR2vp1kCHN8283JvuoxA9V4lwSUkyoEAgml9RRlpC1VDDUPcIMM9GcW2GjtMfZNxuSasWtAkEA0KnoHw+dt4RS6drXqWBJ7CSqcTRQCq23nt25mtzbIOPz7FXxdQEFgHEDpG+1AQtZVEKF7nQVnmRzn/pmgIwgeQJAcIdib9oxU84aRAnf9pmAes1HNBYFIldD9VQmnut0TDfD3wBWHuL9TyDh21W3eEwTZjaBQPtvZ0l1iqmqOoQlxQJBAImvU0cLmto5Kiy9BojwzFZRa1X/WsFxFzRwwcP5722ofI95tjWqUuMJr4mVXC4wOuVqb82EM6+sQM9S/sfbrqECQQDHSA7O4Xu5OqwM4ip9zYjVn10njBpjtL+tzhg4yogbEiOgyIDPIQ0w84Le5sErxtKr12qN+dwmR8AGE4CR/Rjb");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检测游戏更新 check the update for game
	 */
	private void checkUpdate() {
		GameServiceSDK.checkUpdate(U8SDK.getInstance().getContext(), new GameEventHandler() {

			@Override
			public void onResult(Result result) {
				if (result.rtnCode != Result.RESULT_OK) {
					// handleError("check update failed:" + result.rtnCode);
				}
			}

			@Override
			public String getGameSign(String appId, String cpId, String ts) {
				return null;
			}

		});
	}

	public void doLogin() {
		GameServiceSDK.login(U8SDK.getInstance().getContext(), new GameEventHandler() {

			@Override
			public void onResult(Result result) {
				if (result.rtnCode != Result.RESULT_OK) {
					Log.d("mmddt", "login failed:" + result.toString());
					// uiHandler.sendMessage(uiHandler.obtainMessage(HIDE_BUTTON));
				} else {
					UserResult userResult = (UserResult) result;
					if (userResult.isAuth != null && userResult.isAuth == 1) {
						String dataStr = "{ \"token\" : \"" + userResult.gameAuthSign + "\", \"userid\" : \""
								+ userResult.playerId + "\", \"ts\" : \"" + userResult.ts + "\", \"displayName\" : \""
								+ userResult.displayName + "\" }";
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
						U8SDK.getInstance().onLoginResult(dataStr);
					} else if (userResult.isChange != null && userResult.isChange == 1) {
						doLogin();
					} else {
						Log.d("mmddt", "login success:" + userResult.toString());
					}

				}
			}

			@Override
			public String getGameSign(String appId, String cpId, String ts) {
				return null;
			}

		}, 1);
	}

	public void doLogout() {

		U8SDK.getInstance().onLogout();
	}

	/**
	 * 支付回调handler
	 */
	/**
	 * pay handler
	 */
	private GameEventHandler payHandler = new GameEventHandler() {
		@Override
		public String getGameSign(String appId, String cpId, String ts) {
			return null;
		}

		@Override
		public void onResult(Result result) {

			Map<String, String> payResp = ((PayResult) result).getResultMap();
			Log.d("mmddt", "pay result:" + payResp.toString());
			// 支付成功，进行验签
			// payment successful, then check the response value
			if ("0".equals(payResp.get("returnCode"))) {
				if ("success".equals(payResp.get("errMsg"))) {
					// 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
					// If the response value contain the param
					// "isCheckReturnCode" and its value is yes, then remove the
					// param "isCheckReturnCode".
					if (payResp.containsKey("isCheckReturnCode") && "yes".equals(payResp.get("isCheckReturnCode"))) {
						payResp.remove("isCheckReturnCode");

					}
					// 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
					// If the response value does not contain the param
					// "isCheckReturnCode" and its value is yes, then remove the
					// param "isCheckReturnCode".
					else {
						payResp.remove("isCheckReturnCode");
						payResp.remove("returnCode");
					}
					// 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
					// remove the param "sign" from response
					String sign = payResp.remove("sign");

					// Toast.makeText(U8SDK.getInstance().getContext(), "支付成功",
					// Toast.LENGTH_SHORT).show();
				}

			} else if ("30002".equals(payResp.get("returnCode"))) {
				// pay = getString(R.string.pay_result_timeout);
			}
			// Toast.makeText(GameActivity.this, pay,
			// Toast.LENGTH_SHORT).show();

			// 重新生成订单号，订单编号不能重复，所以使用时间的方式，CP可以根据实际情况进行修改，最长30字符
			// generate the pay ID using the date format, and it can not be
			// repeated.
			// CP can generate the pay ID according to the actual situation, a
			// maximum of 30 characters

		}
	};

	public void doPay(Activity context, PayParams data) {
		final PayParams pobj = data;
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				Map<String, Object> payInfo = new HashMap<String, Object>();

				if (pobj.getExtension().length() > 0) {
					try {
						JSONObject jo = new JSONObject(pobj.getExtension());
						// 必填字段，不能为null或者""
						// the amount is required and can not be null or ""
						payInfo.put(HWGlobalParam.PayParams.AMOUNT, jo.getString(HWGlobalParam.PayParams.AMOUNT));
						// 必填字段，不能为null或者""
						// the product name is required and can not be null or
						// ""
						payInfo.put(HWGlobalParam.PayParams.PRODUCT_NAME,
								jo.getString(HWGlobalParam.PayParams.PRODUCT_NAME));
						// 必填字段，不能为null或者""
						// the request ID is required and can not be null or ""
						payInfo.put(HWGlobalParam.PayParams.REQUEST_ID,
								jo.getString(HWGlobalParam.PayParams.REQUEST_ID));
						// 必填字段，不能为null或者""
						// the product description is required and can not be
						// null or ""
						payInfo.put(HWGlobalParam.PayParams.PRODUCT_DESC,
								jo.getString(HWGlobalParam.PayParams.PRODUCT_DESC));
						// 必填字段，不能为null或者""，请填写自己的公司名称
						// the user name is required and can not be null or "".
						// Input
						// the
						// company name of CP.
						payInfo.put(HWGlobalParam.PayParams.USER_NAME,
								U8SDK.getInstance().getSDKParams().getString("company"));
						// 必填字段，不能为null或者""
						// the APP ID is required and can not be null or "".
						payInfo.put(HWGlobalParam.PayParams.APPLICATION_ID,
								jo.getString(HWGlobalParam.PayParams.APPLICATION_ID));
						// 必填字段，不能为null或者""
						// the user ID is required and can not be null or "".
						payInfo.put(HWGlobalParam.PayParams.USER_ID, jo.getString(HWGlobalParam.PayParams.USER_ID));
						// 必填字段，不能为null或者""
						// the sign is required and can not be null or "".
						payInfo.put(HWGlobalParam.PayParams.SIGN, jo.getString("sign"));
						// 选填字段，建议使用RSA256
						// recommended to use RSA256.
						// payInfo.put(HWGlobalParam.PayParams.SIGN_TYPE,
						// U8SDK.getInstance().getSDKParams().getString("signTpye"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.d("mmddt", "payInfo:" + payInfo.toString());
				payInfo.put(HWGlobalParam.PayParams.SCREENT_ORIENT, HWGlobalParam.PAY_ORI_LAND);
				GameServiceSDK.startPay(U8SDK.getInstance().getContext(), payInfo, payHandler);
			}
		});

	}
}
