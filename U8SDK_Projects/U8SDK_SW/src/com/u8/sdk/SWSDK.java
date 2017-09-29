package com.u8.sdk;

import com.weizhong.shuowan.sdk.ShuoWanSDK;
import com.weizhong.shuowan.sdk.listener.CheckUpdateListener;
import com.weizhong.shuowan.sdk.listener.InitListener;
import com.weizhong.shuowan.sdk.listener.LogoutNotifyListener;
import com.weizhong.shuowan.sdk.listener.OnExitListener;
import com.weizhong.shuowan.sdk.yt.LoginCallback;
import com.weizhong.shuowan.sdk.yt.LoginErrorMsg;
import com.weizhong.shuowan.sdk.yt.OnLoginListener;
import com.weizhong.shuowan.sdk.yt.OnPaymentListener;
import com.weizhong.shuowan.sdk.yt.PaymentCallbackInfo;
import com.weizhong.shuowan.sdk.yt.PaymentErrorMsg;

import android.R.string;
import android.app.Activity;
import android.util.Log;

public class SWSDK {
	private static SWSDK instance;
	private boolean mode = false;

	private SWSDK() {

	}

	public static SWSDK getInstance() {
		if (instance == null) {
			instance = new SWSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		Log.d("mmddt", "start initsdk");

		context.runOnUiThread(new Runnable() {
			public void run() {
				// 初始化ShuoWanSDK
				// ShuoWanSDK.defaultSDK().init(this);
				// 推荐使用此接口进行初始化
				ShuoWanSDK.defaultSDK().init(U8SDK.getInstance().getContext(), new InitListener() {

					@Override
					public void initSuccess() {
						// 初始化成功
						Log.d("mmddt", "init sdk success");
						if (mode) {
							// 设置为测试环境，不调用默认为正式环境，注意：打包正式包时请勿调用
							ShuoWanSDK.defaultSDK().setDebug();
						}

						// 退出登陆回调监听，用户在个人中心修改密码或退出登录时调用
						ShuoWanSDK.defaultSDK().setLougoutNotifyListener(new LogoutNotifyListener() {

							@Override
							public void onLogout() {
								U8SDK.getInstance().onLogout();
								//ShuoWanSDK.defaultSDK().showLogin(U8SDK.getInstance().getContext(), false,loginListener);
							}
						});
					}

					@Override
					public void initFailed(String msg) {
						// 初始化失败，msg：失败原因
						Log.e("shuowanSDK", msg);
					}
				});

			}
		});

	}

	// TODO
	/**
	 * SDK登陆回调监听
	 */
	private OnLoginListener loginListener = new OnLoginListener() {

		@Override
		public void loginSuccess(LoginCallback arg0) {

			// 显示浮窗
			ShuoWanSDK.defaultSDK().showFloatView(U8SDK.getInstance().getContext());
			String sign = arg0.sign;
			String userName = arg0.username;
			String loginTime = Long.valueOf(arg0.logintime).toString();
			if (sign != null && userName != null)
			{
				String dataStr = String.format(" { sign:\"%s\", username:\"%s\", loginTime:\"%s\" }", sign, userName,
						loginTime);
				U8SDK.getInstance().onLoginResult(dataStr);
			}
			else
			{
				Log.d("mmddt","argument have error");
			}
			// TODO 使用sign去游戏服务器进行登陆验证
			
		}

		@Override
		public void loginError(LoginErrorMsg errMsg) {
			if (errMsg.code == LoginErrorMsg.CODE_CANCEL) {
				// TODO用户取消登录窗口
				Log.e("GameDemo", "取消登陆");
			} else {
				Log.e("GameDemo", "登陆失败");
			}
		}
	};

	private void parseSDKParams(SDKParams params) {
		this.mode = params.getBoolean("mode");
	}

	public void doLogin() {
		/**
		 * 调用登陆接口 参数说明: Context arg0---上下文对象, boolean
		 * arg1----是否快速登陆(true：调用后直接登陆，false：需要用户点击登陆), OnLoginListener
		 * arg2-----登陆回调监听
		 */

		ShuoWanSDK.defaultSDK().showLogin(U8SDK.getInstance().getContext(), false, loginListener);
	}

	public void doLogout() {
		ShuoWanSDK.defaultSDK().logout();
		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {
		/**
		 * 
		 * 参数说明: Context arg0----上下文对象; String arg1----游戏角色id; double
		 * arg2-----充值金额(单位：元); String arg3-----服务器id; String arg4------商品名称;
		 * String arg5-----商品描述 ; String arg6------附加参数; OnPaymentListener
		 * arg7-----支付回调
		 */

		ShuoWanSDK.defaultSDK().showPay(
				U8SDK.getInstance().getContext(),
				data.getRoleId(), 
				data.getPrice(),
				data.getServerId(),
				data.getProductName(), 
				"diamond",
				data.getOrderID(),
				new OnPaymentListener() {

					@Override
					public void paymentSuccess(PaymentCallbackInfo arg0) {
						Log.e("shuowanSDK", "支付成功");
						U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "支付成功");
						// Toast.makeText(U8SDK.getInstance().getContext(),
						// "支付成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void paymentError(PaymentErrorMsg msg) {
						switch (msg.code) {
						case PaymentErrorMsg.CODE_CANCEL:
							// Toast.makeText(U8SDK.getInstance().getContext(),
							// "取消支付", Toast.LENGTH_SHORT).show();
							break;
						case PaymentErrorMsg.CODE_FAILED:
							// Toast.makeText(U8SDK.getInstance().getContext(),
							// "支付失败", Toast.LENGTH_SHORT).show();
							break;
						case PaymentErrorMsg.CODE_UNKNOW:
							// 未知情况，此处去服务器请求支付结果
							// Toast.makeText(U8SDK.getInstance().getContext(),
							// "未知", Toast.LENGTH_SHORT).show();
							break;
						}
						U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "支付失败");
					}
				});
	}

	public void onBackPressed() {
		// TODO 退出游戏提示框,必调
		ShuoWanSDK.defaultSDK().exitSDK(U8SDK.getInstance().getContext(), new OnExitListener() {

			@Override
			public void exit() {
				// 退出游戏
				Log.e("shuowanSDK", "exit");

			}

			@Override
			public void backToGame() {
				// 返回游戏
				Log.e("shuowanSDK", "backToGame");
			}
		});

	}
}
