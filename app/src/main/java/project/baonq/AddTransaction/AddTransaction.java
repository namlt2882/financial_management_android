package project.baonq.AddTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import project.baonq.model.Ledger;
import project.baonq.service.App;
import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionDao;
import project.baonq.service.LedgerService;
import project.baonq.util.ConvertUtil;

import static android.widget.Toast.LENGTH_SHORT;

public class AddTransaction extends AppCompatActivity {
    private DaoSession daoSession;
    private static TransactionDao dao;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        daoSession = ((project.baonq.service.App) getApplication()).getDaoSession();
        Button btn = null;

        btn = findViewById(R.id.btnCategory);
        btn.setText("Select category");
        EditText txt = findViewById(R.id.txtDate);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTransaction.this, SelectCategory.class);
                startActivity(intent);
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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText tmp = (EditText)findViewById(R.id.nmAmount);
//                String txtNote = findViewById(R.id.txtNote).toString();
//                String date = findViewById(R.id.txtDate).toString();
//
//                if (tmp.getText().toString().isEmpty() || txtNote.isEmpty() || date.isEmpty()) {
//                    new AlertDialog.Builder(AddTransaction.this)
//                            .setTitle("Oops")
//                            .setMessage("Please fill all field")
//                            .setNegativeButton("OK", null)
//                            .show();
//                } else {
//                    daoSession = ((project.baonq.service.App) getApplication()).getDaoSession();
//                    TransactionService.setDaoSession(daoSession);
//
//
//                    double amount = Double.parseDouble(tmp.getText().toString());
//                    Transaction transaction = new Transaction();
//                    transaction.setBalance(amount);
//                    transaction.setNote(txtNote);
//                    transaction.setTdate(date);
//                    transaction.setInsert_date(LocalDate.now().toString());
//                    dao = daoSession.getTransactionDao();
//                    dao.insert(transaction);
//
//                    Intent intent = new Intent(AddTransaction.this, MainActivity.class);
//                    startActivity(intent);
//                }
                Intent intent = new Intent(AddTransaction.this, ChooseLedger.class);
                    startActivity(intent);


            }
        });
        dao = daoSession.getTransactionDao();
        List<Transaction> list =  dao.loadAll();
        Log.i("Transaction",String.valueOf(list.size()));
        File dbFile = getDatabasePath("ledger");
        Log.i("db:",dbFile.getAbsolutePath());
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        EditText txt = findViewById(R.id.txtDate);
        txt.setText(sdf.format(myCalendar.getTime()));
    }

    public void generateLedger(){


        daoSession = ((App) getApplication()).getDaoSession();

        LedgerService.setDaoSession(daoSession);
        Long i = LedgerService.addLedger("ABC","VND",true);
        i = LedgerService.addLedger("Agribank","VND",true);
        i=LedgerService.addLedger("Sacom","VND",true);
        List<Ledger> ledgerList = LedgerService.getAll();

        }
    }





