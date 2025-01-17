package kz.topsecurity.client.domain.PlaceScreen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kz.topsecurity.client.R;
import kz.topsecurity.client.model.place.Place;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>  {
    private List<Place> mDataset = new ArrayList<>() ;
    private PlaceListAdapterListener listener;
    private int selectedItemPosition = -1;
    private int marginInPX = 0;

    public void updateData(List<Place> places) {
        mDataset.clear();
        mDataset.addAll(places);
        this.notifyDataSetChanged();
    }

    public void add(Place place) {
        mDataset.add(place);
        this.notifyDataSetChanged();
    }

    public void removeByDataId(int id) {
        Iterator<Place> iter = mDataset.iterator();
        while(iter.hasNext()){
            if(iter.next().getId() ==id )
                iter.remove();
        }
        notifyDataSetChanged();
        removeSelection();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout mContainer;
        public TextView mPlaceName;
        public ImageView mDeleteIV;
        public ImageView mEditIV;
        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.rl_place_container);
            mPlaceName = v.findViewById(R.id.tv_place_name);
            mDeleteIV = v.findViewById(R.id.iv_delete_place);
            mEditIV = v.findViewById(R.id.iv_edit_place);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceListAdapter(List<Place> myDataset,PlaceListAdapterListener listener) {
        mDataset.addAll(myDataset);
        this.listener = listener;
    }

    public void setMargin(Context context){
        marginInPX = (int)(50 * context.getResources().getDisplayMetrics().density);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.mPlaceName.setText(mDataset.get(position).getName());
        holder.itemView.setOnClickListener(v->{
            if(selectedItemPosition!=-1) {
                removeSelection();
            }
//            layoutParams.setMargins(0,0,marginInPX,0);

            holder.mContainer.setLayoutParams(layoutParams);
            holder.mContainer.setBackgroundResource(R.drawable.place_item_selected_bg);
            holder.mPlaceName.setTextColor(Color.WHITE);
            holder.mDeleteIV.setVisibility(View.VISIBLE);
            holder.mEditIV.setVisibility(View.VISIBLE);
            holder.mEditIV.setEnabled(true);
            listener.onItemSelected(mDataset.get(position));
            selectedItemPosition = position;
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    layoutParams.setMargins(0,0,(int)(marginInPX * interpolatedTime),0);
                    holder.mContainer.setLayoutParams(layoutParams);
                    if(hasEnded() && selectedItemPosition!= position){
                        layoutParams.setMargins(0,0,(int)(0),0);
                    }
                }

            };
            a.setDuration(500); // in ms
            holder.mContainer.startAnimation(a);

        });
        holder.mDeleteIV.setOnClickListener(v->{
            listener.onItemDelete(mDataset.get(position));
        });
        holder.mEditIV.setOnClickListener(v->{
            listener.onItemEdit(mDataset.get(position));
        });
        if(selectedItemPosition!=-1 && position == selectedItemPosition){
//            layoutParams.setMargins(0,0,marginInPX,0);
//            holder.mContainer.setLayoutParams(layoutParams);
            holder.mContainer.setBackgroundResource(R.drawable.place_item_selected_bg);
            holder.mPlaceName.setTextColor(Color.WHITE);
            holder.mDeleteIV.setVisibility(View.VISIBLE);
            holder.mEditIV.setVisibility(View.VISIBLE);
            holder.mEditIV.setEnabled(true);
            Animation a = new Animation() {

                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    layoutParams.setMargins(0,0,(int)(marginInPX * interpolatedTime),0);
                    holder.mContainer.setLayoutParams(layoutParams);
                }
            };
            a.setDuration(500); // in ms
            holder.mContainer.startAnimation(a);
        }
        else{
            layoutParams.setMargins(0,0,0,0);
            holder.mContainer.setLayoutParams(layoutParams);
            holder.mContainer.setBackgroundResource(R.drawable.place_item_default_bg);
            holder.mPlaceName.setTextColor(Color.BLACK);
            holder.mDeleteIV.setVisibility(View.GONE);
            holder.mEditIV.setVisibility(View.INVISIBLE);
            holder.mEditIV.setEnabled(false);
        }
        holder.mEditIV.setVisibility(View.GONE);
    }

    public void removeSelection(){
        if(selectedItemPosition!=-1){
            notifyItemChanged(selectedItemPosition);
            selectedItemPosition = -1;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface PlaceListAdapterListener{
        void onItemSelected(Place data);

        void onItemDelete(Place place);

        void onItemEdit(Place place);
    }

}