package kz.topsecurity.client.ui_widgets.customDialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kz.topsecurity.client.R;

public class CustomSimpleDialog extends DialogFragment {

    public static final String DIALOG_MESSAGE = "dialog_message";

    public CustomSimpleDialog(){
        super();
    }

    private Button btn_ok;
    private Button btn_cancel;
    private TextView tv_dialog_msg;
    private String dialogMsg ;
    public interface Callback{
        void onCancelBtnClicked();
        void onPositiveBtnClicked();
    }

    private Callback listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogMsg = getArguments().getString(DIALOG_MESSAGE,getString(R.string.are_you_sure_delete));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_simple_dialog, container, false);
        btn_ok = v.findViewById(R.id.btn_ok);
        btn_cancel = v.findViewById(R.id.btn_cancel);
        tv_dialog_msg = v.findViewById(R.id.tv_dialog_msg);
        tv_dialog_msg.setText(dialogMsg);
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
        return v;
    }

    public void setListener( Callback callback){
        this.listener = callback;
    }

}
