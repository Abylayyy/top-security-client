package kz.topsecurity.client.domain.FeedbackScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_contacts_phone_for_other_issues)
    TextView tv_contacts_phone_for_other_issues;
    @BindView(R.id.tv_technical_issues)
    TextView tv_technical_issues;
    @BindView(R.id.tv_user_agreement)
    TextView tv_user_agreement;
    @BindView(R.id.tv_user_instruction)
    TextView tv_user_instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.about_and_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tv_contacts_phone_for_other_issues.setOnClickListener(this);
        tv_technical_issues.setOnClickListener(this);
        tv_user_agreement.setOnClickListener(this);
        tv_user_instruction.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id  = view.getId();
        String content = ((TextView)view).getText().toString();
        switch (id){
            case R.id.tv_contacts_phone_for_other_issues:{
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", content, null));
                startActivity(phoneIntent);
                break;
            }
            case R.id.tv_technical_issues:{
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + content));
//                intent.putExtra(Intent.EXTRA_EMAIL, content);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Технический вопрос");
                intent.putExtra(Intent.EXTRA_TEXT, "Здесь напишите свою проблему");
                startActivity(Intent.createChooser(intent, "Отправить Email"));
                break;
            }
            case R.id.tv_user_agreement:{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.PRIVACY_POLICY_LINK));
                startActivity(i);
                break;
            }
            case R.id.tv_user_instruction:{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.INSTRuCTION_LINK));
                startActivity(i);
                break;
            }
        }
    }
}
