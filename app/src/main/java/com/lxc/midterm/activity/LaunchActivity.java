package com.lxc.midterm.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.lxc.midterm.R;
import com.lxc.midterm.view.FadeInView;

public class LaunchActivity extends AppCompatActivity {

	//目前绘制的行号
	int curLine = 1;
	FadeInView fadeInView;

	ValueAnimator anim;
	boolean isOpened = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_launch);
		fadeInView = findViewById(R.id.fi_view);

		//fadeInView加载完成时回调这个方法，开启动画
		fadeInView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				startAnim(fadeInView);
			}
		});
	}


	//每一个绘制行之间的距离（px）
	final int GAP = 200;

	private void startAnim(final FadeInView fadeInView) {
		final int mWidth = fadeInView.getWidth();
		int mHeight = fadeInView.getHeight();

		//计算总共要绘制的行数
		final int lineNum = mHeight/GAP;

		//用数值动画对绘制点进行控制
		anim = ValueAnimator.ofFloat(0f, lineNum*mWidth);
		anim.setDuration(2000);
		//数值更新监听器，控制绘制的点
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			//onAnimationUpdate回调的次数
			int cnt = 0;
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float currentValue = (float) animation.getAnimatedValue();
				cnt++;
				//隔两次才绘制一次，这样减少了绘制的压力，而且画笔宽度足够大不用担心有地方绘制不到
				if (!(cnt % 3 == 0)){
					return;
				}
				//计算当前行数
				if (currentValue > curLine*mWidth)
					curLine++;
				//控制x轴的折返（从左往右，然后变成从右往左，然后左往右……）
				float x = curLine % 2 == 1 ? currentValue % mWidth : mWidth - (currentValue % mWidth);
				float y = curLine==1 ? 100 : curLine*GAP - 100;
				fadeInView.setDrawPosition(x, y);
			}
		});
		//数值动画结束的监听器，动画结束开启MainActivity
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				openMainActivity();
			}
		});
		//匀速动画
		anim.setInterpolator(new LinearInterpolator());
		anim.start();

	}

	private void openMainActivity(){
		if (isOpened)
			return;
		isOpened = true;

		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		finish();
	}
}
