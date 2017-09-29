package com.lion.ccpaydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.lion.ccpay.CCPaySdk;
import com.lion.ccpay.CCPaySdk$Stats;
import com.lion.ccpay.login.LoginListener;
import com.lion.ccpay.pay.PayListener;
import com.lion.ccpay.pay.vo.PayResult;
import com.lion.ccpay.user.vo.LoginResult;

/**
 * 支付
 * 
 * @author zhangbp
 * 
 */

public class ExampleActivity extends Activity implements OnClickListener {

	private EditText customAmount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_layout);
		findViewById(R.id.pay).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.customAmount).setOnClickListener(this);
		customAmount = (EditText) findViewById(R.id.price);
		CCPaySdk.getInstance().init(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int _id = v.getId();
		if (_id == R.id.pay) {
			CCPaySdk.getInstance().pay(this, "100045", "123456789", new PayListener() {

				@Override
				public void onComplete(PayResult result) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), result.statusCode + "-------" + result.trade_no + "-------" + result.msg, Toast.LENGTH_LONG).show();
				}
			});
		} else if (_id == R.id.login) {
			CCPaySdk.getInstance().login(this, new LoginListener() {

				@Override
				public void onComplete(LoginResult result) {
					if (result != null && result.isSuccess) {
						Toast.makeText(getApplicationContext(), "结果：" + result.toString(), Toast.LENGTH_SHORT).show();
						// DCAccount.login(result.userId + "");
					} else {
						Toast.makeText(getApplicationContext(), "结果：" + result.toString(), Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else if (_id == R.id.customAmount) {
			CCPaySdk.getInstance().pay(this, "101602", customAmount.getText().toString(), "123456789", new PayListener() {

				@Override
				public void onComplete(PayResult result) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), result.statusCode + "-------" + result.trade_no + "-------" + result.msg, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		CCPaySdk$Stats.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		CCPaySdk$Stats.onPause(this);
	}

}
