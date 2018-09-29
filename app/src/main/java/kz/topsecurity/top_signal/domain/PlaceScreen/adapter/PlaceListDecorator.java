package kz.topsecurity.top_signal.domain.PlaceScreen.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class PlaceListDecorator extends RecyclerView.ItemDecoration {
    private final static int PADDING_IN_DIPS = 8;
    private final int mPadding;

    public PlaceListDecorator(@NonNull Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_IN_DIPS, metrics);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        outRect.top = mPadding;
        outRect.bottom = mPadding;
    }

}