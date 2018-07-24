package project.baonq.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import project.baonq.enumeration.TransactionGroupType;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final App application = (App) getActivity().getApplication();
        View view = inflater.inflate(R.layout.ledge_fragment_layout, container, false);
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        list = new TransactionService(getActivity().getApplication()).getAll();
        sortListByTdate();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.loadTransaction);
        String tmp = "";


        View wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
        LinearLayout layout_in_wrapLayout = (LinearLayout) wrap_transaction.findViewById(R.id.wrap_transaction);
        ;

        wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
        View transactionLayout = getLayoutInflater().inflate(R.layout.transaction_info, null);

        double totalInDay = 0;
        for (int i = 0; i < list.size(); i++) {
            Transaction transaction = list.get(i);
            TransactionGroup r;
            if (ledgerId == 0) {
                transactionLayout = getLayoutInflater().inflate(R.layout.transaction_info, null);
                if (!transaction.getTdate().equals(tmp)) {
                    tmp = transaction.getTdate();
                    wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
                    layout_in_wrapLayout = (LinearLayout) wrap_transaction.findViewById(R.id.wrap_transaction);
                    r = new TransactionGroupService(((App) getActivity().getApplication())).getTransactionGroupByID(transaction.getGroup_id());
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView3)).setText(transaction.getTdate());
                } else {
                    r = new TransactionGroupService(((App) getActivity().getApplication())).getTransactionGroupByID(transaction.getGroup_id());
                    ((TextView) transactionLayout.findViewById(R.id.txtAmount)).setText(String.valueOf(transaction.getBalance()));
                }
                ((TextView) transactionLayout.findViewById(R.id.txtNote)).setText(String.valueOf(transaction.getNote()));
                ((TextView) transactionLayout.findViewById(R.id.txtCategory)).setText(r.getName());
                String balance = "";
                String color = "#00ff00";
                if (r.getTransaction_type() == TransactionGroupType.INCOME.getType()) {
                    balance += ("+" + transaction.getBalance());
                    totalInDay += transaction.getBalance();
                } else {
                    balance += ("-" + transaction.getBalance());
                    totalInDay -= transaction.getBalance();
                    color = "#ff0000";
                }
                ((TextView) transactionLayout.findViewById(R.id.txtAmount))
                        .setText(balance);
                ((TextView) transactionLayout.findViewById(R.id.txtAmount))
                        .setTextColor(Color.parseColor(color));
                layout_in_wrapLayout.addView(transactionLayout);
            }
            if (i == (list.size() - 1) || !list.get(i + 1).getTdate().equals(tmp)) {
                if (totalInDay > 0) {
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setText(String.valueOf(totalInDay));
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setTextColor(Color.parseColor("#00ff00"));
                } else {
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setText(String.valueOf(totalInDay));
                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView4)).setTextColor(Color.parseColor("#ff0000"));
                }
                totalInDay = 0;
                linearLayout.addView(wrap_transaction);
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
