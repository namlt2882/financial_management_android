package project.baonq.ui;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import project.baonq.AddTransaction.AddTransaction;
import project.baonq.menu.R;
import project.baonq.service.App;
import project.baonq.service.AuthenticationService;
import project.baonq.service.LedgerSyncService;
import project.baonq.service.NotificationService;
import project.baonq.util.ConvertUtil;


public class MainActivity extends AppCompatActivity {
    CalendarPickerView calendar;
    Button button;
    public static Long startTime;
    public static Long endTime;
    public static Long ledger_id;
    public static String my_money;
    public static String txtDateHistory;
    AuthenticationService authService;
    Thread notificationService;
    LedgerSyncService ledgerSyncService;
    public static final boolean GET_NOTIFICATION = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authService = new AuthenticationService(this);
        ledgerSyncService = new LedgerSyncService(getApplication());
        if (!authService.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

//        ((App) getApplication()).getDaoSession().getLedgerDao().deleteAll();
//        ((App) getApplication()).getDaoSession().getTransactionDao().deleteAll();
//        ((App) getApplication()).getDaoSession().getTransactionGroupDao().deleteAll();

        //set date picker
        if(txtDateHistory == null){
            setActionBarLayout("Chọn ngày");
        }else {
            setActionBarLayout(txtDateHistory);
        }

        //set date picker
        initDatepicker();
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

    private void initDatepicker() {
        final Button edtDate = (Button) findViewById(R.id.editDate);
        edtDate.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        final View mView = getLayoutInflater().inflate(R.layout.date_range_dialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        mDialogBuilder.setView(mView);
        final AlertDialog dialog = mDialogBuilder.create();
        createDatePicker(mView, dialog);
        edtDate.clearFocus();
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                edtDate.clearFocus();
            }
        });
    }

    private void createDatePicker(final View mView, final AlertDialog dialog) {
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
        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MM, YYYY", Locale.getDefault())) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDate(new Date())
                .withDeactivateDates(list)
                .withHighlightedDates(arrayList);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Date> dateList = calendar.getSelectedDates();
                txtDateHistory = "Từ : " + dateformat.format(dateList.get(0)) + " đến  " + dateformat.format(dateList.get(dateList.size() - 1));
                setActionBarLayout(txtDateHistory);
                initDatepicker();
                startTime = atStartOfDay(dateList.get(0));
                endTime = atEndOfDay(dateList.get(dateList.size() - 1));
                dialog.hide();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem btnViewNotification = menu.findItem(R.id.btnViewNotification);
        btnViewNotification.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
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

    public void setActionBarLayout(String edtDateText) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.activity_main_menu_layout, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        TextView mCashTextView = (TextView) mCustomView.findViewById(R.id.txtCash);
        Button mEdtDate = (Button) mCustomView.findViewById(R.id.editDate);
        CircleImageView circleImage = (CircleImageView) mCustomView.findViewById(R.id.circleImage);
        if(MainActivity.ledger_id == null){
            circleImage.setImageResource(R.drawable.global_icon);
        }else {
            circleImage.setImageResource(R.drawable.wallet);
        }
        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LedgeChoosenActivity.class);
                intent.putExtra("ledger_id", ledger_id);
                startActivityForResult(intent,1);
            }
        });
        mTitleTextView.setText("Tiền của tôi:");
        if(my_money != null){
            mCashTextView.setText(my_money);
        }else {
            mCashTextView.setText("0,00đ");
        }
        mEdtDate.setText(edtDateText);

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if(txtDateHistory != null){
                    setActionBarLayout(txtDateHistory);
                }else {
                    setActionBarLayout("Chọn ngày");
                }
                initDatepicker();
            }
        }
    }

    private Long atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private Long atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
}
