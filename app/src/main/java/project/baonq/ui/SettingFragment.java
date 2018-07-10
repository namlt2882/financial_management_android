package project.baonq.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import project.baonq.menu.R;
import project.baonq.service.AuthenticationService;

public class SettingFragment extends Fragment {
    public SettingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //set logout button action
        View settingLayout = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        Button btnLogout = settingLayout.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToLogout();
                ((MainActivity) getActivity()).restartApp();
            }
        });
        return settingLayout;
    }

    public void clickToLogout() {
        AuthenticationService authenticationService = new AuthenticationService(getContext());
        try {
            authenticationService.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
