package project.baonq.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
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

import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Ledger;
import project.baonq.model.LedgerDao;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.service.App;
import project.baonq.service.LedgerService;
import project.baonq.service.TransactionGroupService;
import project.baonq.service.TransactionService;
import project.baonq.util.ConvertUtil;

public class AddLedgeActivity extends AppCompatActivity {

    private Ledger ledger;
    private DaoSession daoSession;
    private boolean isUpdate = false;
    private Long id = null;

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
        //load form for update
        loadUpdateForm();
        //init menu element
        initMenuElement();
    }

    private void initBodyElement(String name, String currency, double currentBalance) {
        TextView txtCash = (TextView) findViewById(R.id.txtCash);
        Spinner spCurrency = (Spinner) findViewById(R.id.spinerCurrency);
        TextView txtCurrentBalance = (TextView) findViewById(R.id.txtCurrentBalance);
        txtCash.setText(name);
        txtCurrentBalance.setText(ConvertUtil.convertCashFormat(currentBalance).replaceAll(",",""));
        spCurrency.setSelection(getIndexOfSpinner(spCurrency, currency));
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

    private void loadUpdateForm() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // in case this is not update request
        if (bundle != null) {
            isUpdate = true;
            id = intent.getLongExtra("id", -1);
            String currency = intent.getStringExtra("currency");
            double currentBalance = intent.getDoubleExtra("currentBalance", -1);
            String name = intent.getStringExtra("name");
            //init body element
            initBodyElement(name, currency, currentBalance);
        }
    }

    private Long addLedger(String name, String currency, boolean isChecked) {
        LedgerService.setDaoSession(daoSession);
        return LedgerService.addLedger(name, currency, isChecked);
    }

    private void addDefaultTransactionGroup(Long ledgerId) {
        String[] defaultIncomeName = {"Lương", "Tiền chuyển đến", "Được tặng", "Lãi ngân hàng", "Khác"};
        Long index = 0L;
        for (String name : defaultIncomeName) {
            daoSession = ((App) getApplication()).getDaoSession();
            TransactionGroupService.setDaoSession(daoSession);
            TransactionGroupService.addTransactionGroup(ledgerId, name, 1, 1);
        }
        String[] defaultPurchaseName = {"Ăn uống", "Hóa đơn", "Mua sắm", "Di chuyển", "Khác"};
        for (String name : defaultPurchaseName) {
            daoSession = ((App) getApplication()).getDaoSession();
            TransactionGroupService.setDaoSession(daoSession);
            TransactionGroupService.addTransactionGroup(ledgerId, name, 2, 1);
        }
    }


    private void addTransaction(Long ledger_id, Long group_id, double balance, int status) {
        long now = System.currentTimeMillis();
        TransactionService.setDaoSession(daoSession);
        TransactionService.addTransaction(ledger_id, group_id, balance, now, status);
    }

    private void updateLedger(Long id, String name, String currency, boolean isChecked) {
        LedgerService.setDaoSession(daoSession);
        LedgerService.updateLedger(id, name, currency, isChecked);
    }

    private void updateTransaction(Long ledger_id, int transaction_Type, String name, double balance) {
        TransactionGroupService.setDaoSession(daoSession);
        Long group_id = TransactionGroupService.getTransactionGroupID(ledger_id, transaction_Type, name);
        TransactionService.setDaoSession(daoSession);
        TransactionService.updateTransaction(ledger_id, group_id, balance);
    }

    private void initSubmitText() {
        TextView txtTitle = (TextView) findViewById(R.id.ledgeTittle);
        TextView txtSubmit = (TextView) findViewById(R.id.submitAddLedge);
        final EditText edtName = (EditText) findViewById(R.id.txtCash);
        final EditText edtCurrentBalance = (EditText) findViewById(R.id.txtCurrentBalance);
        final Spinner spCurrency = (Spinner) findViewById(R.id.spinerCurrency);
        final CheckBox cbReport = (CheckBox) findViewById(R.id.cb_report);
        final Intent intent = getIntent();

        if (!isUpdate) {
            txtTitle.setText("Thêm ví");
            txtSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    daoSession.getLedgerDao().deleteAll();
//                    daoSession.getTransactionDao().deleteAll();
//                    daoSession.getTransactionGroupDao().deleteAll();
                    String name = edtName.getText().toString();
                    String currentBalance = edtCurrentBalance.getText().toString();
                    Long group_id = 0L;
                    double balance = Double.parseDouble(currentBalance);
                    String currency = spCurrency.getSelectedItem().toString();
                    boolean isChecked = cbReport.isChecked();
                    Long ledgerId = addLedger(name, currency, isChecked);
                    addDefaultTransactionGroup(ledgerId);
                    //in case current is > 0
                    if (Double.parseDouble(currentBalance) > 0) {
                        group_id = TransactionGroupService.getTransactionGroupID(ledgerId, 1, "Khác");
                        addTransaction(ledgerId, group_id, balance, 1);
                    } else if (Double.parseDouble(currentBalance) < 0) {
                        group_id = TransactionGroupService.getTransactionGroupID(ledgerId, 2, "Khác");
                        addTransaction(ledgerId, group_id, balance, 1);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            txtTitle.setText("Cập nhật ví");
            txtSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = edtName.getText().toString();
                    String currentBalance = edtCurrentBalance.getText().toString();
                    currentBalance = currentBalance.replaceAll(",", "");
                    String currency = spCurrency.getSelectedItem().toString();
                    boolean isChecked = cbReport.isChecked();
                    updateLedger(id, name, currency, isChecked);
                    updateTransaction(id, 1, "Khác", Double.parseDouble(currentBalance));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
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

    //get index of spinner
    private int getIndexOfSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

}
