package com.u8.sdk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import zty.sdk.game.GameSDK;
import zty.sdk.listener.ExitListener;
import zty.sdk.listener.ExitQuitListener;
import zty.sdk.listener.GameSDKLoginListener;

public class MuZhiSDK {
	private static MuZhiSDK instance;

	private MuZhiSDK() {

	}

	public static MuZhiSDK getInstance() {
		if (instance == null) {
			instance = new MuZhiSDK();
		}
		return instance;
	}

	public void initSDK(Activity context, SDKParams params) {
		//bugly init		
		// 获取当前包名
		String packageName = context.getPackageName();
		// 获取当前进程名
		String processName = getProcessName(android.os.Process.myPid());
		// 设置是否为上报进程
		UserStrategy strategy = new UserStrategy(context);
		strategy.setUploadProcess(processName == null || processName.equals(packageName));
		// 初始化Bugly
		CrashReport.initCrashReport(context, params.getString("BUGLY_APPID"), params.getBoolean("BUGLY_DEBUG_MODE"), strategy);
		// 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
		// CrashReport.initCrashReport(context, strategy);
		
		String channel = context.getPackageName();
		String version = "1.0";
		
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = String.format("/.2f", pi.versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CrashReport.setAppVersion(context,version);
		CrashReport.setAppVersion(context,params.getString("BUGLY_CHANNEL"));
		
		
		// 初始化SDK
		GameSDK.initSDK(context, new GameSDKLoginListener() {

			@Override
			public void onLoginCancelled() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onLoginSucess(String username, int userId, String sign) {
				// 登陆成功
				String dataStr = "{ \"sign\":\"" + sign + "\", \"userid\":\"" + userId + "\", \"username\":\"" + username + "\" }";
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
				U8SDK.getInstance().onLoginResult(dataStr);
			}
			
		}, false);
		
		// 传递系统事件给SDK
		U8SDK.getInstance().setActivityCallback(new ActivityCallbackAdapter() {
			
			@Override
			public void onPause() {
				GameSDK.onPause();
			}

			@Override
			public void onResume() {
				GameSDK.onResume();
			}
		});
	}

	public void doLogin() {
		GameSDK.Login();
	}

	ExitListener exitListener = new ExitListener() {
		@Override
		public void onExit(Object para) {
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	};
	
	ExitQuitListener exitQuitListener = new ExitQuitListener(){
		@Override
		public void onExitQuit(Object param) {
			//do nothing
		}
	};	
	
	public void doLogout() {
		U8SDK.getInstance().onLogout();
	}

	public void doPay(Activity context, PayParams data) {
		
		GameSDK.startPay(U8SDK.getInstance().getContext(),
				data.getServerId(), 
				data.getRoleLevel(), 
				data.getServerName(), 
				data.getRoleName(), 
				data.getOrderID() ,
				data.getPrice(), 
				10, 
				data.getProductName());
	}
	
	public void doSubmitExtraData(UserExtraData extraData) {
		
		GameSDK.afdf2Self(String.valueOf(extraData.getServerID()),//1 - 游戏服ID，1服为1，2服为2……
				extraData.getServerName(),//2 - 游戏服名称
				extraData.getRoleName(),//3 - 玩家角色名称
				Integer.parseInt(extraData.getRoleLevel())//4 - 玩家级别
				);
	}
	
	public void doExit()
	{
		GameSDK.afdfOut(U8SDK.getInstance().getContext(), exitListener, exitQuitListener, null);
	}	
	
	/**
	 * 获取进程号对应的进程名
	 * 
	 * @param pid 进程号
	 * @return 进程名
	 */
	private static String getProcessName(int pid) {
	    BufferedReader reader = null;
	    try {
	        reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
	        String processName = reader.readLine();
	        if (!TextUtils.isEmpty(processName)) {
	            processName = processName.trim();
	        }
	        return processName;
	    } catch (Throwable throwable) {
	        throwable.printStackTrace();
	    } finally {
	        try {
	            if (reader != null) {
	                reader.close();
	            }
	        } catch (IOException exception) {
	            exception.printStackTrace();
	        }
	    }
	    return null;
	}
}
