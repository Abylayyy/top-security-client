package kz.topsecurity.client.domain.TrustedNumbersScreen.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class ContactItemDecorator extends RecyclerView.ItemDecoration {
    private final static int PADDING_IN_DIPS = 56;
    private final int mPadding;

    public ContactItemDecorator(@NonNull Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DIPS, metrics);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        int childCount = parent.getAdapter().getItemCount();

        if(itemPosition!=(childCount - 1))
            return;

        outRect.bottom = mPadding;
    }

}