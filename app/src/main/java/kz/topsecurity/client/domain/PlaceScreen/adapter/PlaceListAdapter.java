package kz.topsecurity.client.domain.PlaceScreen.adapter;

import android.annotation.SuppressLint;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.domain.PlaceScreen.PlaceActivity;
import kz.topsecurity.client.model.place.Place;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>  {
    private List<Place> mDataset = new ArrayList<>() ;
    private PlaceListAdapterListener listener;
    private int selectedItemPosition = -1;
    private boolean isVisible = false;

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
            if(iter.next().getId() == id )
                iter.remove();
        }
        notifyDataSetChanged();
        removeSelection();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mPlaceName;
        ImageView mDeleteIV;

        public MyViewHolder(View v) {
            super(v);
            mPlaceName = v.findViewById(R.id.favorName);
            mDeleteIV = v.findViewById(R.id.deleteFavor);
        }
    }

    public PlaceListAdapter(List<Place> myDataset, PlaceListAdapterListener listener) {
        mDataset.addAll(myDataset);
        this.listener = listener;
    }

    public List<Place> getPlacesList() {
        return mDataset;
    }


    @NotNull
    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {

        holder.mPlaceName.setText(mDataset.get(position).getName());
        holder.itemView.setOnClickListener(v->{
            listener.onItemSelected(mDataset.get(position));
        });

        holder.mDeleteIV.setOnClickListener(v->{
            listener.onItemDelete(mDataset.get(position));
        });

        if (isVisible) {
            holder.mDeleteIV.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteIV.setVisibility(View.INVISIBLE);
        }
    }

    public void updateVisibility(boolean newValue){
        isVisible = newValue;
        notifyDataSetChanged();
    }


    public void removeSelection(){
        if(selectedItemPosition!=-1){
            notifyItemChanged(selectedItemPosition);
            selectedItemPosition = -1;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface PlaceListAdapterListener{
        void onItemSelected(Place data);
        void onItemDelete(Place place);
    }
}