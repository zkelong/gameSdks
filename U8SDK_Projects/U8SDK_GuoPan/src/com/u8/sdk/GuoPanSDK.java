package com.u8.sdk;


import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPSDKPlayerInfo;
import com.flamingo.sdk.access.GPUploadPlayerInfoResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUploadPlayerInfoObsv;
import com.flamingo.sdk.access.IGPUserObsv;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class GuoPanSDK {
	private static GuoPanSDK instance;

	private String appID = "";
	private String appKey = "";

	private boolean mIsInitSuccess = false;
	private int mInitCount = 0;
	
	private GuoPanSDK() {

	}

	public static GuoPanSDK getInstance() {
		if (instance == null) {
			instance = new GuoPanSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);

		// 打开日志、发布状态切记不要打开
		GPApiFactory.getGPApi().setLogOpen(false);
		
		if (!mIsInitSuccess) {
			Log.d("ddt2", String.format("appid: %s \n appkey: %s", appID, appKey));
			mInitCount = 0;
			GPApiFactory.getGPApi().initSdk(context, appID, appKey, mInitObsv);
		} else {
			Log.d("ddt2", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		}
	}

	private IGPSDKInitObsv mInitObsv = new IGPSDKInitObsv() {

		@Override
		public void onInitFinish(GPSDKInitResult initResult) {
			
			Log.d("ddt2", String.format("aaaaaaaaaaaaaaaaaaaaaaaaa: %d", initResult.mInitErrCode));
			
            switch (initResult.mInitErrCode) {
                case GPSDKInitResult.GPInitErrorCodeConfig:
                	//初始化回调:初始化配置错误
                    retryInit();
                    break;
                    
                case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
                	//初始化回调:游戏需要更新
                    break;
                    
                case GPSDKInitResult.GPInitErrorCodeNet:
                	//初始化回调:初始化网络错误
                    retryInit();
                    break;
                    
                case GPSDKInitResult.GPInitErrorCodeNone:
                	//初始化回调:初始化成功
                	mIsInitSuccess = true;
                	mInitCount = 0;
                    break;
            }
		}
		
	};
	
	
	private void parseSDKParams(SDKParams params) {
		this.appID = params.getString("appid");
		this.appKey = params.getString("appkey");
	}

	public void doLogin() {
		GPApiFactory.getGPApi().login(U8SDK.getInstance().getApplication(), new IGPUserObsv() {
	        @Override
	        public void onFinish(final GPUserResult result) {
	            switch (result.mErrCode) {
	                case GPUserResult.USER_RESULT_LOGIN_FAIL:
	                //    writeLog("登录回调:登录失败");
	                    break;
	                    
	                case GPUserResult.USER_RESULT_LOGIN_SUCC:
	                	// 登陆成功
	                	String token = GPApiFactory.getGPApi().getLoginToken();
	                	String uin = GPApiFactory.getGPApi().getLoginUin();
	                	String name = GPApiFactory.getGPApi().getAccountName();
	                	
						String dataStr = String.format("{ uin:\"%s\", token:\"%s\", username:\"%s\" }", uin, token, name);
						U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
						U8SDK.getInstance().onLoginResult(dataStr);
	                	
	                    break;
	            }
	        }
	    });
	}

	public void doLogout() {
		
		GPApiFactory.getGPApi().logout();
		U8SDK.getInstance().onLogout();
		
	}
	
	public void doExit() {
		
		GPApiFactory.getGPApi().exit(new IGPExitObsv() {
	        @Override
	        public void onExitFinish(GPExitResult exitResult) {
	            switch (exitResult.mResultCode) {
	                case GPExitResult.GPSDKExitResultCodeError:
	                    break;
	                    
	                case GPExitResult.GPSDKExitResultCodeExitGame:
	                	
	                	U8SDK.getInstance().getContext().finish();
	                	System.exit(0);
	                    break;
	                    
	                case GPExitResult.GPSDKExitResultCodeCloseWindow:
	                    break;
	            }
	        }
	    });
		
	}

	public void doPay(Activity context, PayParams data) {
		GPSDKGamePayment payParam = createPayParam(data);
		
		GPApiFactory.getGPApi().buy(payParam, new IGPPayObsv() {
	        @Override
	        public void onPayFinish(GPPayResult payResult) {
	        	if (payResult == null)
	        		return;
	        	
	        	switch (payResult.mErrCode) {
	        	
		        	case GPPayResult.GPSDKPayResultCodeSucceed:
		        		U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "GuoPan SDK pay success.");
		        		break;
	        	}
	        }
	    });
	}

	
	public void doSubmitExtraData(UserExtraData extraData) {
		
		GPSDKPlayerInfo playerInfo = new GPSDKPlayerInfo();
		
		playerInfo.mGameLevel = extraData.getRoleLevel();
		playerInfo.mPlayerId = extraData.getRoleID();
		playerInfo.mPlayerNickName = extraData.getRoleName();
		playerInfo.mServerId = Integer.toString(extraData.getServerID()) ;
		playerInfo.mServerName = extraData.getServerName();
		
		GPApiFactory.getGPApi().uploadPlayerInfo(playerInfo, new IGPUploadPlayerInfoObsv() {
	        @Override
	        public void onUploadFinish(final GPUploadPlayerInfoResult gpUploadPlayerInfoResult) {
	        /*
	        	if (gpUploadPlayerInfoResult.mResultCode == GPUploadPlayerInfoResult.GPSDKUploadSuccess) {
	        		//上报数据回调:成功
	        	} else {
	        		//上报数据回调:失败
	        	}
	        */
	        }
	    });
	}
	
	
	/**
     * 重试初始化3次
     */
    public void retryInit() {
    	if (mInitCount < 3) {

    		mInitCount++;
    		GPApiFactory.getGPApi().initSdk(U8SDK.getInstance().getContext(), appID, appKey, mInitObsv);
    		
    	} else {
    		
    		Toast.makeText(U8SDK.getInstance().getContext(), "初始化失败，请检查网络", Toast.LENGTH_SHORT).show();
    		
    	}
    }
	
    // 设置购买参数
    private GPSDKGamePayment createPayParam(PayParams data) {
    	GPSDKGamePayment payParam = new GPSDKGamePayment();
    	
    	payParam.mItemId = data.getProductId();
    	payParam.mItemName = data.getProductName();
    	payParam.mPaymentDes = data.getProductDesc();
    	payParam.mItemPrice = data.getPrice();
    	payParam.mItemOrigPrice = data.getPrice();
    	payParam.mSerialNumber = data.getOrderID();
    	payParam.mCurrentActivity = U8SDK.getInstance().getContext();

    	return payParam;
    }
}
