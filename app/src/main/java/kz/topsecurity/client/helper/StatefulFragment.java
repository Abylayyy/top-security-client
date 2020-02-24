package kz.topsecurity.client.helper;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class StatefulFragment extends Fragment {

    private Bundle savedState;
    private boolean saved;
    private static final String _FRAGMENT_STATE = "FRAGMENT_STATE";

    @Override
    public void onSaveInstanceState(Bundle state) {
        if (getView() == null) {
            state.putBundle(_FRAGMENT_STATE, savedState);
        } else {
            Bundle bundle = saved ? savedState : getStateToSave();

            state.putBundle(_FRAGMENT_STATE, bundle);
        }

        saved = false;

        super.onSaveInstanceState(state);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (state != null) {
            savedState = state.getBundle(_FRAGMENT_STATE);
        }
    }

    @Override
    public void onDestroyView() {
        savedState = getStateToSave();
        saved = true;

        super.onDestroyView();
    }

    protected Bundle getSavedState() {
        return savedState;
    }

    protected abstract boolean hasSavedState();

    protected abstract Bundle getStateToSave();
}
