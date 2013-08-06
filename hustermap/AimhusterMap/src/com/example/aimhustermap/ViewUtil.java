package com.example.aimhustermap;

import android.view.View;

public class ViewUtil {
	private int width;
	private int height;
	private int top;
	private int left;
	private int right;
	private int bottom;
	private View view;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTop() {
		return top;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public int getBottom() {
		return bottom;
	}

	public ViewUtil(View view) {
		super();
		this.view = view;
		MeasureView();
	}

	private void MeasureView()
	{
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		width = view.getMeasuredWidth();
		height = view.getMeasuredHeight();
		int[] location = new int[2];  
		view.getLocationOnScreen(location);  
		left=location[0];
		top=location[1];
		right=left+width;
		bottom=top+height;
	}
}
