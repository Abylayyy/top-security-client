package kz.topsecurity.client.ui_widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import kz.topsecurity.client.R;

public class CustomSwitch extends android.support.v7.widget.AppCompatImageView {

    boolean isInititialized = false;
    boolean isChecked = false;
    int[] imageResources = new int[2];

    public CustomSwitch(Context context) {
        super(context);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChecked(boolean b){
        setImageResource(getImageRes(b));
        isChecked = b;
    }

    private int getImageRes(boolean b){
        if(isInititialized)
            return b ? imageResources[0] : imageResources[1];
        else
            return 0;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setImageResources(int activeRes , int notActiveRes){
        setActiveImageRes(activeRes);
        setNotActiveImageRes(notActiveRes);
    }

    private void setActiveImageRes(int imageRes){
        imageResources[0] = imageRes;
        check();
    }

    private void setNotActiveImageRes(int imageRes){
        imageResources[1] = imageRes;
        check();
    }

    void check(){
        isInititialized = true;
        for (int i= 0 ; i<2 ; i++) {
            if (imageResources[i]==0)
                isInititialized = false;
        }
    }
}
