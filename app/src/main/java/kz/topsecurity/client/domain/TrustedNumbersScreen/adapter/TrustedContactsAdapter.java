package kz.topsecurity.client.domain.TrustedNumbersScreen.adapter;

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
import kz.topsecurity.client.model.contact.Contact;

public class TrustedContactsAdapter extends  RecyclerView.Adapter<TrustedContactsAdapter.MyViewHolder>  {
    private List<Contact> mDataset = new ArrayList<>() ;
    private ContactsListAdapter listener;
    int activeOptionViewPosiition = -1;

    public void updateData(List<Contact> places) {
        mDataset.clear();
        mDataset.addAll(places);
        this.notifyDataSetChanged();
    }

    public void add(Contact place) {
        mDataset.add(place);
        this.notifyDataSetChanged();
    }

    public void removeDataById(int id) {
        Iterator<Contact> iter = mDataset.iterator();
        while(iter.hasNext()){
            if(iter.next().getId() ==id )
                iter.remove();
        }

        notifyDataSetChanged();
        removeAllSelection();
    }

    private void removeAllSelection() {
        activeOptionViewPosiition = -1;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mContactName;
        public TextView mContactPhone;
        public LinearLayout ll_options;
        public ImageView iv_more;
        public LinearLayout ll_delete_number;
        public LinearLayout ll_edit_number;
        public TextView mContactDesc;

        public MyViewHolder(View v) {
            super(v);
            mContactName = v.findViewById(R.id.tv_contact_name);
            mContactPhone= v.findViewById(R.id.tv_contact_phone);
            ll_options = (LinearLayout)v.findViewById(R.id.ll_options);
            iv_more = (ImageView)v.findViewById(R.id.iv_more);
            ll_delete_number = (LinearLayout)v.findViewById(R.id.ll_delete_number);
            ll_edit_number = (LinearLayout)v.findViewById(R.id.ll_edit_number);
            mContactDesc = (TextView) v.findViewById(R.id.tv_contact_desc);
        }
    }

    public TrustedContactsAdapter(List<Contact> myDataset, TrustedContactsAdapter.ContactsListAdapter listener) {
        mDataset.addAll(myDataset);
        this.listener = listener;
    }

    @Override
    public TrustedContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trusted_number, parent, false);
        TrustedContactsAdapter.MyViewHolder vh = new TrustedContactsAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TrustedContactsAdapter.MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v->{
            listener.onItemSelected(mDataset.get(position));
        });
        holder.iv_more.setOnClickListener(v->{
            makeSelection(position,  holder.ll_options, holder.iv_more);
            listener.onOptionsItem(mDataset.get(position));
        });
        holder.mContactName.setText(mDataset.get(position).getName());
        holder.mContactPhone.setText(mDataset.get(position).getPhone());
        holder.mContactDesc.setText(mDataset.get(position).getDescription());
        if(activeOptionViewPosiition!=-1 && position == activeOptionViewPosiition){
            holder.iv_more.setImageResource(R.drawable.ic_arrow_up_in_circle);
            holder.ll_options.setVisibility(View.VISIBLE);
        }
        else{
            holder.iv_more.setImageResource(R.drawable.ic_arrow_down_in_circle);
            holder.ll_options.setVisibility(View.GONE);
        }
        holder.ll_delete_number.setOnClickListener(v->{
            listener.onDeleteItem(mDataset.get(position));
        });
        holder.ll_edit_number.setOnClickListener(v->{
            listener.onEditItem(mDataset.get(position));
        });
    }

    private void makeSelection(int position, LinearLayout optionsView, ImageView button) {
        if(activeOptionViewPosiition==-1) {
            activeOptionViewPosiition = position;
            optionsView.setVisibility(View.VISIBLE);
            button.setImageResource(R.drawable.ic_arrow_up_in_circle);
        }
        else if(activeOptionViewPosiition == position){
            removeSelection(button,optionsView);
        }
        else {
            removeAllSelection();
            makeSelection(position,optionsView,button);
        }
    }

    public void removeSelection(ImageView iv_more , LinearLayout ll_options){
        if(activeOptionViewPosiition!=-1){
            iv_more.setImageResource(R.drawable.ic_arrow_down_in_circle);
            ll_options.setVisibility(View.GONE);
//            notifyItemChanged(activeOptionViewPosiition);
            activeOptionViewPosiition = -1;
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface ContactsListAdapter{
        void onItemSelected(Contact data);
        void onDeleteItem(Contact contact);
        void onEditItem(Contact contact);

        void onOptionsItem(Contact contact);
    }
}
