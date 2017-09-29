package com.u8.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import com.lenovo.lsf.gamesdk.GamePayRequest;
import com.lenovo.lsf.gamesdk.IAuthResult;
import com.lenovo.lsf.gamesdk.IPayResult;
import com.lenovo.lsf.gamesdk.LenovoGameApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.Toast;

public class LenovoSDK {
	private static LenovoSDK instance;
	private int pay_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	private AlertDialog.Builder alertDialogBuilder;

	private LenovoSDK() {

	}

	public static LenovoSDK getInstance() {
		if (instance == null) {
			instance = new LenovoSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, final SDKParams params) {
		// SDK初始化
		LenovoGameApi.doInit(context, params.getString("appid"));
	}

	public void doLogin(int aPlatfrom) {

		// 请不要在回调函数里进行UI操作，如需进行UI操作请使用handler将UI操作抛到主线程
		LenovoGameApi.doAutoLogin(U8SDK.getInstance().getContext(), new IAuthResult() {

			@Override
			public void onFinished(boolean ret, String data) {
				if (ret) {
					String token = data;
					String dataStr = "{ \"token\" : \"" + token + "\" }";
					U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
					U8SDK.getInstance().onLoginResult(dataStr);

				} else {
					// 后台快速登录失败(失败原因开启飞行模式、 网络不通等)
					showToastTips("登陆失败");
					// Log.i(Config.TAG, "login fail");
				}
			}
		});
	}

	public void doLogin() {
		doLogin(0);
	}

	public void doLogout() {
		// 这里添加点击确定后的逻辑
		// 退出账号
		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {
		Log.d("mmddt", "ppppppp1");
		final PayParams pObj = data;

		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				/***********
				 * 支付LenovoGameApi.doPay（） 接口 调用
				 */
				GamePayRequest payRequest = new GamePayRequest();
				Log.d("mmddt", "ppppppp2");
				/*/
				pObj.setExtension(
						"{\"exorderno\":\"1314121574369984518\",\"price\":600,\"waresid\":6,\"cpprivateinfo\":\"1708072120979.app.ln\"}");
				//*/
				if (pObj.getExtension().length() > 0) {
					try {
						//
						// 08-30 10:19:01.502: D/ddt2(4811): ThirdSdk doPay :
						// {"roleID":"0500202","orderID":"1314106314351181825","serverID":"5","productName":"60钻石","roleName":"","check_url":"http://192.168.2.66:8080/TSDK/pay/apple/verify","productID":"com.muzhiyouwan.bzddt_60","price":"6","extension":{"exorderno":"1314106314351181825","price":600,"waresid":6,"cpprivateinfo":"1708072120979.app.ln"},"serverName":"5
						// 服 test server","roleLevel":19}
						// payParams.setExtension(ex);
						JSONObject jo = new JSONObject(pObj.getExtension());
						Log.d("mmddt", "ppppppp3");
						// 请填写商品自己的参数
						payRequest.addParam("notifyurl", "");// 当前版本暂时不用，传空String
						payRequest.addParam("appid", U8SDK.getInstance().getSDKParams().getString("appid"));
						payRequest.addParam("waresid", Integer.parseInt(jo.getString("waresid")));
						payRequest.addParam("exorderno", jo.getString("exorderno"));
						payRequest.addParam("price", Integer.parseInt(jo.getString("price")));
						payRequest.addParam("cpprivateinfo", jo.getString("cpprivateinfo"));
						Log.d("mmddt",
								"pay pa sid:" + Integer.parseInt(jo.getString("waresid")) + "exorderno:"
										+ jo.getString("exorderno") + "price:" + Integer.parseInt(jo.getString("price"))
										+ "cpprivateinfo:" + jo.getString("cpprivateinfo") + "appid:"
										+ U8SDK.getInstance().getSDKParams().getString("appid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				LenovoGameApi.doPay(U8SDK.getInstance().getContext(),
						U8SDK.getInstance().getSDKParams().getString("appkey"), payRequest, new IPayResult() {
							@Override
							public void onPayResult(int resultCode, String signValue, String resultInfo) {
								if (LenovoGameApi.PAY_SUCCESS == resultCode) {
									// 支付成功
									// Toast.makeText(GoodsListActivity.this,
									// "sample:支付成功",
									// Toast.LENGTH_SHORT).show();
								} else if (LenovoGameApi.PAY_CANCEL == resultCode) {
									// Toast.makeText(GoodsListActivity.this,
									// "sample:取消支付",
									// Toast.LENGTH_SHORT).show();
									// 取消支付处理，默认采用finish()，请根据需要修改
									// Log.e(Config.TAG, "return cancel");
								} else {
									// Toast.makeText(GoodsListActivity.this,
									// "sample:支付失败",
									// Toast.LENGTH_SHORT).show();
									// 计费失败处理，默认采用finish()，请根据需要修改
									// Log.e(Config.TAG, "return Error");
								}

							}
						});
			}
		});
	}

	public void doSubmitExtraData(UserExtraData extraData) {

	}

	public void doExit() {
		LenovoGameApi.doQuit(U8SDK.getInstance().getContext(), new IAuthResult() {
			@Override
			public void onFinished(boolean result, String data) {
				Log.i("demo", "onFinished：" + data);
				if (result) {
					U8SDK.getInstance().getContext().finish();
					System.exit(0);
				} else {
					// "用户点击底部返回键或点击弹窗close键"
				}
				/*
				 * if(result == 0){//"用户点击弹窗close键"
				 * GameLauncherActivity.this.finish(); System.exit(0); }else
				 * if(result == -1){//"用户点击底部返回键" }else if(result ==
				 * -2){//"用户点击弹窗open键" GameLauncherActivity.this.finish();
				 * System.exit(0); }
				 */
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
