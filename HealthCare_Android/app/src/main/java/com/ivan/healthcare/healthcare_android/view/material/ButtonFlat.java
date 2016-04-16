package com.ivan.healthcare.healthcare_android.view.material;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ButtonFlat extends Button {
	
	TextView textButton;
	int rippleColor;

	public ButtonFlat(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	protected void setDefaultProperties(){
		minHeight = 36;
		minWidth = 88;
		rippleSize = 3;
		// Min size
		setMinimumHeight(AppContext.dp2px(minHeight));
		setMinimumWidth(AppContext.dp2px(minWidth));
		setBackgroundResource(R.drawable.background_transparent);
	}

	@Override
	protected void setAttributes(AttributeSet attrs) {
		// Set text button
		String text;
		int textResource = attrs.getAttributeResourceValue(ANDROIDXML,"text",-1);
		if(textResource != -1){
			text = getResources().getString(textResource);
		}else{
			text = attrs.getAttributeValue(ANDROIDXML,"text");
		}
		if(text != null){
			textButton = new TextView(getContext());
			textButton.setText(text.toUpperCase());
			int textColorResource = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", -1);
			int textColor = -1;
			if (textColorResource == -1) {
				textColor = attrs.getAttributeIntValue(ANDROIDXML, "textColor", -1);
			} else {
				textColor = Compat.getColor(getContext(), textColorResource);
			}
			if (textColor == -1)
			{
				textColor = Compat.getColor(getContext(), R.color.textColorSecondary);
			}
			textButton.setTextColor(textColor);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			textButton.setLayoutParams(params);
			textButton.setGravity(Gravity.CENTER);
			addView(textButton);
		}
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,"background",-1);
		if(bacgroundColor != -1){
			setBackgroundColor(Compat.getColor(getContext(), bacgroundColor));
		}else{
			// Color by hexadecimal
			// Color by hexadecimal
			background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
			if (background != -1)
				setBackgroundColor(background);
		}

		int rippleColorResource = attrs.getAttributeResourceValue(MATERIALDESIGNXML, "ripple_color", -1);
		if (rippleColorResource == -1) {
			rippleColor = attrs.getAttributeIntValue(MATERIALDESIGNXML, "ripple_color", Color.parseColor("#88DDDDDD"));
		} else {
			rippleColor = Compat.getColor(getContext(), rippleColorResource);
		}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {
			
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(makePressColor());
			canvas.drawCircle(x, y, radius, paint);
			if(radius > getHeight()/rippleSize)
				radius += rippleSpeed;
			if(radius >= getWidth()){
				x = -1;
				y = -1;
				radius = getHeight()/rippleSize;
				if(onClickListener != null&& clickAfterRipple)
					onClickListener.onClick(this);
			}
			invalidate();
		}		
		
	}
	
	/**
	 * Make a dark color to ripple effect
	 */
	@Override
	protected int makePressColor(){
		return rippleColor;
	}
	
	public void setText(String text){
		textButton.setText(text.toUpperCase());
	}
	
	// Set color of background
	public void setBackgroundColor(int color){
		backgroundColor = color;
		if(isEnabled())
			beforeBackground = backgroundColor;
		textButton.setTextColor(color);
	}

	@Override
	public TextView getTextView() {
		return textButton;
	}
	
	public String getText(){
        	return textButton.getText().toString();
 	}

}
