package com.shenqi.duokai.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenqi.duokai.R;
import com.shenqi.duokai.interf.OnSegmentButtonSelectedListener;


public class SegmentButton extends RelativeLayout implements OnClickListener {

	private boolean isLeftSelected;
	private TextView tvLeft;
	private TextView tvRight;
	
	private OnSegmentButtonSelectedListener onSegmentButtonSelectedListener;

	public void setOnSegmentButtonSelectedListener(OnSegmentButtonSelectedListener onSegmentButtonSelectedListener) {
		this.onSegmentButtonSelectedListener = onSegmentButtonSelectedListener;
	}

	public SegmentButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//加载布局   addView到第三个参数上
		//返回root  
		//如果第三个参数为空  返回第二个参数加载的布局View
		View.inflate(context, R.layout.view_segment_button, this);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SegmentButton);
		String leftString = typedArray.getString(R.styleable.SegmentButton_leftString);
		String rightString = typedArray.getString(R.styleable.SegmentButton_rightString);
		typedArray.recycle();
		
		tvLeft = (TextView) findViewById(R.id.tv_left);
		tvRight = (TextView) findViewById(R.id.tv_right);
		
		tvLeft.setText(leftString);
		tvRight.setText(rightString);
		
		tvLeft.setOnClickListener(this);
		tvRight.setOnClickListener(this);
		
		setLeftSelected(true);
	}
	
	/**
	 * 设置当前选择状态
	 * @param isLeftSelected
	 */
	public void setLeftSelected(boolean isLeftSelected) {
		this.isLeftSelected=  isLeftSelected;
		if (isLeftSelected) {
			//互斥  互相排斥 要么我死要么你亡   2选1
			tvLeft.setSelected(true);
			tvRight.setSelected(false);
//			Utils.toast(getContext(), "选择了左边");
		} else {
			tvRight.setSelected(true);
			tvLeft.setSelected(false);
//			Utils.toast(getContext(), "选择了右边");
		}
		

		//isLeftSelected当前的状态 直接传递过去
		if (onSegmentButtonSelectedListener != null) {
			onSegmentButtonSelectedListener.onLeftSelected(isLeftSelected);
		}
	}
	
	/**
	 * 获取当前选择状态
	 * @return
	 */
	public boolean isLeftSelected() {
		return isLeftSelected;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_left:
			setLeftSelected(true);
			break;
		case R.id.tv_right:
			setLeftSelected(false);
			break;

		default:
			break;
		}
	}

}

