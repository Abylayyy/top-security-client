package kz.topsecurity.client.domain.HealthCardScreen;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class SliderLayoutManagerNew extends LinearLayoutManager {

    public OnItemSelectedListener getCallback() {
        return callback;
    }

    public void setCallback(OnItemSelectedListener callback) {
        this.callback = callback;
    }

    private OnItemSelectedListener callback = null;


    private RecyclerView recyclerView;

    public SliderLayoutManagerNew(Context context) {
        super(context);
    }

    public SliderLayoutManagerNew(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SliderLayoutManagerNew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void scaleDownView() {
        float mid = getWidth() / 2.0f;
        for (int i =0 ; i<getChildCount(); i++) {
            // Calculating the distance of the child from the center
            View child = getChildAt(i) ;
            if(child==null)
                return;
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float distanceFromCenter = Math.abs(mid - childMid);

            // The scaling formula
            float scale = 1-(float)Math.sqrt((double) (distanceFromCenter/getWidth()))*0.8f;
            float alpha = 255*(1-(float)Math.sqrt((double) (distanceFromCenter/getWidth()))*0.99F);

            // Set scale to view
            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setAlpha(alpha);
        }
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        recyclerView = view;
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scaleDownView();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getOrientation() == LinearLayoutManager.HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            scaleDownView();
            return scrolled;
        } else {
            return 0;
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state==RecyclerView.SCROLL_STATE_IDLE) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            int recyclerViewCenterX = getRecyclerViewCenterX();
            int minDistance = recyclerView.getWidth();
            int  position = -1;
            for (int i=0; i<recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);
                int childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2;
                int newDistance = Math.abs(childCenterX - recyclerViewCenterX);
                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    position = recyclerView.getChildLayoutPosition(child);
                }
            }

            // Notify on item selection
            callback.onItemSelected(position);
        }
    }

    private int getRecyclerViewCenterX() {
        return (recyclerView.getRight() - recyclerView.getLeft())/2 + recyclerView.getLeft();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int layoutPosition);
    }
}
