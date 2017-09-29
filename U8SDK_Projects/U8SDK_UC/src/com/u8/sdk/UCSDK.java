package com.u8.sdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import cn.uc.gamesdk.UCGameSdk;
import cn.uc.gamesdk.even.SDKEventKey;
import cn.uc.gamesdk.even.SDKEventReceiver;
import cn.uc.gamesdk.even.Subscribe;
import cn.uc.gamesdk.exception.AliLackActivityException;
import cn.uc.gamesdk.exception.AliNotInitException;
import cn.uc.gamesdk.open.GameParamInfo;
import cn.uc.gamesdk.open.OrderInfo;
import cn.uc.gamesdk.open.UCLogLevel;
import cn.uc.gamesdk.open.UCOrientation;
import cn.uc.gamesdk.param.SDKParamKey;

public class UCSDK {

	enum SDKState {
		StateDefault, StateIniting, StateInited, StateLogin, StateLogined
	}

	private static final String TAG = "MMDDT";
	private Handler handler;

	private SDKState state = SDKState.StateDefault;
	private boolean loginAfterInit = false;
	private ProgressDialog loadingActivity = null;

	private static UCSDK instance;

	private UCLogLevel logLevel = UCLogLevel.DEBUG;

	private long cpId;
	private long gameId;
	private boolean debugMode = true;

	private boolean isSwitchAccount = false;
	public boolean mRepeatCreate = false;

	private UCSDK() {

	}

	public static UCSDK getInstance() {
		if (instance == null) {
			instance = new UCSDK();
		}
		return instance;
	}

	private void parseSDKParams(SDKParams params) {
		this.gameId = params.getInt("UCGameId");
		this.cpId = params.getInt("UCCpId");

		this.debugMode = params.getBoolean("UCDebugMode");

		this.logLevel = this.debugMode ? UCLogLevel.DEBUG : UCLogLevel.ERROR;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		this.initSDK(context);
	}

	public void logout() {
		try {
			UCGameSdk.defaultSdk().logout(U8SDK.getInstance().getContext(), null);
		} catch (AliLackActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AliNotInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void switchAccount() {
		this.isSwitchAccount = true;
		logout();
	}

	private SDKEventReceiver receiver = new SDKEventReceiver() {
		@Subscribe(event = SDKEventKey.ON_INIT_SUCC)
		private void onInitSucc() {
			// 初始化成功
			handler.post(new Runnable() {

				@Override
				public void run() {
					// startGame();
				}
			});
		}

		@Subscribe(event = SDKEventKey.ON_INIT_FAILED)
		private void onInitFailed(String data) {
			// 初始化失败
			// Toast.makeText(U8SDK.getInstance().getContext(), "init failed",
			// Toast.LENGTH_SHORT).show();
			// ucNetworkAndInitUCGameSDK(null);
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
		private void onLoginSucc(String sid) {
			// Toast.makeText(U8SDK.getInstance().getContext(), "login
			// succ,sid=" + sid, Toast.LENGTH_SHORT).show();
			String dataStr = "{ \"token\" : \"" + sid + "\" }";
			U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
			U8SDK.getInstance().onLoginResult(dataStr);
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
		private void onLoginFailed(String desc) {

			// printMsg(desc);
		}

		@Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
		private void onCreateOrderSucc(OrderInfo orderInfo) {
			if (orderInfo != null) {
				String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
				String ordereId = orderInfo.getOrderId();// 获取订单号
				float orderAmount = orderInfo.getOrderAmount();// 获取订单金额
				int payWay = orderInfo.getPayWay();
				String payWayName = orderInfo.getPayWayName();
				System.out.print(ordereId + "," + orderAmount + "," + payWay + "," + payWayName);
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "ucsdk pay success.");
			}
			Log.i(TAG, "pay create succ");
		}

		@Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
		private void onPayUserExit(OrderInfo orderInfo) {
			if (orderInfo != null) {
				String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
			}
			Log.i(TAG, "pay exit");
		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_SUCC)
		private void onLogoutSucc() {
			U8SDK.getInstance().onLogout();
		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_FAILED)
		private void onLogoutFailed() {
			// Toast.makeText(GameActivity.this, "logout failed",
			// Toast.LENGTH_SHORT).show();
			// printMsg("注销失败");
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
		private void onExit(String desc) {
			// Toast.makeText(GameActivity.this, desc,
			// Toast.LENGTH_SHORT).show();

			// 退出程序
			// Intent intent = new Intent(Intent.ACTION_MAIN);
			// intent.addCategory(Intent.CATEGORY_HOME);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());
			// printMsg(desc);
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
		private void onExitCanceled(String desc) {
			// Toast.makeText(GameActivity.this, desc,
			// Toast.LENGTH_SHORT).show();
		}

	};

	private void ucSdkInit() {
		Activity context = U8SDK.getInstance().getContext();
		if ((context.getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			Log.i("mmddt", "onCreate with flag FLAG_ACTIVITY_BROUGHT_TO_FRONT");
			mRepeatCreate = true;
			context.finish();
			return;
		}
		cn.uc.gamesdk.param.SDKParams params = new cn.uc.gamesdk.param.SDKParams();
		params.put("cpId", this.cpId);
		params.put("gameId", this.gameId);
		params.put("serverId", 0L);
		params.put("enablePayHistory",
				Boolean.parseBoolean(U8SDK.getInstance().getSDKParams().getString("enablePayHistory")));
		params.put("enableUserCharge",
				Boolean.parseBoolean(U8SDK.getInstance().getSDKParams().getString("enableUserCharge")));

		GameParamInfo gpi = new GameParamInfo();// 下面的值仅供参考
		gpi.setCpId(params.get("cpId", 0L).intValue());
		gpi.setGameId(params.get("gameId", 0L).intValue());
		gpi.setServerId(params.get("serverId", 0L).intValue());
		gpi.setEnablePayHistory(params.get("enablePayHistory", false));
		gpi.setEnableUserChange(params.get("enableUserCharge", false));
		gpi.setOrientation(UCOrientation.LANDSCAPE);

		params.put("gameParams", gpi);

		// 联调环境已经废用
		// sdkParams.put(SDKParamKey.DEBUG_MODE, UCSdkConfig.debugMode);

		try {
			UCGameSdk.defaultSdk().initSdk(context, params);
		} catch (AliLackActivityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 必接功能<br>
	 * sdk初始化功能<br>
	 */
	public void initSDK(final Activity context) {
		ucSdkInit();// 执行UCGameSDK初始化
		UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);

		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {

			@Override
			public void onDestroy() {
				// TODO Auto-generated method stub
				if (!mRepeatCreate) {
					UCGameSdk.defaultSdk().unregisterSDKEventReceiver(receiver);
					receiver = null;
				}
				super.onDestroy();
			}
		});
	}

	public void login() {
		try {
			UCGameSdk.defaultSdk().login(U8SDK.getInstance().getContext(), null);
		} catch (AliLackActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AliNotInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 提交扩展数据
	public void submitExtendData(UserExtraData extraData) {
		cn.uc.gamesdk.param.SDKParams sdkParams = new cn.uc.gamesdk.param.SDKParams();
		sdkParams.put(SDKParamKey.STRING_ROLE_ID, extraData.getRoleID());
		sdkParams.put(SDKParamKey.STRING_ROLE_NAME, extraData.getRoleName());
		sdkParams.put(SDKParamKey.LONG_ROLE_LEVEL, extraData.getRoleLevel());
		sdkParams.put(SDKParamKey.LONG_ROLE_CTIME, extraData.getRoleCTime());
		sdkParams.put(SDKParamKey.STRING_ZONE_ID, extraData.getServerID());
		sdkParams.put(SDKParamKey.STRING_ZONE_NAME, extraData.getServerName());

		try {
			UCGameSdk.defaultSdk().submitRoleData(U8SDK.getInstance().getContext(), sdkParams);
			// Toast.makeText(GameActivity.this,
			// "数据已提交，查看数据是否正确，请到开放平台接入联调工具查看", Toast.LENGTH_SHORT).show();
		} catch (AliNotInitException e) {
			e.printStackTrace();
		} catch (AliLackActivityException e) {
			e.printStackTrace();
		}
	}

	private cn.uc.gamesdk.param.SDKParams decodePayParams(PayParams payParams) {
		cn.uc.gamesdk.param.SDKParams pInfo = new cn.uc.gamesdk.param.SDKParams(); // 创建Payment对象，用于传递充值信息
		Map<String, String> paramMap = new HashMap<String, String>();
		if (payParams.getExtension().length() > 0) {

			try {
				//String ex = "{\"accountId\":\"4aa75da6b260f9c30b48424beebed30b\",\"amount\":\"6\",\"callbackInfo\":\"com.muzhiyouwan.bzddt_60\",\"cpOrderId\":\"1313140479285526529\",\"notifyUrl\":\"http://171.221.254.163:9000/TSDK/pay/uc/payCallback\",\"sign\":\"7dc1953da71b5dfc8d8a389c86d867df\",\"signType\":\"MD5\"}";
				//payParams.setExtension(ex);
				JSONObject jo = new JSONObject(payParams.getExtension());
				paramMap.put(SDKParamKey.CALLBACK_INFO, jo.getString("callbackInfo"));
				paramMap.put(SDKParamKey.NOTIFY_URL, jo.getString("notifyUrl"));
				paramMap.put(SDKParamKey.AMOUNT, jo.getString("amount"));
				paramMap.put(SDKParamKey.CP_ORDER_ID, jo.getString("cpOrderId"));
				paramMap.put(SDKParamKey.ACCOUNT_ID, jo.getString("accountId"));
				paramMap.put(SDKParamKey.SIGN_TYPE, jo.getString("signType"));
				paramMap.put(SDKParamKey.SIGN, jo.getString("sign"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(paramMap);

		Log.d("mmddt", "maps:" + map.toString());
		Log.d("mmddt", "paramMap:" + paramMap.toString());

		pInfo.putAll(map);
		Log.d("mmddt", "pInfo:" + pInfo.toString());
		return pInfo;
	}

	public void pay(Activity context, PayParams data) {
		Log.d("mmddt2", "ucsdk pay");

		if (!SDKTools.isNetworkAvailable(context)) {
			U8SDK.getInstance().onResult(U8Code.CODE_NO_NETWORK, "The network now is unavailable");
			return;
		}

		cn.uc.gamesdk.param.SDKParams pInfo = decodePayParams(data);

		try {
			UCGameSdk.defaultSdk().pay(U8SDK.getInstance().getContext(), pInfo);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AliLackActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AliNotInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showProgressDialog(Activity context) {
		if (loadingActivity != null) {
			return;
		}

		loadingActivity = new ProgressDialog(context);
		loadingActivity.setIndeterminate(true);
		loadingActivity.setCancelable(true);
		loadingActivity.setMessage("正在初始化，请稍后...");
		loadingActivity.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				state = SDKState.StateDefault;
			}
		});
		loadingActivity.show();
	}

	private void hideProgressDialog(Activity context) {
		if (loadingActivity == null) {
			return;
		}
		loadingActivity.dismiss();
		loadingActivity = null;
	}

	public void ucSdkExit(Activity context) {
		try {
			UCGameSdk.defaultSdk().exit(U8SDK.getInstance().getContext(), null);
		} catch (AliLackActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AliNotInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
