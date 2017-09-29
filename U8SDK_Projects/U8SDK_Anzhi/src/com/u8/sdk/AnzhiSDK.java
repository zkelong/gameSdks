package com.u8.sdk;


import org.json.JSONException;
import org.json.JSONObject;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.AzOutGameInter;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.anzhi.usercenter.sdk.item.UserInfo;

import android.app.Activity;
import android.util.Log;

public class AnzhiSDK {
	private static AnzhiSDK instance;

	private AnzhiUserCenter mAnzhiCenter;
	private Activity mActivity;
	
	private static final String KEY_LOGIN = "key_login";   // 登录的key
    private static final String KEY_LOGOUT = "key_logout"; // 登出的KEY
	private static final String KEY_PAY = "key_pay";       // 支付的key
	
    private static final String JS_CALLBACK_KEY = "callback_key";
	
    private static final String JS_SID = "sid";
    private static final String JS_UID = "uid";
    
    private String mGameName;
    
	private AnzhiSDK() {

	}

	public static AnzhiSDK getInstance() {
		if (instance == null) {
			instance = new AnzhiSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
	
		mGameName = params.getString("GameName");
		
		final CPInfo info = new CPInfo();
        info.setOpenOfficialLogin(false);// 是否开启游戏官方账号登录，默认为关闭
        info.setAppKey(params.getString("AppKey"));
        info.setSecret(params.getString("AppSecret"));
        info.setChannel(params.getString("Channel"));
        info.setGameName(params.getString("GameName"));
        
        mAnzhiCenter = AnzhiUserCenter.getInstance();
        mActivity = U8SDK.getInstance().getContext();
        
        mAnzhiCenter.setKeybackCall(mKeyCall); // 设置返回游戏的通知
        mAnzhiCenter.azinitSDK(mActivity, info, mInitSDKCallback, mGameInter);
        mAnzhiCenter.setCallback(mCallback);// 设置 登录、登出、支付 回调
        mAnzhiCenter.setActivityOrientation(0);// 0横屏,1竖屏,4根据物理感应来选择方向
	}

	private KeybackCall mKeyCall = new KeybackCall() {
        @Override
        public void KeybackCall(String Call) {
            Log.e("xugh", "Call == " + Call);
        }
    };
	
    /**
     * 初始化完成后的回调
     */
    private InitSDKCallback mInitSDKCallback = new InitSDKCallback() {
        @Override
        public void initSdkCallcack() {
        	Log.d("ddt2", "Anzhi InitSDKCallback.");
        }
    };
	
    /**
     * 退出游戏的接口，开发者在本接口中实现退出游戏的方法。 在安智的退出弹窗页点退出游戏或在三秒内连续两次调用 azoutGame(boolean)方法会调用本接口
     * 
     * 根据标示码判读是否退出游戏
     * 
     */
    private AzOutGameInter mGameInter = new AzOutGameInter() {
        @Override
        public void azOutGameInter(int arg) {

            switch (arg) {
            case AzOutGameInter.KEY_OUT_GAME:// 退出游戏
            	mAnzhiCenter.removeFloaticon(mActivity);
                mActivity.finish();
                System.exit(0);
                break;
                
            case AzOutGameInter.KEY_CANCEL: // 取消退出游戏
                break;
                
            default:
                break;
            }

        }
    };
    
    /**
     * 登录、登出、支付通知
     */
    private AnzhiCallback mCallback = new AnzhiCallback() {

        @Override
        public void onCallback(CPInfo cpInfo, final String result) {
        	Log.e("ddt2", "AnzhiCallback: " + result);
        	
			try {
				JSONObject json = new JSONObject(result);
				String key = json.optString(JS_CALLBACK_KEY);
				
	            if (KEY_PAY.equals(key)) {// 支付结果通知
	            	
	            	Log.e("ddt2", "AnzhiCallback: KEY_PAY");
	            	
	            } else if (KEY_LOGOUT.equals(key)) {// 切换或退出账号的通知

	            	U8SDK.getInstance().onLogout();
	            	
	            } else if (KEY_LOGIN.equals(key)) {// 登录游戏的方法

	            	String uid = json.optString(JS_UID);
	            	String sid = json.optString(JS_SID);
	            	
	            	String dataStr = String.format("{ sid:\"%s\", uid:\"%s\" }", sid, uid);
	            	
	            	Log.d("ddt2", dataStr);
	            	
	            	U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
	            	U8SDK.getInstance().onLoginResult(dataStr);

	            }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

        }
     
    };
    

	public void doLogin() {
		mAnzhiCenter.login(mActivity, true);
	}

	public void doLogout() {
		mAnzhiCenter.logout(mActivity);
	}
	
	public void doExit() {
		mAnzhiCenter.azoutGame(false);
	}

	public void doPay(Activity context, PayParams data) {
		Log.d("ddt2", "Anzhi pay: " + data.getProductName() + "-----" + data.getOrderID());
		String info = data.getOrderID();
		mAnzhiCenter.pay(mActivity, 0, data.getPrice(), data.getProductName(), info);
		
	}

	
	public void doSubmitExtraData(UserExtraData extraData) {
		
		UserInfo userInfo = mAnzhiCenter.getUserInfo();
		
		UserGameInfo gameInfo = new UserGameInfo();
        gameInfo.setNickName(userInfo.getNickname());
        gameInfo.setUid(userInfo.getUid());
        gameInfo.setAppName(mGameName);
        gameInfo.setGameArea(extraData.getServerName());// 游戏的服务器区
        gameInfo.setGameLevel(extraData.getRoleLevel());// 游戏角色等级
        gameInfo.setUserRole(extraData.getRoleName());// 角色名称
        gameInfo.setMemo("");// 备注
        
        mAnzhiCenter.submitGameInfo(mActivity, gameInfo);
	}
	
}
