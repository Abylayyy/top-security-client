package kz.topsecurity.client.domain.base;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class HelperActivity extends AppCompatActivity {

    ProgressDialog pg_loader;

    protected void showLoadingDialog()  {
        pg_loader = ProgressDialog.show(this,"","Идет загрузка ...",true);
    }

    protected void hideProgressDialog() {
        if(pg_loader!=null )
            pg_loader.cancel();
    }

    protected void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HelperActivity.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }
    protected void showToast(int textMsgResId){
        showToast(getString(textMsgResId));
    }


}
