package project.baonq.ui;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import project.baonq.menu.R;
import project.baonq.service.AuthenticationService;
import project.baonq.service.BaseAuthService;

public class LoginActivity extends AppCompatActivity {
    TextView txtError;

    AuthenticationService authenticationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check if user is logged in
        authenticationService = new AuthenticationService(this);
        if (BaseAuthService.getJwt() != null) {
            finish();
        }
        //if user has not been logged in
        setContentView(R.layout.activity_login);
        txtError = findViewById(R.id.txtLoginError);
        setErrorMessage("");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void setErrorMessage(String message) {
        txtError.setText(message);
    }

    public void clickToLogin(View view) {
        String username = ((TextView) findViewById(R.id.txtLoginUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.txtLoginPassword)).getText().toString();
        try {
            String jwt = authenticationService.login(username, password);
            if (jwt != null) {
                finish();
            } else {
                setErrorMessage("Wrong username and password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            setErrorMessage(e.getMessage());
        }
    }
}
