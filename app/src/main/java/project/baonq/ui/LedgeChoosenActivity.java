package project.baonq.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import project.baonq.enumeration.TransactionGroupType;
import project.baonq.menu.R;
import project.baonq.model.Ledger;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.service.LedgerService;
import project.baonq.service.TransactionGroupService;
import project.baonq.service.TransactionService;
import project.baonq.util.ConvertUtil;

public class LedgeChoosenActivity extends AppCompatActivity {
    private final static int LAYOUT_INFO = 1;
    private final static int LAYOUT_UPDATE = 2;
    TransactionGroupService transactionGroupService;
    LedgerService ledgerService;
    TransactionService transactionService;
    public static String MAIN_PREFERENCE = "main_preference";
    private Map<Long, View> layoutList = new HashMap<>();
    private View cardSumLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.NormalSizeAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ledge_choosen_layout);
        cardSumLayout = findViewById(R.id.cardSumLayout);
        layoutList.put(Long.parseLong("0"), cardSumLayout);
        cardSumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRow(null);
            }
        });
        transactionGroupService = new TransactionGroupService(getApplication());
        ledgerService = new LedgerService(getApplication());
        transactionService = new TransactionService(getApplication());
        //set init for action bar
        initActionBar();
        //set init for menu action
        initMenuAction();
        //set init layout element
        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRow(getCurrentLedgerId());
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
            List<Transaction> transactionList = transactionService.getByLedgerId(ledger.getId());
            double transactionSum = 0;
            if (transactionList != null) {
                transactionSum = sumOfTransaction(transactionList);
            }
            createNewRowData(ledger, transactionSum);
            totalLedgerCash += transactionSum;
        }
        setTotalLedgerCash(totalLedgerCash, currency);
    }

    public static double sumOfTransaction(List<Transaction> transactionList) {
        double sum = 0;
        for (Transaction item : transactionList) {
            TransactionGroup transactionGroup = item.getTransactionGroup();
            int transactionGrouptype = transactionGroup.getTransaction_type();
            if (transactionGrouptype == TransactionGroupType.EXPENSE.getType()) {
                sum -= item.getBalance();
            }
            if (transactionGrouptype == TransactionGroupType.INCOME.getType()) {
                sum += item.getBalance();
            }
        }
        return sum;
    }

    private List<Ledger> getLedgerList() {
        List<Ledger> ledgerList = ledgerService.getAll();
        return ledgerList;
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
        }
        txtTitle.setText(ledger.getName());
        String currentBalanceFormat = formatMoney(sum);
        txtCash.setText(currentBalanceFormat + ConvertUtil.convertCurrency(ledger.getCurrency()));

        //create image button
        createImageButton(ledger, sum, submitLayout);

        LinearLayout contentLedgeChosenLayout = (LinearLayout) findViewById(R.id.contentLedgerChosen);
        submitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRow(ledger.getId());
            }
        });
        contentLedgeChosenLayout.addView(submitLayout);
        layoutList.put(ledger.getId(), submitLayout);
        if (ledger.getId() == getCurrentLedgerId()) {
            checkRow(ledger.getId());
        }
    }

    private void checkRow(Long id) {
        changeCurrentLedgerId(id);
        uncheckAllRow();
        View view = cardSumLayout;
        if (id != null) {
            view = layoutList.get(id);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(4, getResources().getColor(R.color.color_red));
        view.setBackground(gradientDrawable);
    }

    private void uncheckAllRow() {
        layoutList.values().stream().forEach(view -> {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor("#FFFFFF"));
            view.setBackground(gradientDrawable);
        });
    }

    private void changeCurrentLedgerId(Long id) {
        SharedPreferences sharedPreferences = getSharedPreferences(MAIN_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (id != null) {
            editor.putLong("curLedgerId", id);
        } else {
            editor.remove("curLedgerId");
        }
        editor.commit();
    }

    private Long getCurrentLedgerId() {
        SharedPreferences sharedPreferences = getSharedPreferences(MAIN_PREFERENCE, MODE_PRIVATE);
        Long rs = sharedPreferences.getLong("curLedgerId", Long.parseLong("0"));
        if (rs == 0L) {
            return null;
        }
        return rs;
    }

    private void setTotalLedgerCash(double totalLedgerCash, String currency) {
        TextView txtLedgerCashSum = (TextView) findViewById(R.id.txtLedgerCashSum);
        if (totalLedgerCash < 0) {
            txtLedgerCashSum.setTextColor(Color.RED);
        }
        String totalCash = formatMoney(totalLedgerCash);
        txtLedgerCashSum.setText(totalCash + currency);
    }

    public String formatMoney(double amount) {
        return (amount < 0 ? "-" : "") + ConvertUtil.convertCashFormat(Math.abs(amount));
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
