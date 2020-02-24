package kz.topsecurity.client.fragments.main_fragment.main_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyContactViewHolder> {

    private Context context;
    private List<Contact> contactList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public MyContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_emergency_number, viewGroup, false);
        return new MyContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyContactViewHolder myContactViewHolder, int i) {
        Contact contact = contactList.get(i);

        myContactViewHolder.emName.setText(contact.getName());
        myContactViewHolder.emWho.setText(contact.getDescription());
        myContactViewHolder.emPhone.setText(contact.getPhone());

        myContactViewHolder.emDelete.setOnClickListener(v -> {
            removeDataByIndex(i);
            deleteContact(contact.getId());
            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateData(List<Contact> places) {
        contactList.clear();
        contactList.addAll(places);
        this.notifyDataSetChanged();
    }

    public void add(Contact place) {
        contactList.add(0, place);
        this.notifyDataSetChanged();
    }

    public void removeDataByIndex(int index) {
        contactList.remove(index);
        this.notifyDataSetChanged();
    }

    public void deleteContact(Integer id) {
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse r) {
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                Toast.makeText(context, "Failed to delete!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                Toast.makeText(context, "Error with deleting contact!", Toast.LENGTH_SHORT).show();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .deleteContact(RetrofitClient.getRequestToken(), id));
        compositeDisposable.add(subscribe);
    }

    class MyContactViewHolder extends RecyclerView.ViewHolder {
        TextView emName, emPhone, emWho, emDelete;

        MyContactViewHolder(@NonNull View itemView) {
            super(itemView);

            emName = itemView.findViewById(R.id.emName);
            emPhone = itemView.findViewById(R.id.emPhone);
            emWho = itemView.findViewById(R.id.emWho);
            emDelete = itemView.findViewById(R.id.emDelete);
        }
    }
}
