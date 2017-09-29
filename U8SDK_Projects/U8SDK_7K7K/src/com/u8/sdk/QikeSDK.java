package com.u8.sdk;

import com.diannaoban.sdk.QikeGameSDK;
import com.diannaoban.sdk.QikeSDKStatusCode;
import com.diannaoban.sdk.bean.GameParamInfo;
import com.diannaoban.sdk.bean.GameRoleDto;
import com.diannaoban.sdk.iface.Listener.QikeCallbackListener;
import com.diannaoban.sdk.iface.Listener.QikeCallbackListenerNullException;
import com.diannaoban.sdk.pay.bean.PayArgs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class QikeSDK {
	private static QikeSDK instance;
	
	private String m_cpid = "";
	private String m_appid = "";
	private String m_appkey = "";
		
	
	private QikeSDK() {
		
	}
	
	public static QikeSDK getInstance() {
		if (instance == null) {
			instance = new QikeSDK();
		}
		return instance;
	}
	
	public void initSDK(Activity context, SDKParams params) {
		this.parseSDKParams(params);
		this.qikeSDKInit(context);
	}
	
	private void qikeSDKInit(Activity context) {
		try {
			GameParamInfo info = new GameParamInfo();
			QikeGameSDK.defaultSDK().initSDK(context, info, new QikeCallbackListener<String>() {

				@Override
				public void callback(int code, String data) {
					Log.e("QikeGameSDK", "QikeGameSDK初始化接口返回的数据- msg:" + data + ",code:" + code + "\n");
					switch (code) {
					// 初始化成功,可以执行后续的登录充值操作
					case QikeSDKStatusCode.SUCCESS:
						U8SDK.getInstance().onResult(U8Code.CODE_INIT_SUCCESS, data);
						break;
						
					default:
						break;
					}
				}
				
			});
		} catch (QikeCallbackListenerNullException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseSDKParams(SDKParams params) {
		this.m_cpid = params.getString("cpid");
		this.m_appid = params.getString("appid");
		this.m_appkey = params.getString("appkey");
	}
	
	// 登录接口回调。从这里可以获取登录结果。（必须）
	QikeCallbackListener<String> loginCallbackListener = new QikeCallbackListener<String>() {
		@Override
		public void callback(int code, String msg) {
			Log.e("QikeGameSDK", "QikeGameSDK登录接口返回数据:code=" + code + ",msg=" + msg);

			switch (code) {
			
			case QikeSDKStatusCode.SUCCESS:
				String sid = QikeGameSDK.defaultSDK().getSid(U8SDK.getInstance().getContext());
				String userid = QikeGameSDK.defaultSDK().getUserid(U8SDK.getInstance().getContext());//获取用户的安趣id
				String dataStr = String.format(" { sid : \"%s\", userid : \"%s\" }", sid, userid);
				U8SDK.getInstance().onResult(U8Code.CODE_LOGIN_SUCCESS, dataStr);
	            U8SDK.getInstance().onLoginResult(dataStr);
				break;
				
			case QikeSDKStatusCode.LOGOUT_SUCCESS:
				U8SDK.getInstance().onLogout();
				break;
				
			case QikeSDKStatusCode.NO_INIT:
				// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
				qikeSDKInit(U8SDK.getInstance().getContext());
				break;
				
			case QikeSDKStatusCode.LOGIN_EXIT:
				// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
				break;
			}
		}
	};

	//定义退出监听器  （必须）
	QikeCallbackListener<String> logoutcallback = new QikeCallbackListener<String>() {
		@Override
		public void callback(int statuscode, String data) {
			Log.e("QikeGameSDK", "登出接口返回数据:code=" + statuscode + ",data=" + data);
			if (statuscode == QikeSDKStatusCode.LOGOUT_SUCCESS) {
				QikeGameSDK.defaultSDK().release();
				U8SDK.getInstance().onLogout();
			}

		}
	};
	
	
	public void doLogin() {
		U8SDK.getInstance().runOnMainThread(new Runnable() {
			public void run() {
				try {
					
					QikeGameSDK.defaultSDK().login(U8SDK.getInstance().getContext(), loginCallbackListener);
					
					QikeGameSDK.defaultSDK().setLogoutCallbackListener(logoutcallback);
					
//					QikeGameSDK.defaultSDK().setCancleLoginListener(new CancleLoginListener() {
//						
//						@Override
//						public void onCancleLoginListener() {
//							Log.e("test", "cancle");
//						}
//					});
					
					QikeGameSDK.defaultSDK().setLogoutNotifyListener(new QikeCallbackListener<String>() {
						
						@Override
						public void callback(int statuscode, String data) {
							//Log.e("test", "logoutNotify");
							U8SDK.getInstance().onLogout();
						}
					});
					
				} catch (QikeCallbackListenerNullException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void doExit() {
		QikeGameSDK.defaultSDK().exitSDK(U8SDK.getInstance().getContext(), new QikeCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (QikeSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码
				} else if (QikeSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					//释放资源
					QikeGameSDK.defaultSDK().release();
				//	finish();
				}
			}
		});
	}
	
	public void doLogout() {
		try {

			QikeGameSDK.defaultSDK().logout(U8SDK.getInstance().getContext(), logoutcallback);
		} catch (QikeCallbackListenerNullException e) {
			// 未设置退出侦听器
		}
	}

	public void doPay(Activity context, PayParams data) { 
		PayArgs payArg = new PayArgs();
		payArg.amount = data.getPrice();
		payArg.appId = m_appid;
		payArg.appkey = m_appkey;
		payArg.customerorderId = data.getOrderID();
		payArg.roleId = data.getRoleId();
		payArg.productname = data.getProductName();
		payArg.body = data.getProductName();
		
		try {
			QikeGameSDK.defaultSDK().pay(context, payArg, payResultListener);
		} catch (QikeCallbackListenerNullException e) {
			// 异常处理
		}

	}
	
	/**
	 * 支付回调（必须）
	 */
	private QikeCallbackListener<PayArgs> payResultListener = new QikeCallbackListener<PayArgs>() {
		@Override
		public void callback(int statuscode, PayArgs payArg) {
			switch (statuscode) {
			
			case QikeSDKStatusCode.NO_INIT:
				break;
				
			case QikeSDKStatusCode.SUCCESS:
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_SUCCESS, "7k7k SDK pay success.");
				break;
				
			case QikeSDKStatusCode.FAIL:
				U8SDK.getInstance().onResult(U8Code.CODE_PAY_FAIL, "7k7k SDK pay fail.");
				break;
				
			case QikeSDKStatusCode.PAY_USER_EXIT:
				break;
			}
		}
	};
	
	private PayArgs decodePayParams(PayParams data) {
		PayArgs payArg = new PayArgs();
		
		payArg.amount = data.getPrice();
		payArg.customerorderId = data.getOrderID();
		payArg.productname = data.getProductName();
		
		return payArg;
	}
	
	/**
	 * 必接功能<br>
	 * 提交游戏扩展数据功能，游戏SDK要求游戏在运行过程中，提交一些用于运营需要的扩展数据，这些数据通过扩展数据提交方法进行提交。
	 * 登录游戏角色成功后调用此段
	 */
	private void QikeSdkSubmitExtendData(String userid) {
		try {
			//CP可以根据userid获取该用户的扩展信息....
			GameRoleDto gameRole = new GameRoleDto();
			gameRole.setGameName("笑傲江湖");
			gameRole.setRoleId("R0010");
			gameRole.setRoleName("令狐冲1111");
			gameRole.setRoleLevel("99");
			gameRole.setZoneId("192825");
			gameRole.setZoneName("游戏一区-逍遥谷");
			
			QikeGameSDK.defaultSDK().submitExtendData(U8SDK.getInstance().getContext(), gameRole);
			Log.e("QikeGameSDK", "提交游戏扩展数据功能调用成功");
		} catch (Exception e) {
			// 处理异常
		}
	}
}
