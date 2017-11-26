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
		mErasurePaint.setStrokeCap(Paint.Cap.ROUND);
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

		width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);

		//创建一个Bitmap作为蒙版
		maskingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmapCanvas = new Canvas(maskingBitmap);//该画布为bitmap

		matrix.reset();
		//变换矩阵，使得图片大小与屏幕匹配
		matrix.postScale((float)width/mBitmapBackground.getWidth(),
				(float)height/mBitmapBackground.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//将bitmapBackground设置该View画布的背景
		canvas.drawBitmap(mBitmapBackground,matrix,null);

		//然后画布添加背景的基础上绘制蒙版
		canvas.drawBitmap(maskingBitmap, 0, 0, mBitmapPaint);
		bitmapCanvas.drawRect(0, 0, width, height, mPaintRect);

		//在蒙版对应的bitmap上绘制路径，绘制的地方会变成透明
		bitmapCanvas.drawPath(mPath, mErasurePaint);
	}

	public void setDrawPosition(float x, float y){
		//将移动的轨迹画成直线，很多个点就构成路径
		if (!isFirst)
			mPath.lineTo(x, y);
		isFirst = false;
		mPath.moveTo(x, y);
		invalidate();//更新画面
	}

}
