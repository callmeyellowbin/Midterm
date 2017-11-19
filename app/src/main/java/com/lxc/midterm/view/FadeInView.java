package com.lxc.midterm.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lxc.midterm.R;

/**
 * Created by LaiXiancheng on 2017/11/18.
 * Email: lxc.sysu@qq.com
 */

public class FadeInView extends View {
	private int width;
	private int height;
	private Paint mBitmapPaint;

	//蒙版Bitmap
	private Bitmap maskingBitmap;
	//该蒙版Bitmap的画布
	private Canvas bitmapCanvas;
	//擦除蒙版的画笔
	private Paint mErasurePaint;
	//蒙版画笔
	private Paint mPaintRect;

	//背景的Bitmap
	private Bitmap mBitmapBackground;
	private Matrix matrix = new Matrix();
	private Path mPath;
	//是不是第一笔
	private boolean isFirst = true;


	public FadeInView(Context context) {
		super(context, null);
	}

	public FadeInView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBitmapPaint = new Paint();//Bitmap的画笔

		//背景图
		mBitmapBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.sanguo_launch);


		mErasurePaint = new Paint();
		//抗锯齿
		mErasurePaint.setAntiAlias(true);
		mErasurePaint.setColor(Color.WHITE);
		//画笔宽度
		mErasurePaint.setStrokeWidth(350);
		//设置图形混合方式，这里使用PorterDuff.Mode.XOR模式，与底层重叠部分设为透明
		PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
		mErasurePaint.setXfermode(mode);
		mErasurePaint.setStyle(Paint.Style.STROKE);
		//设置笔刷的样式，默认为BUTT，如果设置为ROUND(圆形),SQUARE(方形)，需要将填充类型Style设置为STROKE或者FILL_AND_STROKE
		mErasurePaint.setStrokeCap(Paint.Cap.ROUND);
		//设置画笔的结合方式
		mErasurePaint.setStrokeJoin(Paint.Join.ROUND);


		//绘制蒙版的画笔
		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true);
		mPaintRect.setColor(Color.WHITE);

		//记录擦除点的路径
		mPath = new Path();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.d("onTouch", "onMeasure");
		width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);//设置宽和高

		//创建一个Bitmap，用于绘图。
		maskingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmapCanvas = new Canvas(maskingBitmap);//该画布为bitmap。

		matrix.reset();
		//变换矩阵，使得图片大小与屏幕匹配
		matrix.postScale((float)width/mBitmapBackground.getWidth(), (float)height/mBitmapBackground.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//将bitmapBackground设置该View画布的背景
		canvas.drawBitmap(mBitmapBackground,matrix,null);
		//然后画布添加背景的基础上添加bitmap
		canvas.drawBitmap(maskingBitmap, 0, 0, mBitmapPaint);
		bitmapCanvas.drawRect(0, 0, width, height, mPaintRect);//bitmap上绘制一个蒙版
		//bitmap上绘制路径，使用PorterDuff.Mode.XOR模式，因此与底层蒙版重叠部分设为透明
		bitmapCanvas.drawPath(mPath, mErasurePaint);
	}

	public void setDrawPosition(float x, float y){
		//将移动的轨迹画成直线，很多个点就构成路径
		if (!isFirst)
			mPath.lineTo(x, y);
		isFirst = false;
		mPath.moveTo(x, y);
		Log.d("onTouch", String.valueOf(x)+" "+String.valueOf(y));
		invalidate();//更新画面
	}

}
