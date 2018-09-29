package kz.topsecurity.top_signal.view.base;

import android.view.View;

public interface BaseView {
    void onShowToast(int textMsgResId);
    void onShowToast(String msg);
    void onShowSnackbar(int resMsg, int resActionTxt, View.OnClickListener listener);
    void onShowSnackbar(String resMsg, String resActionTxt, View.OnClickListener listener);
}
