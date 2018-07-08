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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.greendao.database.Database;

import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;
import project.baonq.service.App;

public class AddLedgeActivity extends AppCompatActivity {

    private Ledger ledger;
    private DaoSession daoSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.NormalSizeAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ledge_layout);
        daoSession = ((App) getApplication()).getDaoSession();

        //init action bar
        initActionBar();
        //init spiner
        initSpiner();
        //init menu element
        initMenuElement();
        //create database
    }

    private void initMenuElement() {
        initSubmitText();
        initCancelText();
    }

    private void initCancelText() {
        TextView txtCancel = (TextView) findViewById(R.id.closeAddLedge);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addLedger(String name, String currency, String currentBalance, boolean isChecked) {
        if (ledger == null) {
            ledger = new Ledger();
        }
        ledger.setName(name);
        ledger.setCurrency(currency);
        ledger.setCurrentBalance(currentBalance);
        ledger.setCounted_on_report(isChecked);
        LedgerDao ledgerDao = daoSession.getLedgerDao();
        ledgerDao.insert(ledger);
    }

    private void initSubmitText() {
        TextView txtSubmit = (TextView) findViewById(R.id.submitAddLedge);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtName = (EditText) findViewById(R.id.txtCash);
                EditText edtCurrentBalance = (EditText) findViewById(R.id.txtCurrentBalance);
                Spinner spCurrency = (Spinner) findViewById(R.id.spinerCurrency);
                CheckBox cbReport = (CheckBox) findViewById(R.id.cb_report);
                Intent intent = getIntent();
                String name = edtName.getText().toString();
                String currentBalance = edtCurrentBalance.getText().toString();
                String currency = spCurrency.getSelectedItem().toString();
                boolean isChecked = cbReport.isChecked();
                intent.putExtra("name", name);
                intent.putExtra("currentBalance", currentBalance);
                intent.putExtra("currency", currency);
                addLedger(name, currency, currentBalance, isChecked);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mCustomView = layoutInflater.inflate(R.layout.add_ledge_sub_layout, null);
        actionBar.setCustomView(mCustomView);
    }

    private void initSpiner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinerCurrency);
        String[] items = new String[]{"VNĐ", "Dollar", "Euro"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddLedgeActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(arrayAdapter);
    }
}
