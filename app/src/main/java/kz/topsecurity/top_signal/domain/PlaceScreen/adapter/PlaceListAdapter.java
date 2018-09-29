package kz.topsecurity.top_signal.domain.PlaceScreen.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.model.place.Place;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>  {
    private List<Place> mDataset = new ArrayList<>() ;
    private PlaceListAdapterListener listener;
    private int selectedItemPosition = -1;

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
        public TextView mPlaceName;
        public ImageView mDeleteIV;
        public MyViewHolder(View v) {
            super(v);
            mPlaceName = v.findViewById(R.id.tv_place_name);
            mDeleteIV = v.findViewById(R.id.iv_delete_place);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceListAdapter(List<Place> myDataset,PlaceListAdapterListener listener) {
        mDataset.addAll(myDataset);
        this.listener = listener;
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
        holder.mPlaceName.setText(mDataset.get(position).getName());
        holder.itemView.setOnClickListener(v->{
            if(selectedItemPosition!=-1) {
                removeSelection();
            }
            holder.itemView.setBackgroundColor(Color.BLACK);
            holder.mPlaceName.setTextColor(Color.WHITE);
            holder.mDeleteIV.setVisibility(View.VISIBLE);
            listener.onItemSelected(mDataset.get(position));
            selectedItemPosition = position;

        });
        holder.mDeleteIV.setOnClickListener(v->{
            listener.onItemDelete(mDataset.get(position));
        });
        if(selectedItemPosition!=-1 && position == selectedItemPosition){
            holder.itemView.setBackgroundColor(Color.BLACK);
            holder.mPlaceName.setTextColor(Color.WHITE);
            holder.mDeleteIV.setVisibility(View.VISIBLE);
        }
        else{
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.mPlaceName.setTextColor(Color.BLACK);
            holder.mDeleteIV.setVisibility(View.GONE);
        }
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
    }
}