package kz.topsecurity.client.ui_widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCorneredImageView extends android.support.v7.widget.AppCompatImageView {
    public RoundCorneredImageView(Context context) {
        super(context);
    }

    public RoundCorneredImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCorneredImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * 0.5f);
//        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }

}
