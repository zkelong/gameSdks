package com.u8.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.gionee.gamesdk.floatwindow.AccountInfo;
import com.gionee.gamesdk.floatwindow.GamePayCallBack;
import com.gionee.gamesdk.floatwindow.GamePayManager;
import com.gionee.gamesdk.floatwindow.GamePlatform;
import com.gionee.gamesdk.floatwindow.GamePlatform.LoginListener;
import com.gionee.gamesdk.floatwindow.utils.Util;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class JinLiSDK {
	private static JinLiSDK instance;

	private JinLiSDK() {
		
	}

	public static JinLiSDK getInstance() {
		if (instance == null) {
			instance = new JinLiSDK();
		}
		return instance;
	}

	private String m_userID = "";

	public void initSDK(Activity context, final SDKParams params) {
		/**
		 * 开发者重点关注部分 6.0以上系统需要手动申请悬浮窗权限 该方法需要最好与登录帐号分别在2个不同的页面调用，最好是在启动游戏的时候调用。
		 * 因SDK demo没有启动页面，所以加在此处
		 */
		GamePlatform.requestFloatWindowsPermission(context);

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				// TODO Auto-generated method stub
				super.onActivityResult(requestCode, resultCode, data);
				/**
				 * 该处是为了提示权限赋予成功
				 */
				GamePlatform.onActivityResult(U8SDK.getInstance().getContext(), requestCode, resultCode, data);
			}
		});
	}

	public void doLogin(int aPlatfrom) {
		GamePlatform.loginAccount(U8SDK.getInstance().getContext(), true, new LoginListener() {

			@Override
			public void onSuccess(AccountInfo accountInfo) {
				// 登录成功，处理自己的业务。

				// 获取playerId
				String playerID = accountInfo.mPlayerId;

				// 获取amigoToken
				String amigoToken = accountInfo.mToken;

				// 获取用户ID
				String userId = accountInfo.mUserId;

				JSONObject jo = new JSONObject();
				try {
					jo.put("token", amigoToken);
					jo.put("userid", userId);
					jo.put("playerId", playerID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * String dataStr = "{ \"token\" : \"" + amigoToken +
				 * "\", \"userid\" : \"" + userId + "\", \"playerId\" : \"" +
				 * playerID + "\" }";
				 */
				String dataStr = jo.toString();
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
				U8SDK.getInstance().onLoginResult(dataStr);
			}

			@Override
			public void onError(Object e) {
				// Toast.makeText(U8SDK.getInstance().getContext(), "登录失败:" + e,
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				Toast.makeText(U8SDK.getInstance().getContext(), "取消登录", Toast.LENGTH_SHORT).show();
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

		final PayParams pObj = data;

		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				String orderId = "";
				if (pObj.getExtension().length() > 0) {
					try {
						JSONObject jo = new JSONObject(pObj.getExtension());
						orderId = jo.getString("out_order_no");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				/**
				 * orderInfo 为服务器创建订单成功后返回的全部数据
				 */
				GamePayManager.getInstance().pay(U8SDK.getInstance().getContext(), orderId, new GamePayCallBack() {

					/**
					 * 因为是服务器下单，所以这个接口不会返回
					 * 
					 * @param s
					 */
					@Override
					public void onCreateOrderSuccess(String s) {

					}

					/**
					 * 支付成功
					 */
					@Override
					public void onPaySuccess() {
						// Toast.makeText(NewPayOnlineTestActivity.this,
						// "支付成功", Toast.LENGTH_SHORT).show();
					}

					/**
					 * 支付失败
					 */
					@Override
					public void onPayFail(Exception e) {
						// Toast.makeText(NewPayOnlineTestActivity.this,
						// "支付失败：" + e, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	public void doSubmitExtraData(UserExtraData extraData) {

	}

	public void doExit() {
	}

	public void showToastTips(final String aTips) {
		U8SDK.getInstance().getContext().runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(U8SDK.getInstance().getContext(), aTips, Toast.LENGTH_LONG).show();
			}
		});
	}

}
