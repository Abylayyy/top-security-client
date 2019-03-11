package kz.topsecurity.client.domain.HealthCardScreen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kz.topsecurity.client.R;

class NumberChooserAdapter extends  RecyclerView.Adapter<NumberChooserAdapter.MyViewHolder>  {
    private List<String> mDataset = new ArrayList<>() ;
    int activeOptionViewPosiition = -1;

    public void updateData(List<String> places) {
        mDataset.clear();
        mDataset.addAll(places);
        this.notifyDataSetChanged();
    }


    private void removeAllSelection() {
        activeOptionViewPosiition = -1;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView picker_item;

        public MyViewHolder(View v) {
            super(v);
            picker_item = v.findViewById(R.id.picker_item);
        }
    }

    public NumberChooserAdapter(List<String> myDataset) {
        mDataset.addAll(myDataset);
    }

    @Override
    public NumberChooserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number, parent, false);
        NumberChooserAdapter.MyViewHolder vh = new NumberChooserAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NumberChooserAdapter.MyViewHolder holder, int position) {
        holder.picker_item.setText(String.valueOf(mDataset.get(position)));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
