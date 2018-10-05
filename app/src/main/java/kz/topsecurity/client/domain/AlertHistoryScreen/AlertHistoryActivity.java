package kz.topsecurity.client.domain.AlertHistoryScreen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.AlertHistoryScreen.adapter.AlertListAdapter;
import kz.topsecurity.client.domain.AlertHistoryScreen.adapter.AlertListDecorator;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.model.alertList.Alert;
import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.presenter.alertHistoryPresenter.AlertHistoryPresenter;
import kz.topsecurity.client.presenter.alertHistoryPresenter.AlertHistoryPresenterImpl;
import kz.topsecurity.client.view.alertHistoryView.AlertHistoryView;

public class AlertHistoryActivity extends BaseActivity<AlertHistoryView,AlertHistoryPresenter, AlertHistoryPresenterImpl> implements AlertHistoryView, AlertListAdapter.PlaceListAdapterListener {

    @BindView(R.id.rv_alerts)
    RecyclerView rv_alerts;

    AlertListAdapter mAdapter = new AlertListAdapter(this,new ArrayList<>(),this);
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_history);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.alert_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initRV();
        initPresenter(new AlertHistoryPresenterImpl(this));
        presenter.getAlertHistory(0);
    }

    private void initRV() {
        rv_alerts.setHasFixedSize(true);
        rv_alerts.addItemDecoration(new AlertListDecorator(this));
        mLayoutManager = new LinearLayoutManager(this);
        rv_alerts.setLayoutManager(mLayoutManager);
        rv_alerts.setAdapter(mAdapter);
    }

    @Override
    public void onHistoryLoaded(List<Alert> history ) {
        if(mAdapter!=null)
            mAdapter.updateData(history);
    }

    @Override
    public void onHistoryEmpty() {
        showToast(R.string.list_is_empty);
    }

    @Override
    public void onHistoryLoadFailed(int error_msg) {
        showToast(error_msg);
    }

    @Override
    public void onItemSelected(Alert data) {

    }

    @Override
    public void onItemDelete(Alert data) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}