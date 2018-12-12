package kz.topsecurity.client.domain.HealthCardScreen.additionalScreen;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.topsecurity.client.R;

public class ValuesAdapter extends RecyclerView.Adapter<ValuesAdapter.MyViewHolder>  {

    private List<String> mDataset = new ArrayList<>() ;
    private ValuesAdapterListener listener;
    private int selectedItemPosition = -1;
    private int marginInPX = 0;

    public void updateData(String[] dataArray) {
        List<String> dataList = new ArrayList<>(Arrays.asList(dataArray));
        updateData(dataList);
    }


    public void updateData(List<String> places) {
        mDataset.clear();
        mDataset.addAll(places);
        this.notifyDataSetChanged();
    }

    public void add(String newValue) {
        if(!mDataset.contains(newValue)) {
            mDataset.add(newValue);
            this.notifyDataSetChanged();
        }
        else
            listener.showToastMsg(R.string.already_added);
    }

    public void removeByDataId(int index) {
        mDataset.remove(index);
        notifyDataSetChanged();
        removeSelection();
    }

    public List<String> getData() {
        return mDataset;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout mContainer;
        public TextView mValueText;
        public ImageView mDeleteIV;
        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.cl_container);
            mValueText = v.findViewById(R.id.tv_value);
            mDeleteIV = v.findViewById(R.id.iv_remove_value);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ValuesAdapter(List<String> myDataset, ValuesAdapterListener listener) {
        mDataset.addAll(myDataset);
        this.listener = listener;
    }

    public void setMargin(Context context){
        marginInPX = (int)(50 * context.getResources().getDisplayMetrics().density);
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.values_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.mValueText.setText(mDataset.get(position));
        holder.mContainer.setOnClickListener(v->{

        });
        holder.mDeleteIV.setOnClickListener(v->{
            listener.onItemRemove(position,mDataset.get(position));
        });
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

}