package project.baonq.ui;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import project.baonq.AddTransaction.AddTransaction;
import project.baonq.menu.R;
import project.baonq.model.Ledger;
import project.baonq.model.Transaction;
import project.baonq.service.App;
import project.baonq.service.AuthenticationService;
import project.baonq.service.LedgerSyncService;
import project.baonq.service.NotificationService;
import project.baonq.service.RecyclerItemClickListener;
import project.baonq.service.TransactionService;
import project.baonq.util.ConvertUtil;


public class MainActivity extends AppCompatActivity {
    CalendarPickerView calendar;
    Button button;
    AuthenticationService authService;
    Thread notificationService;
    LedgerSyncService ledgerSyncService;
    public static final boolean GET_NOTIFICATION = false;
    private TransactionService transactionService;
    private Ledger ledger;
    private double sumledgerMoney = 0;
    private View mCustomView;



    /////////////////////////////////////
    private ArrayList<String> mItems;
    private RecyclerView mRecentRecyclerView;
    private LinearLayoutManager mRecentLayoutManager;
    private RecyclerView.Adapter<CustomViewHolder> mAdapter;
    public static Date dateFrom = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,1);
    public static Date dateTo   = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,31);
    //////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authService = new AuthenticationService(this);
        ledgerSyncService = new LedgerSyncService(getApplication());
        transactionService = new TransactionService(getApplication());
        if (!authService.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //set date picker

        setActionBarLayout("Chọn ngày");
        //set date picker
//        initDatepicker();
        //set float action button
        initFloatActionButton();
        //set botttom navigation bar activities
        setFragmentBottomNavigationBarActivities();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GET_NOTIFICATION) {
            if (authService.isLoggedIn() && notificationService == null) {
                System.out.println("INIT NOTIFICATION SERVICE-------");
                notificationService = new Thread(new NotificationService(getApplication()));
                notificationService.start();
            }
        }
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        TextView mCashTextView = (TextView) mCustomView.findViewById(R.id.txtCash);
        mTitleTextView.setText(getLedgerName());
        mCashTextView.setText(getLedgerSum());
    }


    public void restartApp() {
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                PendingIntent.getActivity(this.getBaseContext(),
                        0, new Intent(getIntent()), getIntent().getFlags()));
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void imageClick(View view) {
        Intent intent = new Intent(MainActivity.this, LedgeChoosenActivity.class);
        startActivity(intent);
    }

    private void initFloatActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTransaction.class);
                removeData();
                startActivity(intent);
            }
        });
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

//    private void initDatepicker() {
//        final Button edtDate = (Button) findViewById(R.id.editDate);
//        edtDate.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//        final View mView = getLayoutInflater().inflate(R.layout.date_range_dialog, null);
//        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        mDialogBuilder.setView(mView);
//        final AlertDialog dialog = mDialogBuilder.create();
//        testDatePicker(mView, dialog);
//        edtDate.clearFocus();
//        edtDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//                edtDate.clearFocus();
//            }
//        });
//    }

    private void testDatePicker(final View mView, final AlertDialog dialog) {
        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -10);

        calendar = (CalendarPickerView) mView.findViewById(R.id.calendar_view);
        button = (Button) mView.findViewById(R.id.get_selected_dates);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);

        calendar.deactivateDates(list);
        //this array use for high line important date
        ArrayList<Date> arrayList = new ArrayList<>();
        final SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
//        try {
//            String strdate = "";
//            String strdate2 = "";
//            Date newdate = dateformat.parse(strdate);
//            Date newdate2 = dateformat.parse(strdate2);
//            arrayList.add(newdate);
//            arrayList.add(newdate2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MM, YYYY", Locale.getDefault())) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDate(new Date())
                .withDeactivateDates(list)
                .withHighlightedDates(arrayList);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Date> dateList = calendar.getSelectedDates();
                setActionBarLayout("Từ : " + dateformat.format(dateList.get(0)) + " đến  " + dateformat.format(dateList.get(dateList.size() - 1)));
//                initDatepicker();
                dialog.hide();
            }
        });

    }

//    private void initDatePicker() {
//        final EditText datePicker = (EditText) findViewById(R.id.editDate);
//        final Calendar calendar = Calendar.getInstance();
//        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//        //set date picker event
//        datePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setdatePickerDialog(datePicker, calendar, simpleDateFormat);
//            }
//        });
//        //set date when load page in first time
//        datePicker.setText(simpleDateFormat.format(calendar.getTime()));
//
//    }
//
//    private void setdatePickerDialog(final EditText datePicker, final Calendar calendar, final SimpleDateFormat simpleDateFormat) {
//        int day = calendar.get(Calendar.DATE);
//        int month = calendar.get(Calendar.MONTH);
//        int year = calendar.get(Calendar.YEAR);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                calendar.set(year, month, dayOfMonth);
//                datePicker.setText(simpleDateFormat.format(calendar.getTime()));
//            }
//        }, year, month, day);
//        datePickerDialog.show();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem btnViewNotification = menu.findItem(R.id.btnViewNotification);
        btnViewNotification.getActionView().setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSynchonize:
                if (((App) getApplication()).isNetworkConnected()) {
                    new Thread(ledgerSyncService).start();
                } else {
                    Toast.makeText(this, "Network is not available to sync!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFragmentBottomNavigationBarActivities() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setBackgroundColor(Color.parseColor("#ffffff"));
        bottomNavigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#7f7f7f")));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_item1:
                        selectedFragment = LedgeFragment.newInstance();
                        break;
                    case R.id.action_item2:
                        selectedFragment = ReportFragment.newInstance();
                        break;
                    case R.id.action_item4:
                        selectedFragment = SettingFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, LedgeFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void setActionBarLayout(String edtDateText) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.activity_main_menu_layout, null);

        initData();
        initRecyclerView();

//        Button mEdtDate = (Button) mCustomView.findViewById(R.id.editDate);
        CircleImageView circleImage = (CircleImageView) mCustomView.findViewById(R.id.circleImage);
        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LedgeChoosenActivity.class);
                startActivity(intent);
            }
        });
//        mEdtDate.setText(edtDateText);

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
    }

    public String formatMoney(double amount) {
        return (amount < 0 ? "-" : "") + ConvertUtil.convertCashFormat(Math.abs(amount));
    }

    private String getLedgerName() {
        return ledger != null ? ledger.getName() : "Tổng cộng";
    }

    private String getLedgerSum() {
        if (getLedger() == null) {
            List<Ledger> ledgerList = ledgerSyncService.loadAll();
            double total = 0;
            if (ledgerList != null && !ledgerList.isEmpty()) {
            }

            for (Ledger ledger : ledgerList) {
                List<Transaction> transactionList = transactionService.getByLedgerId(ledger.getId());
                double transactionSum = 0;
                if (transactionList != null) {
                    transactionSum = LedgeChoosenActivity.sumOfTransaction(transactionList);
                }
                total += transactionSum;
            }
            sumledgerMoney = total;
        } else {
            List<Transaction> l = transactionService.getByLedgerId(ledger.getId());
            sumledgerMoney = LedgeChoosenActivity.sumOfTransaction(l);
        }
        return formatMoney(sumledgerMoney);
    }

    public Ledger getLedger() {
        SharedPreferences sharedPreferences = getSharedPreferences(LedgeChoosenActivity.MAIN_PREFERENCE, MODE_PRIVATE);
        Long id = sharedPreferences.getLong("curLedgerId", Long.parseLong("0"));
        if (id != 0) {
            ledger = ledgerSyncService.findById(id);
        } else {
            ledger = null;
        }
        return ledger;
    }

///RecyclerView /////////////////////////////////////////////////////////////////////////////////////////////


    public Date getDateFrom(){

        return dateFrom;
    }

    private void initData() {
        mItems = new ArrayList<String>();
        mItems.add("THIS YEAR");
        mItems.add("LAST 3 MONTHS");
        mItems.add("LAST MONTH");
        mItems.add("THIS MONTH");
        mItems.add("FUTURE");
    }

    private void initRecyclerView() {

        mRecentRecyclerView = (RecyclerView)mCustomView.findViewById(R.id.recentrecyclerView);
        mRecentRecyclerView.setHasFixedSize(true);
        mRecentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecentRecyclerView.setLayoutManager(mRecentLayoutManager);



        mAdapter = new RecyclerView.Adapter<CustomViewHolder>() {
            @Override
            public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wrap_recycler_item
                        , viewGroup, false);
                return new CustomViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CustomViewHolder viewHolder, int i) {
                viewHolder.noticeSubject.setText(mItems.get(i));
            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }

        };

        mRecentRecyclerView.setAdapter(mAdapter);
        Log.i("QEE",String.valueOf(mRecentRecyclerView.getLayoutManager().getItemCount()));

        mRecentRecyclerView.scrollToPosition(4);
        mRecentRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecentRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        resetRecycler(1);
                        ((TextView)view.findViewById(R.id.recyclerItem)).setTypeface(null, Typeface.BOLD);
                        ((TextView)view.findViewById(R.id.recyclerItem)).setTextColor(Color.parseColor("#ccced1"));
                        String tabString = ((TextView)view.findViewById(R.id.recyclerItem)).getText().toString();
                        setDate(tabString);
                        mRecentRecyclerView.scrollToPosition(position);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


      // ((TextView)mRecentLayoutManager.findViewByPosition(0).findViewById(R.id.recyclerItem)).setTypeface(null, Typeface.BOLD);
    }
    public void setDate(String nameTab){
        if (nameTab.equals("FUTURE")){
            dateFrom = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,LocalDate.now().getDayOfMonth()+1);
            dateTo = new Date(LocalDate.now().getYear()+100,2,1);
        }
        if (nameTab.equals("THIS MONTH")){
            dateFrom = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,1);
            dateTo = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,31);
        }
        if (nameTab.equals("LAST MONTH")){
            dateFrom = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1-1,1);
            dateTo = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1-1,31);
        }
        if (nameTab.equals("LAST 3 MONTHS")){
            dateFrom = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1-2,1);
            dateTo = new Date(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,31);

        }
        if (nameTab.equals("THIS YEAR")){
            dateFrom = new Date(LocalDate.now().getYear(),1-1,1);
            dateTo = new Date(LocalDate.now().getYear(), 12-1,31);
            Log.i("fro1m",dateTo.toString());
        }
    }
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView noticeSubject;

        public CustomViewHolder(View itemView) {
            super(itemView);

            noticeSubject = (TextView) itemView.findViewById(R.id.recyclerItem);
        }
    }

    private void resetRecycler(int tab){

        for (int i = 0;i<6;i++)
        if (mRecentLayoutManager.findViewByPosition(i) != null)
        {
            ((TextView)mRecentLayoutManager.findViewByPosition(i).findViewById(R.id.recyclerItem)).setTypeface(null, Typeface.NORMAL);
            ((TextView)mRecentLayoutManager.findViewByPosition(i).findViewById(R.id.recyclerItem)).setTextColor(Color.parseColor("#f7f8f9"));
        }
    }
//////////////////////////////////
}
