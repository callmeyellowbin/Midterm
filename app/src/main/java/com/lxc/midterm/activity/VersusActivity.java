package com.lxc.midterm.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.lxc.midterm.R;

public class VersusActivity extends AppCompatActivity {
	LinearLayout plays_layout1;
	LinearLayout plays_layout2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus);
		plays_layout1 = (LinearLayout) findViewById(R.id.plays1);
		plays_layout2 = (LinearLayout) findViewById(R.id.plays2);
		//VersusRoleView roleView = new VersusRoleView(this, null);
		/*roleView.setName("张飞");
		roleView.setFiled("火");
		roleView.setAbility("5");
		plays_layout1.addView(roleView);
		roleView = new VersusRoleView(this, null, plays_layout1);
		roleView.setName("关羽");
		roleView.setFiled("水");
		roleView.setAbility("2");
		plays_layout1.addView(roleView);
		roleView = new VersusRoleView(this, null, plays_layout1);
		roleView.setName("赵云");
		roleView.setFiled("金");
		roleView.setAbility("3");
		plays_layout1.addView(roleView);*/

	}
}
