package kz.topsecurity.client.domain.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.lang.ref.WeakReference;

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

    public boolean isNetworkOnline() {
        boolean status=false;
        WeakReference data = null;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            data = new WeakReference<ConnectivityManager>(cm);
            if(cm!=null) {
                NetworkInfo netInfo = cm.getNetworkInfo(0);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    netInfo = cm.getNetworkInfo(1);
                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                        status = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(data!=null)
                data.clear();
        }
        return status;
    }

}
