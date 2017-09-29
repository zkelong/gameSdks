package com.u8.sdk;

import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.tsixi.sdk.TSIXILoginResult;
import com.tsixi.sdk.TSIXISDK;
import com.tsixi.sdk.TSIXISDKLoginListener;

import android.app.Activity;
import android.util.Log;

public class TsixiSDK {
	private static TsixiSDK instance;
	private String m_ipayAppID;

	private TsixiSDK() {

	}

	public static TsixiSDK getInstance() {
		if (instance == null) {
			instance = new TsixiSDK();
		}
		return instance;
	}

	public void initSDK(Activity aContext, SDKParams params) {
		Log.d("mmddt2", "params value:    " + params.getString("app_id") + "channel:" + params.getString("channel_id"));
		if (!TSIXISDK.getInstance().initSDK(aContext, params.getString("channel_id"), params.getString("app_id"))) {
			Log.d("mmddt2", "tsixi sdk init");
			return;
		}
		
		TSIXISDK.getInstance().setLoginListener(new TSIXISDKLoginListener() {
			@Override
			public void onLogin(Boolean arg0, TSIXILoginResult arg1) {
				if (arg0) {
					String dataStr = String.format("{ 'token' : \"%s\", 'userid' : %s }", arg1.getSession(),
							arg1.getUserID());
					U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
					U8SDK.getInstance().onLoginResult(dataStr);
				} else {
					U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_FAIL, arg0.toString());
				}
			}
		});
		
		// 爱贝支付SDK初始化
		m_ipayAppID = params.getString("ipay_appid");
		
		IAppPay.init(aContext, IAppPay.LANDSCAPE, m_ipayAppID);		
	}

	public void doLogin() {
		TSIXISDK.getInstance().doLogin();
	}

	public void doLogout() {
		U8SDK.getInstance().runOnMainThread(new Runnable() {

			@Override
			public void run() {
				U8SDK.getInstance().onLogout();
			}
			
		});
	}

	public void doPay(Activity context, PayParams data) {
		String params = String.format("transid=%s&appid=%s&waresid=%s&waresname=%s&cporderid=%s&price=%d&currency=%s&appuserid=%s",
				data.getExtension(),
				m_ipayAppID,
				data.getProductId(),
				data.getProductName(),
				data.getOrderID(),
				data.getPrice(),
				"RMB – 人民币（单位：元）",
				data.getRoleId());
		
		Log.d("ddt2", params);
		
		IAppPay.startPay (context, params, new IPayResultCallback() {
			
			@Override
			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
				switch (resultCode) {
				case IAppPay.PAY_SUCCESS:
					//调用 IAppPayOrderUtils 的验签方法进行支付结果验证
				//	boolean payState = IAppPayOrderUtils.checkPayResult(signvalue, PayConfig.publicKey);
				//	if(payState){
				//		Toast.makeText(GoodsActivity.this, "支付成功", Toast.LENGTH_LONG).show();
				//	}
					
					U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "Tsixi-IAppPay SDK pay success.");
					
					break;
				case IAppPay.PAY_ING:
				//	Toast.makeText(GoodsActivity.this, "成功下单", Toast.LENGTH_LONG).show();
					break ;
				default:
				//	Toast.makeText(GoodsActivity.this, resultInfo, Toast.LENGTH_LONG).show();
					break;
				}
				Log.d("ddt2", "requestCode:"+resultCode + ",signvalue:" + signvalue + ",resultInfo:"+resultInfo);
			}
			
		});
	
	}
}
