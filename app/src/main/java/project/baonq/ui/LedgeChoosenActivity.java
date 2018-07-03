package project.baonq.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import project.baonq.menu.R;

public class LedgeChoosenActivity extends AppCompatActivity {
    private final static int LAYOUT_INFO = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.NormalSizeAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ledge_choosen_layout);
        //set init for action bar
        initActionBar();
        //set init for menu action
        initMenuAction();
        //set init layout element
        initLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAYOUT_INFO) {
            if (resultCode == RESULT_OK) {
                View submitLayout = getLayoutInflater().inflate(R.layout.add_ledge_submit_layout, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 2, 0, 0);
                submitLayout.setLayoutParams(layoutParams);
                TextView txtTitle = submitLayout.findViewById(R.id.txtTittle);
                TextView txtCash = submitLayout.findViewById(R.id.txtCash);
                txtTitle.setText(data.getStringExtra("name"));
                txtCash.setText(data.getStringExtra("currentBalance"));
                LinearLayout contentLedgeChosenLayout = (LinearLayout) findViewById(R.id.contentLedgerChosen);
                contentLedgeChosenLayout.addView(submitLayout);
            }
        }
    }

    private void initLayout() {
        initAddLedgeText();
    }

    private void initAddLedgeText() {
        TextView txtAddLedge = (TextView) findViewById(R.id.txtAddLedge);
        txtAddLedge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LedgeChoosenActivity.this, AddLedgeActivity.class);
                startActivityForResult(intent, LAYOUT_INFO);
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customView = layoutInflater.inflate(R.layout.ledge_choosen_sub_layout, null);
        actionBar.setCustomView(customView);
    }

    private void initMenuAction() {
        TextView txtClose = (TextView) findViewById(R.id.closeLedge);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
