package com.u8.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.game.sdk.common.model.biz.ReportUserGameInfoParam;
import com.nearme.game.sdk.common.util.AppUtil;
import com.nearme.platform.opensdk.pay.PayResponse;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class OPPOSDK {
	private static OPPOSDK instance;

	private OPPOSDK() {

	}

	public static OPPOSDK getInstance() {
		if (instance == null) {
			instance = new OPPOSDK();
		}
		return instance;
	}

	private String m_userID = "";

	public void initSDK(final Activity context, final SDKParams params) {
		Log.d("mmddt", "params:" + params.toString());
	}

	public void doLogin(int aPlatfrom) {
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				GameCenterSDK.getInstance().doLogin(U8SDK.getInstance().getContext(), new ApiCallback() {

					@Override
					public void onSuccess(String resultMsg) {
						GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {

							@Override
							public void onSuccess(String resultMsg) {
								try {
									JSONObject json = new JSONObject(resultMsg);
									String token = json.getString("token");
									String ssoid = json.getString("ssoid");
									String dataStr = "{ \"token\" : \"" + token + "\", \"userid\" : \"" + ssoid + "\" }";
									U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
									U8SDK.getInstance().onLoginResult(dataStr);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(String content, int resultCode) {

							}
						});
					}

					@Override
					public void onFailure(String resultMsg, int resultCode) {
						// Toast.makeText(DemoActivity.this, resultMsg,
						// Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	public void doLogin() {
		doLogin(0);
	}

	public void doLogout() {

		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {
		

		/*String ps = "{\"state\":1,\"data\":{\"orderID\":\"1313550167625957383\",\"extension\":{\"amount\":600,\"attach\":\"\",\"callbackUrl\":\"http://171.221.254.163:9000/TSDK/pay/oppo/payCallback\",\"order\":\"1313550167625957383\",\"productDesc\":\"\",\"productName\":\"com.muzhiyouwan.bzddt_60\"}}}";

		try {
			JSONObject jo = new JSONObject(ps);
			data = new PayParams();
			data.setOrderID(jo.getString("orderID"));
			data.setPrice(1);
			data.setProductName("60钻石");
			data.setProductDesc("60钻石");
			data.setPayNotifyUrl("http://127.0.0.1");
		} catch (JSONException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		final PayParams pObj = data;

		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				PayInfo payInfo = new PayInfo(pObj.getOrderID(), pObj.getOrderID(), pObj.getPrice());
				if (pObj.getExtension().length() > 0) {
					try {
						//String ex = "{\"accountId\":\"4aa75da6b260f9c30b48424beebed30b\",\"amount\":\"6\",\"callbackInfo\":\"com.muzhiyouwan.bzddt_60\",\"cpOrderId\":\"1313140479285526529\",\"notifyUrl\":\"http://171.221.254.163:9000/TSDK/pay/uc/payCallback\",\"sign\":\"7dc1953da71b5dfc8d8a389c86d867df\",\"signType\":\"MD5\"}";
						//payParams.setExtension(ex);
						JSONObject jo = new JSONObject(pObj.getExtension());
						payInfo.setOrder(jo.getString("order"));
						payInfo.setProductDesc(pObj.getProductDesc());
						payInfo.setProductName(jo.getString("productName"));
						payInfo.setCallbackUrl(jo.getString("callbackUrl"));
						payInfo.setAmount(Integer.parseInt(jo.getString("amount")));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				GameCenterSDK.getInstance().doPay(U8SDK.getInstance().getContext(), payInfo, new ApiCallback() {

					@Override
					public void onSuccess(String resultMsg) {
						Toast.makeText(U8SDK.getInstance().getContext(), "支付成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(String resultMsg, int resultCode) {
						if (PayResponse.CODE_CANCEL != resultCode) {
							Toast.makeText(U8SDK.getInstance().getContext(), "支付失败", Toast.LENGTH_SHORT).show();
						} else {
							// 取消支付处理
							Toast.makeText(U8SDK.getInstance().getContext(), "支付取消", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	public void doSubmitExtraData(UserExtraData extraData) {
		GameCenterSDK.getInstance().doReportUserGameInfoData(
				new ReportUserGameInfoParam("gameID", "service", "role", "grade"), new ApiCallback() {

					@Override
					public void onSuccess(String resultMsg) {
						// Toast.makeText(DemoActivity.this, "success",
						// Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(String resultMsg, int resultCode) {
						// Toast.makeText(DemoActivity.this, resultMsg,
						// Toast.LENGTH_LONG).show();
					}
				});
	}

	public void doExit() {
		GameCenterSDK.getInstance().onExit(U8SDK.getInstance().getContext(), new GameExitCallback() {

			@Override
			public void exitGame() {
				// CP 实现游戏退出操作，也可以直接调用
				// AppUtil工具类里面的实现直接强杀进程~
				AppUtil.exitGameProcess(U8SDK.getInstance().getContext());
			}
		});
	}

	public void showToastTips(final String aTips) {
		U8SDK.getInstance().getContext().runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(U8SDK.getInstance().getContext(), aTips, Toast.LENGTH_LONG).show();
			}
		});
	}

}
