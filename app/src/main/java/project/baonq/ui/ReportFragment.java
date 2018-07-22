package project.baonq.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import project.baonq.menu.R;
import project.baonq.model.Transaction;
import project.baonq.model.TransactionGroup;
import project.baonq.service.TransactionGroupService;
import project.baonq.service.TransactionService;


public class ReportFragment extends Fragment {
    MainActivity mainActivity;
    List<String> nameList;
    List<Double> valueList;
    Long startTime;
    Long endTime;

    public ReportFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance() {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startTime = mainActivity.startTime;
        endTime = mainActivity.endTime;
        getDataForPie(2L, 1);
        PieChart chartIncome = (PieChart) getView().findViewById(R.id.inComePieChart);
        setUpPieChart(valueList, nameList, chartIncome);
        getDataForPie(2L, 2);
        PieChart chartExpand = (PieChart) getView().findViewById(R.id.expandPieChart);
        setUpPieChart(valueList, nameList, chartExpand);

    }

    private void setUpPieChart(List<Double> value, List<String> key, PieChart pieChart) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {

            pieEntries.add(new PieEntry(Float.parseFloat(value.get(i).toString()), key.get(i)));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(16);
        PieData pieData = new PieData(dataSet);
        PieChart chart = pieChart;
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setData(pieData);
        chart.invalidate();
    }

    private void getDataForPie(Long ledger_id, int typeAspect) {
        Application application = mainActivity.getApplication();
        List<Transaction> transactionList = new TransactionService(application).getByLedgerId(ledger_id);
        nameList = new ArrayList<>();
        valueList = new ArrayList<>();
        for (Transaction item : transactionList) {
            Long groupId = item.getGroup_id();
            TransactionGroup transactionGroup = new TransactionGroupService(application).getTransactionGroupByID(groupId);
            int type = transactionGroup.getTransaction_type();
            if (type == typeAspect) {
                nameList.add(transactionGroup.getName());
                valueList.add(item.getBalance());
            }
        }
    }

    private Transaction compareTransaction(Transaction transaction) {
        return null;
    }
}
