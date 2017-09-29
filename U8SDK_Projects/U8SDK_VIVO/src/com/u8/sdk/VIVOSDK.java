package com.u8.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoRoleInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class VIVOSDK {
	private static VIVOSDK instance;

	private VIVOSDK() {

	}

	public static VIVOSDK getInstance() {
		if (instance == null) {
			instance = new VIVOSDK();
		}
		return instance;
	}

	private String m_userID = "";

	public void initSDK(final Activity context, final SDKParams params) {
		Log.d("mmddt", "params:" + params.toString());
		VivoUnionSDK.registerAccountCallback(U8SDK.getInstance().getContext(), m_accountCallback);
		U8SDK.getInstance().setActivityCallback(new IActivityCallback() {

			@Override
			public void onStop() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onResume() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRestart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPause() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNewIntent(Intent newIntent) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDestroy() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCreate() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBackPressed() {
				// TODO Auto-generated method stub
				VivoUnionSDK.exit(U8SDK.getInstance().getContext(), new VivoExitCallback() {

					@Override
					public void onExitConfirm() {
						// TODO Auto-generated method stub
						System.exit(0);
					}

					@Override
					public void onExitCancel() {
						// TODO Auto-generated method stub

					}
				});
			}

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				// TODO Auto-generated method stub

			}
		});

	}

	private VivoAccountCallback m_accountCallback = new VivoAccountCallback() {

		@Override
		public void onVivoAccountLogout(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onVivoAccountLoginCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onVivoAccountLogin(String aUserName, String aOpenID, String aToken) {
			// TODO Auto-generated method stub
			m_userID = aOpenID;
			final String dataStr = "{token:\"" + aToken + "\",userid:\"" + aOpenID + "\",userName:\"" + aUserName
					+ "\"}";
			U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
			U8SDK.getInstance().onLoginResult(dataStr);
		}
	};

	private VivoPayCallback m_payBack = new VivoPayCallback() {

		@Override
		public void onVivoPayResult(String aTransNumber, boolean aIsSucc, String aErrorCode) {
			// TODO Auto-generated method stub
			Log.d("mmddt", "vivo pay aTransNumber:" + aTransNumber + " ,error Code:" + aErrorCode + "success:"
					+ (aIsSucc ? "true" : "false"));
			if (aIsSucc) {
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "");
			}
		}
	};

	public void doLogin(int aPlatfrom) {
		m_userID = "";
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				VivoUnionSDK.login(U8SDK.getInstance().getContext());
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

		/*String ps = "{\"roleID\":\"0500183\",\"orderID\":\"1311304037169102850\",\"serverID\":\"5\",\"productName\":\"60钻石\",\"roleName\":\"紫霹的蔡遥\",\"check_url\":\"http://192.168.2.152:8080/TSDK/pay/apple/verify\",\"productID\":\"com.muzhiyouwan.bzddt_60\",\"price\":\"6\",\"extension\":{\"orderTime\":\"20170825110019\",\"orderDesc\":\"com.muzhiyouwan.bzddt_60\",\"extInfo\":\"1311304037169102850\",\"orderTitle\":\"com.muzhiyouwan.bzddt_60\",\"accessKey\":\"52af1f7283ce58c0cb0babc4f4ead470\",\"notifyUrl\":\"http://localhost:8080/vivo/payCallback\",\"transNo\":\"2017082511002188300011647830\",\"orderAmount\":\"600\",\"cpOrderNumber\":\"1311304037169102850\"},\"serverName\":\"5 服  test server\",\"roleLevel\":19}";

		try {
			data = parsePayParams(new JSONObject(ps));
		} catch (JSONException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		final PayParams pObj = data;

		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				String accessKey = "";
				String transNo = "";
				String proName = "";
				String proDesc = "";
				String price = "";
				if (pObj.getExtension().length() > 0) {
					try {
						JSONObject obj = new JSONObject(pObj.getExtension());
						accessKey = obj.getString("accessKey");
						transNo = obj.getString("transNo");
						proName = obj.getString("orderTitle");
						proDesc = obj.getString("orderDesc");
						price = obj.getString("orderAmount");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				VivoPayInfo info = new VivoPayInfo(proName, proDesc, price, accessKey,
						U8SDK.getInstance().getSDKParams().getString("appid"), transNo, m_userID);

				String s = info.toString();
				//Log.d("mmddt", "vivoPayInfo:" + s + ",userId:" + m_userID);
				VivoUnionSDK.pay(U8SDK.getInstance().getContext(), info, m_payBack);
			}
		});
	}

	public void doSubmitExtraData(UserExtraData extraData) {
		VivoUnionSDK.reportRoleInfo(new VivoRoleInfo(String.valueOf(extraData.getRoleID()),
				String.valueOf(extraData.getRoleLevel()), extraData.getRoleName(),
				String.valueOf(extraData.getServerID()), String.valueOf(extraData.getRoleLevel())));
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
