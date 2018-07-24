package project.baonq.AddTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import project.baonq.model.Ledger;
import project.baonq.service.App;
import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;
import project.baonq.service.LedgerService;
import project.baonq.service.TransactionService;
import project.baonq.ui.MainActivity;
import project.baonq.util.ConvertUtil;

import static android.widget.Toast.LENGTH_SHORT;

public class AddTransaction extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    private TransactionService transactionService;
    private String txtNote;
    private String txtDate;
    private String balance;
    private Long ledgerId;
    private Long catId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        transactionService = new TransactionService(getApplication());

        Button btn = null;
        btn = findViewById(R.id.btnCategory);
        btn.setText("Select category");
        EditText txt = findViewById(R.id.txtDate);
        txt.setFocusable(false);
        txt.setBackgroundResource(android.R.color.transparent);
        ((EditText) findViewById(R.id.nmAmount)).setBackgroundResource(android.R.color.transparent);
        ((EditText) findViewById(R.id.nmAmount)).setTextSize(50);
        ((EditText) findViewById(R.id.txtNote)).setBackgroundResource(android.R.color.transparent);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ledgerId != null) {
                    Intent intent = new Intent(AddTransaction.this, SelectCategory.class);
                    intent.putExtra("ledgerId", ledgerId);
                    saveData();
                    startActivity(intent);
                }
            }
        });


        EditText edittext = findViewById(R.id.txtDate);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddTransaction.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn = findViewById(R.id.btnWallet);
        btn.setText("Select wallet");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransaction.this, ChooseLedger.class);
                startActivity(intent);
                saveData();
            }
        });

        btn = findViewById(R.id.btnCurrency);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tmp = (EditText) findViewById(R.id.nmAmount);
                String txtNote = ((EditText) findViewById(R.id.txtNote))
                        .getText().toString();
                String date = ((EditText) findViewById(R.id.txtDate)).getText().toString();

                if (tmp.getText().toString().isEmpty() || txtNote.isEmpty() || date.isEmpty()) {
                    new AlertDialog.Builder(AddTransaction.this)
                            .setTitle("Oops")
                            .setMessage("Please fill all field")
                            .setNegativeButton("OK", null)
                            .show();
                } else {
                    double amount = Double.parseDouble(tmp.getText().toString());
                    transactionService.addTransaction(ledgerId, catId, amount, date, txtNote);
                    finish();
                }
                removeData();
            }
        });

        loadData();
        Intent intent = AddTransaction.this.getIntent();
        Long number = intent.getLongExtra("LedgerId", 0);
        if (number != 0) {
            ((Button) findViewById(R.id.btnWallet)).setText(String.valueOf(number));
            ledgerId = number;
        }
        number = intent.getLongExtra("catId", 0);
        if (number != 0) {
            ((Button) findViewById(R.id.btnCategory)).setText(String.valueOf(number));
            catId = number;
        }

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText txt = findViewById(R.id.txtDate);
        txt.setText(sdf.format(myCalendar.getTime()));
    }


    public void saveData() {
        SharedPreferences pre = getSharedPreferences("transaction_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();

        EditText tmp = (EditText) findViewById(R.id.nmAmount);
        txtNote = ((EditText) findViewById(R.id.txtNote))
                .getText().toString();
        if (txtNote != null) {
            editor.putString("Note", txtNote);
        }
        txtDate = ((EditText) findViewById(R.id.txtDate)).getText().toString();
        if (txtDate != null) editor.putString("Date", txtDate);

        if (tmp.getText().toString() != null && tmp.getText().toString() != "") {
            editor.putString("balance", tmp.getText().toString());
        }
        String text = ((Button) findViewById(R.id.btnWallet)).getText().toString();
        editor.putString("walletId", text);
        text = ((Button) findViewById(R.id.btnCategory)).getText().toString();
        editor.putString("catId", text);
        editor.commit();
    }

    public void loadData() {
        SharedPreferences pre = getSharedPreferences("transaction_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        txtNote = pre.getString("Note", "");
        ((EditText) findViewById(R.id.txtNote)).setText(txtNote);
        txtDate = pre.getString("Date", "");
        ((EditText) findViewById(R.id.txtDate)).setText(txtDate);
        balance = pre.getString("balance", "0");
        ((EditText) findViewById(R.id.nmAmount)).setText(balance);
        if (pre.getString("walletId", "") != "Select wallet" && pre.getString("walletId", "") != "") {
            ledgerId = Long.parseLong(pre.getString("walletId", "0"));
            ((Button) findViewById(R.id.btnWallet)).setText(pre.getString("walletId", ""));
            Log.i("Ledger", pre.getString("walletId", "0"));
        }
        if (pre.getString("catId", "") != "Select category" && pre.getString("catId", "") != "") {
            catId = Long.parseLong(pre.getString("catId", "0"));
            ((Button) findViewById(R.id.btnCategory)).setText(pre.getString("catId", ""));
            Log.i("Cat", pre.getString("catId", "0"));
        }
    }

    public void removeData() {
        SharedPreferences pre = getSharedPreferences("transaction_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.remove("Note");
        editor.remove("Date");
        editor.remove("balance");
        editor.remove("catId");
        editor.remove("walletId");
        editor.commit();
    }
}





