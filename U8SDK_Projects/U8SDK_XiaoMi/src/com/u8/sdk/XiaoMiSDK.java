package com.u8.sdk;

import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnExitListner;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppType;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class XiaoMiSDK {
	private static XiaoMiSDK instance;

	private String appID = "";
	private String appKey = "";

	private XiaoMiSDK() {

	}

	public static XiaoMiSDK getInstance() {
		if (instance == null) {
			instance = new XiaoMiSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);

		MiAppInfo appInfo = new MiAppInfo();
		appInfo.setAppId(this.appID);
		appInfo.setAppKey(this.appKey);
		appInfo.setAppType(MiAppType.online);
		appInfo.setOrientation(ScreenOrientation.horizontal);
		MiCommplatform.Init(context, appInfo);
	}

	private void parseSDKParams(SDKParams params) {
		this.appID = params.getString("appid");
		this.appKey = params.getString("appkey");
	}

	public void doLogin() {
		try{
	//	if (!MiCommplatform.getInstance().isMiAccountLogin()) {
			MiCommplatform.getInstance().miLogin(U8SDK.getInstance().getContext(), new OnLoginProcessListener() {
				@Override
				public void finishLoginProcess(int code, MiAccountInfo arg1) {
					switch (code) {
					case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
						// 登陆成功
						String dataStr = String.format("{ uid : \"%s\", session : \"%s\" }", arg1.getUid(), arg1.getSessionId());
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
						U8SDK.getInstance().onLoginResult(dataStr);

						break;
					case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:
						//登陆失败
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login failed");
						break;
					case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL:
						// 取消登录
						 U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login cancel");
						break;
					case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:
						 U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login action executed");
						// 登录操作正在进行中
						break;
					default:
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, "login failed1");
						// 登录失败
						break;
					}
				}
			});
	//	}
	//	else
	//	{
			// 登陆成功
	//		String dataStr = String.format("{ uid : \"%s\", session : \"%s\" }", MiCommplatform.getInstance().getMiAccountInfo().getUid(),
	//				 MiCommplatform.getInstance().getMiAccountInfo().getSessionId());
	//		U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
	//		U8SDK.getInstance().onLoginResult(dataStr);
	//		Log.d("mmddt2", "已经登陆过了");
	//	}		
			} catch(Exception e) {
				e.printStackTrace();
			}
	}

	public void doLogout() {
		MiCommplatform.getInstance().miAppExit(U8SDK.getInstance().getContext(), new OnExitListner() {

			@Override
			public void onExit(int arg0) {

				if (arg0 == MiErrorCode.MI_XIAOMI_EXIT) {
					U8SDK.getInstance().onLogout();
				}
			}

		});
	}

	public void doPay(Activity context, PayParams data) {
		MiBuyInfo miBuyInfo = this.decodePayParams(data);

		MiCommplatform.getInstance().miUniPay(U8SDK.getInstance().getContext(), miBuyInfo, new OnPayProcessListener() {
			@Override
			public void finishPayProcess(int code) {
				switch (code) {
				case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
					// 购买成功
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "Xiaomi SDK pay success.");
					break;
				case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
					// 取消购买
					break;
				case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
					// 购买失败
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "Xiaomi SDK pay fail.");
					break;
				case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
					// 操作正在进行中
					break;
				default:
					// 购买失败
					break;
				}
			}
		});
	}

	private MiBuyInfo decodePayParams(PayParams data) {
		MiBuyInfo info = new MiBuyInfo();

		info.setCpOrderId(data.getOrderID());// 订单号唯一（不为空）
		info.setCpUserInfo(data.getRoleId()); // 此参数在用户支付成功后会透传给CP的服务器
		info.setAmount(data.getPrice()); // 必须是大于1的整数，10代表10米币，即10元人民币（不为空）
		// 用户信息，网游必须设置、单机游戏或应用可选**
		Bundle mBundle = new Bundle();
		mBundle.putString(GameInfoField.GAME_USER_BALANCE, ""); // 用户余额
		mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, ""); // vip等级
		mBundle.putString(GameInfoField.GAME_USER_LV, ""); // 角色等级
		mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, ""); // 工会，帮派
		mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME, ""); // 角色名称
		mBundle.putString(GameInfoField.GAME_USER_ROLEID, ""); // 角色id
		mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME, ""); // 所在服务器
		info.setExtraInfo(mBundle); // 设置用户信息

		return info;
	}
}
