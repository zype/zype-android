package com.zype.android.ui.Consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zype.android.R;
import com.zype.android.ui.NavigationHelper;
import com.zype.android.ui.base.BaseActivity;

import static com.zype.android.utils.BundleConstants.REQUEST_CONSUMER;
import static com.zype.android.utils.BundleConstants.REQUEST_LOGIN;

public class UnAuthorizedUserActivity extends BaseActivity {
  private static final String TAG = UnAuthorizedUserActivity.class.getSimpleName();

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.activity_unauthorised);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setTitle(R.string.unauthorised_title);


  }

  @Override
  protected String getActivityName() {
    return TAG;
  }

  public void signIn(View view) {
    NavigationHelper.getInstance(this).switchToLoginScreen(this);

  }

  public void signUp(View view) {
    NavigationHelper.getInstance(this).switchToConsumerScreen(this);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_CONSUMER:
      case REQUEST_LOGIN:
        if (resultCode == RESULT_OK) {
          Intent resultData = new Intent();
          resultData.putExtras(getIntent().getExtras());
          setResult(RESULT_OK, resultData);

        } else {
          setResult(RESULT_CANCELED);
        }
        finish();
        break;
    }
  }
}
