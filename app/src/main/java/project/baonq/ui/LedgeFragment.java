package project.baonq.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import project.baonq.AddTransaction.AddTransaction;
import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.model.TransactionGroupDao;
import project.baonq.service.App;
import project.baonq.service.TransactionGroupService;
import project.baonq.service.TransactionService;


public class LedgeFragment extends Fragment {

    private Long ledgerId = 0L;
    private DaoSession daoSession;
    private List<Transaction> list = null;
    public Date dateTo = MainActivity.dateTo;
    public Date dateFrom = MainActivity.dateFrom;

    public LedgeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LedgeFragment newInstance() {
        LedgeFragment fragment = new LedgeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private int checkInRange(String cur, Date first, Date last) {
        Date current = null;
        String first1 = String.valueOf(first.getMonth() + 1) + "/" + String.valueOf(first.getDate()) + "/" + String.valueOf(first.getYear());
        String last1 = String.valueOf(last.getMonth() + 1) + "/" + String.valueOf(last.getDate()) + "/" + String.valueOf(last.getYear());
        Date dateFirst = null;
        Date dateLast = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            current = sdf.parse(cur);
            dateFirst = sdf.parse(first1);
            dateLast = sdf.parse(last1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (current.after(dateFirst) && current.before(dateLast)) return 1;
        return 0;
    }

    private View getTransactionLayout(View layout, Transaction item, TransactionGroup r) {
        View tmp = getLayoutInflater().inflate(R.layout.transaction_info, null);
        ((TextView) tmp.findViewById(R.id.txtCategory)).setText(r.getName());
        ((TextView) tmp.findViewById(R.id.txtNote)).setText(String.valueOf(item.getNote()));

        if (r.getTransaction_type() == 1) {
            totalInDay += item.getBalance();
            ((TextView) tmp.findViewById(R.id.txtAmount)).setText(String.valueOf(item.getBalance()));
            ((TextView) tmp.findViewById(R.id.txtAmount))
                    .setTextColor(Color.parseColor("#00ff00"));
        } else {
            totalInDay -= item.getBalance();
            ((TextView) tmp.findViewById(R.id.txtAmount))
                    .setText(String.valueOf(item.getBalance()));
            ((TextView) tmp.findViewById(R.id.txtAmount))
                    .setTextColor(Color.parseColor("#ff0000"));
        }
        tmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click on transaction", String.valueOf(item.getId()));
                Intent intent = new Intent(getActivity(), AddTransaction.class);
                intent.putExtra("transactionID", item.getId());
                startActivity(intent);
            }
        });
        return tmp;
    }

    private double totalInDay = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ledge_fragment_layout, container, false);
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        list = new TransactionService(getActivity().getApplication()).getAll();
        sortListByTdate();
        String tmp = "";
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.loadTransaction);
        View wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
        LinearLayout layout_in_wrapLayout = (LinearLayout) wrap_transaction.findViewById(R.id.wrap_transaction);

        View transactionLayout = null;

        for (int i = 0; i < list.size(); i++) {
            Transaction item = list.get(i);
            TransactionGroup r = new TransactionGroupService(((App) getActivity().getApplication())).getTransactionGroupByID(list.get(i).getGroup_id());
            if (checkInRange(item.getTdate(), dateFrom, dateTo) != 1 || item.getStatus() != 1) {
                continue;
            }
            if (ledgerId == 0L || ledgerId == item.getLedger_id()) {
                //neu chua co wrap_transaction_layout
                if (!item.getTdate().equals(tmp)) {
                    tmp = item.getTdate();
                    wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
                    layout_in_wrapLayout = (LinearLayout) wrap_transaction.findViewById(R.id.wrap_transaction);
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView3)).setText(item.getTdate());
                }
                //them transactionLayout vao wraptransactionlayout
                transactionLayout = getLayoutInflater().inflate(R.layout.transaction_info, null);
                layout_in_wrapLayout.addView(getTransactionLayout(transactionLayout, item, r));


                ///add wraplayout vao man hinh
                if (i == (list.size() - 1) || !list.get(i + 1).getTdate().equals(tmp)) {
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setText(String.valueOf(totalInDay));
                    if (totalInDay > 0) {
                        ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setTextColor(Color.parseColor("#00ff00"));
                    } else {
                        ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setTextColor(Color.parseColor("#ff0000"));
                    }
                    totalInDay = 0;
                    linearLayout.addView(wrap_transaction);
                    linearLayout.addView(getLayoutInflater().inflate(R.layout.gray_bar, null));
                }
            }
        }
        return view;
    }

    public void sortListByTdate() {
        for (int i = 0; i < (list.size() - 1); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                long tmp1 = Date.parse(list.get(i).getTdate());
                long tmp2 = Date.parse(list.get(j).getTdate());
                if (tmp1 < tmp2) {
                    Transaction r = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, r);

                }
            }
        }
    }
}
