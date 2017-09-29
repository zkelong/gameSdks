package com.u8.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolcloud.uac.android.api.Coolcloud;
import com.coolcloud.uac.android.api.ErrInfo;
import com.coolcloud.uac.android.api.OnResultListener;
import com.coolcloud.uac.android.common.Constants;
import com.coolcloud.uac.android.common.Params;
import com.coolcloud.uac.android.gameassistplug.GameAssistApi;
import com.coolcloud.uac.android.gameassistplug.GameAssistConfig;
import com.coolcloud.uac.android.gameassistplug.GameAssistContentActivity;
import com.yulong.paysdk.beens.CoolPayResult;
import com.yulong.paysdk.beens.CoolYunAccessInfo;
import com.yulong.paysdk.beens.PayInfo;
import com.yulong.paysdk.coolpayapi.CoolpayApi;
import com.yulong.paysdk.payinterface.IPayResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class CPSDK {
	private static CPSDK instance;
	private com.coolcloud.uac.android.api.Coolcloud mCoolcloud = null;
	private GameAssistApi mGameAssistApi;
	private GameAssistConfig mGameAssistConfig;
	private int pay_style = CoolpayApi.PAY_STYLE_ACTIVITY;
	private int pay_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

	private CPSDK() {

	}

	public static CPSDK getInstance() {
		if (instance == null) {
			instance = new CPSDK();
		}
		return instance;
	}

	private CoolpayApi api;

	public void initSDK(Activity context, final SDKParams params) {
		mCoolcloud = Coolcloud.get(context, params.getString("appid"));
		mGameAssistConfig = new GameAssistConfig();
		mGameAssistConfig.setHideGift(true);

		try {
			Class.forName("com.coolcloud.uac.android.gameassistplug.GameAssistContentActivity");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GameAssistContentActivity at = new GameAssistContentActivity();
			
			Log.d("mmddt","dddd:" + e.getMessage());
			Log.d("mmddt","pppp:"+at.getClass().getName());
		}
		if (mCoolcloud != null) {
			mGameAssistApi = (GameAssistApi) mCoolcloud.getGameAssistApi(context, mGameAssistConfig);
			mGameAssistApi.addOnSwitchingAccountListen(new GameAssistApi.SwitchingAccount() {

				@Override
				public void onSwitchingAccounts() {
					U8SDK.getInstance().onLogout();
				}
			});
		}

		api = CoolpayApi.createCoolpayApi(context, params.getString("appid"));

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {

			@Override
			public void onResume() {
				// TODO Auto-generated method stub
				super.onResume();
				if (mGameAssistApi != null) {
					mGameAssistApi.onResume();
				}
			}

			@Override
			public void onPause() {
				// TODO Auto-generated method stub
				super.onPause();
				if (mGameAssistApi != null) {
					mGameAssistApi.onPause();
				}
			}

		});
	}

	public void doLogin(int aPlatfrom) {

		Bundle mInput = new Bundle();
		// 设置屏幕横竖屏默认为竖屏
		mInput.putInt(Constants.KEY_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 设置获取类型
		mInput.putString(Constants.KEY_RESPONSE_TYPE, Constants.RESPONSE_TYPE_CODE);
		// 设置需要权限 一般都为get_basic_userinfo这个常量
		mInput.putString(Constants.KEY_SCOPE, "get_basic_userinfo");
		mCoolcloud.login(U8SDK.getInstance().getContext(), mInput, new Handler(), new OnResultListener() {

			@Override
			public void onResult(Bundle arg0) {
				// 登录成功 在返回的Bundle中获取AuthCode
				String token = arg0.getString(Params.KEY_AUTHCODE);
				String dataStr = "{ \"token\" : \"" + token + "\" }";
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
				U8SDK.getInstance().onLoginResult(dataStr);
			}

			@Override
			public void onError(ErrInfo s) {
				// 登录失败，获取错误信息
				// Toast.makeText(mContext, s.getMessage(),
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				// 登录被取消
				Toast.makeText(U8SDK.getInstance().getContext(), "取消登陆", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void doLogin() {
		doLogin(0);
	}

	public void doLogout() {
		// 这里添加点击确定后的逻辑
		// 退出账号
		mCoolcloud.logout(U8SDK.getInstance().getContext());
		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {

		final PayParams pObj = data;

		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {

				// 将获取的酷云账号信息传递给支付SDK
				CoolYunAccessInfo accessInfo = new CoolYunAccessInfo();
				PayInfo payInfo = new PayInfo();

				if (pObj.getExtension().length() > 0) {

					try {
						// String ex =
						// "{\"accountId\":\"4aa75da6b260f9c30b48424beebed30b\",\"amount\":\"6\",\"callbackInfo\":\"com.muzhiyouwan.bzddt_60\",\"cpOrderId\":\"1313140479285526529\",\"notifyUrl\":\"http://171.221.254.163:9000/TSDK/pay/uc/payCallback\",\"sign\":\"7dc1953da71b5dfc8d8a389c86d867df\",\"signType\":\"MD5\"}";
						// payParams.setExtension(ex);
						JSONObject jo = new JSONObject(pObj.getExtension());
						accessInfo.setAccessToken(jo.getString("accessToken"));
						// accessInfo.setExpiresIn(jo.getString("expiresIn"));
						accessInfo.setOpenId(jo.getString("openId"));
						// accessInfo.setRefreshToken(jo.getString("refrestoken"));

						payInfo.setAppId(U8SDK.getInstance().getSDKParams().getString("appid"));
						payInfo.setPayKey(jo.getString("payKey"));
						// 设置CP透传信息，如果没有可以不设置
						// payInfo.setCpPrivate("This is a test private. NEW
						// 1112");
						// 商品名称
						payInfo.setName(pObj.getProductName());
						// 支付价格,单位为分
						payInfo.setPrice(jo.getInt("price"));
						// 设置商品编号
						payInfo.setPoint(jo.getInt("point"));
						// 商品数量，目前不支持多数量支付，设置为定值1
						payInfo.setQuantity(1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 如果没有订单号（不可重复），可不设置
				// payInfo.setCpOrder("8888");
				/*
				 * 如果不使用酷云账号，accessInfo 设置为null即可
				 */
				api.startPay(U8SDK.getInstance().getContext(), payInfo, accessInfo, payResult, pay_style,
						pay_orientation);
			}
		});
	}

	// 支付结果回调示例
	private IPayResult payResult = new IPayResult() {
		@Override
		public void onResult(CoolPayResult result) {
			if (null != result) {
				String resultStr = result.getResult();
				Log.d("mmddt", "resultStr:" + resultStr);
				Log.d("mmddt", "ResultStatus:" + result.getResultStatus());
			}
		}
	};

	public void doSubmitExtraData(UserExtraData extraData) {

	}

	public void doExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(U8SDK.getInstance().getContext());
		builder.setTitle("你确定要离开吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 这里添加点击确定后的逻辑
				// 退出账号
				mCoolcloud.logout(U8SDK.getInstance().getContext());
				dialog.dismiss();
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 这里添加点击确定后的逻辑
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public void showToastTips(final String aTips) {
		U8SDK.getInstance().getContext().runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(U8SDK.getInstance().getContext(), aTips, Toast.LENGTH_LONG).show();
			}
		});
	}

}
