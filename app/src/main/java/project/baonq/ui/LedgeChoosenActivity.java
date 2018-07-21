package project.baonq.ui;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

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

public class LedgeChoosenActivity extends AppCompatActivity {
    private final static int LAYOUT_INFO = 1;
    private final static int LAYOUT_UPDATE = 2;

    private Ledger ledger;
    private DaoSession daoSession;
    private Application application;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.NormalSizeAppTheme);
        super.onCreate(savedInstanceState);
        daoSession = ((App) getApplication()).getDaoSession();
        application = getApplication();
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
        if (requestCode == LAYOUT_INFO || requestCode == LAYOUT_UPDATE) {
            if (resultCode == RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }
    }

    private void initLayout() {
        initAddLedgeText();
        loadDataFromSessionDao();
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

    private void loadDataFromSessionDao() {
        List<Ledger> ledgerList = getLedgerList();
        double totalLedgerCash = 0;
        String currency = "";
        //in case this is not have any ledger
        if (ledgerList != null && !ledgerList.isEmpty()) {
            currency = ConvertUtil.convertCurrency(ledgerList.get(0).getCurrency());
        }

        for (Ledger ledger : ledgerList) {
            List<Transaction> transactionList = getTransactionListById(ledger.getId());
            double transactionSum = 0;
            if (transactionList != null) {
                transactionSum = sumOfTransaction(transactionList);
            }
            createNewRowData(ledger, transactionSum);
            totalLedgerCash += transactionSum;
        }
        setTotalLedgerCash(totalLedgerCash, currency);
    }

    private double sumOfTransaction(List<Transaction> transactionList) {
        double sum = 0;
        for (Transaction item : transactionList) {
            TransactionGroup transactionGroup = new TransactionGroupService(application).getTransactionGroupByID(item.getGroup_id());
            int transactionGrouptype = transactionGroup.getTransaction_type();
            if (transactionGrouptype == 1) {
                sum += item.getBalance();
            }

            if (transactionGrouptype == 2) {
                sum -= item.getBalance();
            }
        }
        return sum;
    }

    private List<Ledger> getLedgerList() {
        daoSession = ((App) getApplication()).getDaoSession();
        List<Ledger> ledgerList = new LedgerService(application).getAll();
        return ledgerList;
    }

    private List<Transaction> getTransactionListById(Long ledger_id) {
        return new TransactionService(application).getTransactionByLedgerId(ledger_id);
    }

    private void createNewRowData(final Ledger ledger, double sum) {
        View submitLayout = getLayoutInflater().inflate(R.layout.add_ledge_submit_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 2, 0, 0);
        submitLayout.setLayoutParams(layoutParams);
        TextView txtTitle = submitLayout.findViewById(R.id.txtTittle);
        TextView txtCash = submitLayout.findViewById(R.id.txtCash);
        if (sum < 0) {
            txtCash.setTextColor(Color.RED);
            sum = Math.abs(sum);
        }
        txtTitle.setText(ledger.getName());
        String currentBalanceFormat = ConvertUtil.convertCashFormat(sum);
        txtCash.setText(currentBalanceFormat + ConvertUtil.convertCurrency(ledger.getCurrency()));

        //create image button
        createImageButton(ledger, sum, submitLayout);

        LinearLayout contentLedgeChosenLayout = (LinearLayout) findViewById(R.id.contentLedgerChosen);
        contentLedgeChosenLayout.addView(submitLayout);
    }

    private void setTotalLedgerCash(double totalLedgerCash, String currency) {
        TextView txtLedgerCashSum = (TextView) findViewById(R.id.txtLedgerCashSum);
        if (totalLedgerCash < 0) {
            txtLedgerCashSum.setTextColor(Color.RED);
            totalLedgerCash = Math.abs(totalLedgerCash);
        }
        String totalCash = ConvertUtil.convertCashFormat(totalLedgerCash);
        txtLedgerCashSum.setText(totalCash + currency);
    }

    private void createImageButton(final Ledger ledger, final double sum, View submitLayout) {
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(R.drawable.ic_edit_black_24dp);
        LinearLayout.LayoutParams imageButtonParam = new LinearLayout.LayoutParams(0, 45);
        imageButtonParam.weight = 0.1f;
        imageButtonParam.setMargins(0, 15, 0, 0);
        imageButton.setLayoutParams(imageButtonParam);
        imageButton.setBackground(getResources().getDrawable(R.color.colorWhite));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LedgeChoosenActivity.this, AddLedgeActivity.class);
                intent.putExtra("id", ledger.getId());
                intent.putExtra("name", ledger.getName());
                intent.putExtra("currency", ledger.getCurrency());
                intent.putExtra("currentBalance", sum);
                startActivityForResult(intent, LAYOUT_UPDATE);
            }
        });
        LinearLayout submitLayOutLinear = submitLayout.findViewById(R.id.container);
        submitLayOutLinear.addView(imageButton);
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
