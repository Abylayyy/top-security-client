package kz.topsecurity.client.ui_widgets.customDialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;

public class CustomDialog extends DialogFragment {

    private RoundCorneredEditText ed_user_password;
    private Button btn_ok;
    private Button btn_cancel;

    public interface Callback{
        void onCancelBtnClicked();
        void onPositiveBtnClicked();
    }

    private Callback listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_dialog, container, false);
        ed_user_password = v.findViewById(R.id.ed_user_password);
        btn_ok = v.findViewById(R.id.btn_ok);
        btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancelBtnClicked();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              listener.onPositiveBtnClicked();
            }
        });

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_bg);

        if(ed_user_password!=null && !Constants.BlockedFunctions.isTwoCodeEnabled)
            ed_user_password.setVisibility(View.GONE);
        return v;
    }

    public void setListener( Callback callback){
        this.listener = callback;
    }

}
