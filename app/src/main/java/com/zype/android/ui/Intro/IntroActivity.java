package com.zype.android.ui.Intro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zype.android.R;
import com.zype.android.ui.Consumer.ConsumerActivity;
import com.zype.android.ui.LoginActivity;
import com.zype.android.ui.base.BaseActivity;
import com.zype.android.ui.main.MainActivity;

/**
 * Created by Evgeny Cherkasov on 27.06.2017.
 */

public class IntroActivity extends BaseActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();

    public static final String PARAMETERS_MODE = "Mode";

    public static final int MODE_LAUNCH = 1;
    public static final int MODE_PLAYLIST = 2;

    private static final int REQUEST_LOGIN = 100;

    private Button buttonTrial;
    private Button buttonLogin;
    private Button buttonBrowse;

    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initParameters(savedInstanceState);

        buttonTrial = (Button) findViewById(R.id.buttonTrial);
        buttonTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToCreateConsumerScreen();
            }
        });
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLoginScreen();
            }
        });
        buttonBrowse = (Button) findViewById(R.id.buttonBrowse);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mode) {
                    case MODE_LAUNCH:
                        switchToMainScreen();
                        break;
                    case MODE_PLAYLIST:
                        onBackPressed();
                        break;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PARAMETERS_MODE, mode);
    }

    @Override
    protected String getActivityName() {
        return TAG;
    }

    private void initParameters(Bundle savedInstanceState) {
        Bundle args;
        if (savedInstanceState != null) {
            args = savedInstanceState;
        }
        else {
            args = getIntent().getExtras();
        }
        if (args != null) {
            mode = args.getInt(PARAMETERS_MODE, MODE_LAUNCH);
        }
        else {
            mode = MODE_LAUNCH;
        }
    }

    // //////////
    // UI
    //
    private void switchToCreateConsumerScreen() {
        Intent intent = new Intent(this, ConsumerActivity.class);
        startActivity(intent);
    }

    private void switchToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void switchToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // //////////
    //
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    switchToMainScreen();
                }
                break;
        }
    }
}
