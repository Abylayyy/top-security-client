package kz.topsecurity.client.domain.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import kz.topsecurity.client.R;

public abstract class BaseFragmentActivity extends HelperActivity {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    protected String addFragment(Fragment fragment,String fragmentTag){
        addFragment(R.id.fragment_container,fragment,fragmentTag);
        return fragmentTag;
    }

    protected void removeFragment(String fragment_tag){
        removeFragment(R.id.fragment_container,fragment_tag);
    }

    protected void replaceFragment(Fragment fragment){
        replaceFragment(R.id.fragment_container,fragment,"none",false);
    }

    protected String replaceFragment(Fragment fragment, String fragmentTag, boolean withBackStack){
        replaceFragment(R.id.fragment_container,fragment,fragmentTag,withBackStack);
        return fragmentTag;
    }

    protected void previousFragment(){
        getSupportFragmentManager().popBackStack();
    }

    private void addFragment(int view_id, Fragment fragment,String fragmentTag){
        if(findViewById(view_id)!=null){
            getSupportFragmentManager().beginTransaction()
                    .add(view_id, fragment,fragmentTag).commit();
        }
    }

    private void replaceFragment(int view_id, Fragment fragment, String fragmentTag, boolean withBackStack){
        if(findViewById(view_id)!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction  = getSupportFragmentManager().beginTransaction();
            if(withBackStack){
                fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.addToBackStack(BACK_STACK_ROOT_TAG);
            }
            transaction.replace(view_id, fragment,fragmentTag);
            transaction.commit();
        }
    }

    private void removeFragment(int view_id, String fragment_tag){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragment_tag);
        if(fragment==null)
            return;
        if(findViewById(view_id)!=null){
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment).commit();
        }
    }
}