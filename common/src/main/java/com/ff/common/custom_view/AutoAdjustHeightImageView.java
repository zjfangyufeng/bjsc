package com.ff.common.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AutoAdjustHeightImageView extends ImageView {
	private int imageWidth;
	private int imageHeight;
	Context c;

	public AutoAdjustHeightImageView(Context context,
									 AttributeSet attrs) {
		super(context, attrs);
		c = context;
	}

	private void getImageSize() {
		try {
			Drawable background = getDrawable();
			if (background == null)
				return;
			Bitmap bitmap = ((BitmapDrawable) background).getBitmap();
			imageWidth = bitmap.getWidth();
			imageHeight = bitmap.getHeight();
		} catch (Exception e) {
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		getImageSize();
		if (imageWidth == 0 || imageHeight == 0)
			return;
		ViewGroup.LayoutParams lp = getLayoutParams();
		if (lp == null)
			return;
		int measuredWidth = getMeasuredWidth();
		if(measuredWidth<=0){
			return;
		}
//		if(measuredWidth<=0){
//			measure(lp.width,lp.height);
//			measuredWidth = getMeasuredWidth();
//		}
		lp.width = measuredWidth;
		lp.height = lp.width * imageHeight / imageWidth;
		setLayoutParams(lp);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		getImageSize();
		if (imageWidth == 0 || imageHeight == 0)
			return;
		setMeasuredDimension(getMeasuredWidth(),getMeasuredWidth()* imageHeight / imageWidth);
	}

	public View v;

	@Override
	public void setLayoutParams(ViewGroup.LayoutParams params) {
		super.setLayoutParams(params);
		if(v!=null){
			v.setLayoutParams(params);
		}
	}

}