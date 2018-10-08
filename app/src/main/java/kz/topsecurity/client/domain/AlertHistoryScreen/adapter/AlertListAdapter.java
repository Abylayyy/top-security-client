package kz.topsecurity.client.domain.AlertHistoryScreen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.DateUtils;
import kz.topsecurity.client.model.alertList.Alert;
import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.utils.GlideApp;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.MyViewHolder>  {
    private List<Alert> mDataset = new ArrayList<>() ;
    private PlaceListAdapterListener listener;
    private int selectedItemPosition = -1;
    private Context mContext;

    public void updateData(List<Alert> places) {
        mDataset.clear();
        mDataset.addAll(places);
        this.notifyDataSetChanged();
    }

    public void add(Alert alert) {
        mDataset.add(alert);
        this.notifyDataSetChanged();
    }

    public void removeByDataId(int id) {
//        Iterator<Object> iter = mDataset.iterator();
//        while(iter.hasNext()){
//            if(iter.next().getId() ==id )
//                iter.remove();
//        }
//        notifyDataSetChanged();
//        removeSelection();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView iv_map;
        public TextView tv_alert_time;
        public TextView tv_address;
        public TextView tv_charge_level;
        public MyViewHolder(View v) {
            super(v);
            iv_map = (ImageView) v.findViewById(R.id.iv_map);
            tv_alert_time = v.findViewById(R.id.tv_alert_time);
            tv_address = v.findViewById(R.id.tv_address);
            tv_charge_level = v.findViewById(R.id.tv_charge_level);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AlertListAdapter(Context ctx, List<Alert> myDataset, PlaceListAdapterListener listener) {
        mContext = ctx;
        mDataset.addAll(myDataset);
        this.listener = listener;
        requestOptions = requestOptions.transforms(new CenterInside(), new RoundedCorners(10));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AlertListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_history_card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    RequestOptions requestOptions = new RequestOptions();
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Alert alert = mDataset.get(position);
        if(mContext!=null) {
            Double lat = alert.getTracking().getLat();
            Double lng = alert.getTracking().getLng();
            GlideApp.with(mContext)
                    .load("https://maps.googleapis.com/maps/api/staticmap" +
                            "?center="+ lat +","+ lng +
                            "&zoom=13" +
                            "&size=600x300" +
                            "&maptype=roadmap" +
                            "&markers=color:blue%7Tlabel:S%7C"+lat+","+lng +
                            "&key="+Constants.getGoogleMapKey())
                    .apply(requestOptions)
                    .into(holder.iv_map);
        }
        String address = alert.getTracking().getAddress();

        holder.tv_address.setText(address!=null && !address.isEmpty()? address : "Нет данных");
        Integer charge = alert.getTracking().getCharge();
        holder.tv_charge_level.setText(charge!=null && charge>0? String.format("%d", charge): "Нет данных");
        String timestamp = alert.getTracking().getTimestamp();
        holder.tv_alert_time.setText(timestamp!=null ?DateUtils.convertTimeStampToReadableDate(timestamp): "Нет данных");
    }

    public void removeSelection(){

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface PlaceListAdapterListener{
        void onItemSelected(Alert data);

        void onItemDelete(Alert place);
    }
}