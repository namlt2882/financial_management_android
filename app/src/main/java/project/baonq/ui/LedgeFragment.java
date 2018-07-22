package project.baonq.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import project.baonq.menu.R;
import project.baonq.model.DaoSession;
import project.baonq.model.Transaction;
import project.baonq.service.App;
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

        View view = inflater.inflate(R.layout.ledge_fragment_layout,null);
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        list = new TransactionService(daoSession).getAll();
        sortListByTdate();
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.loadTransaction);
        String tmp = "";
//        View wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
//        LinearLayout layout_in_wrapLayout = (LinearLayout)wrap_transaction.findViewById(R.id.wrap_transaction);;
//
//        wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
//        View transactionLayout = getLayoutInflater().inflate(R.layout.transaction_info, null);
//        layout_in_wrapLayout.addView(transactionLayout);
//        linearLayout.addView(wrap_transaction);


        for (int i =0; i<list.size();i++)
        {


            if (ledgerId == 0) {
                if (list.get(i).getTdate() != tmp) {
                    tmp = list.get(i).getTdate();
                    View wrap_transaction = getLayoutInflater().inflate(R.layout.wrap_transaction_layout, null);
                    LinearLayout layout_in_wrapLayout = (LinearLayout) wrap_transaction.findViewById(R.id.wrap_transaction);

                    ((TextView) layout_in_wrapLayout.findViewById(R.id.textView3)).setText(list.get(i).getTdate());
                    View transactionLayout = getLayoutInflater().inflate(R.layout.transaction_info, null);
                    layout_in_wrapLayout.addView(transactionLayout);
                    linearLayout.addView(wrap_transaction);
                }
            }

        }

        return view;
    }

    public void sortListByTdate(){
        for (int i = 0;i<(list.size()-1);i++)
        {
            for (int j =i+1;j<list.size();j++){
                long tmp1 = Date.parse(list.get(i).getTdate());
                long tmp2 = Date.parse(list.get(j).getTdate());
                if (tmp1 < tmp2)
                {
                    Transaction r = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j,r);

                }
            }
        }
    }
}
