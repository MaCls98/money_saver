package com.theoffice.moneysaver.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.theoffice.moneysaver.ApplicationMoneySaver;
import com.theoffice.moneysaver.R;
import com.theoffice.moneysaver.data.model.User;
import com.theoffice.moneysaver.data.repositories.MoneySaverRepository;
import com.theoffice.moneysaver.utils.AppConstants;
import com.theoffice.moneysaver.utils.MyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnHuaweiLogin;
    private CheckBox cbAutoLogin;
    private ProgressBar pbLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnHuaweiLogin = findViewById(R.id.btn_login_huawei);
        btnHuaweiLogin.setOnClickListener(this);
        cbAutoLogin = findViewById(R.id.cb_auto_login);
        pbLogin = findViewById(R.id.pb_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAutoLogin = sharedPreferences.getBoolean(getString(R.string.auto_login_key), false);
        if (isAutoLogin){
            MyToast.showShortToast(getString(R.string.starting_login), getApplicationContext());
            btnHuaweiLogin.setVisibility(View.GONE);
            cbAutoLogin.setVisibility(View.GONE);
            pbLogin.setVisibility(View.VISIBLE);
            loginWithHuaweiAccount();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login_huawei) {
            loginWithHuaweiAccount();
        }
    }

    private void launchMainActivity(AuthHuaweiId huaweiAccount){
        MoneySaverRepository repository = MoneySaverRepository.getInstance();
        try {
            String userId = repository.getUserId(huaweiAccount.getUnionId());
            if(userId == null){
                userId = repository.createUser(huaweiAccount.getUnionId(), huaweiAccount.getDisplayName());
            }
            String photoPath = huaweiAccount.getAvatarUriString().isEmpty()
                    ? AppConstants.USER_PLACEHOLDER : huaweiAccount.getAvatarUriString();
            User user = new User(userId, huaweiAccount.getDisplayName(), photoPath);
            ApplicationMoneySaver.setMainUser(user);
            savePushToken();
            saveLoginPreference();
            Intent mainActIntent = new Intent(this, MainActivity.class);
            startActivity(mainActIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(AppConstants.MONEY_SAVER_ERROR, "Error at validate user");
        }
    }

    private void saveLoginPreference() {
        if (cbAutoLogin.isChecked()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.auto_login_key), true);
            editor.apply();
        }
    }

    private void loginWithHuaweiAccount() {
        btnHuaweiLogin.setVisibility(View.GONE);
        cbAutoLogin.setVisibility(View.GONE);
        pbLogin.setVisibility(View.VISIBLE);
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams();
        final HuaweiIdAuthService service = HuaweiIdAuthManager.getService(this, authParams);
        final Task<AuthHuaweiId> task = service.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthHuaweiId>() {
            @Override
            public void onSuccess(AuthHuaweiId authHuaweiId) {
                launchMainActivity(task.getResult());
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                startActivityForResult(service.getSignInIntent(), AppConstants.HUAWEI_LOGIN_CODE);
            }
        });
    }

    private void savePushToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(LoginActivity.this).getString("client/app_id");
                    String pushToken = HmsInstanceId.getInstance(LoginActivity.this).getToken(appId, "HCM");
                    MoneySaverRepository.getInstance().sendTokenPushKit(pushToken);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.HUAWEI_LOGIN_CODE) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                launchMainActivity(authHuaweiIdTask.getResult());
            } else {
                Log.e(AppConstants.MONEY_SAVER_ERROR, "Sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
                btnHuaweiLogin.setVisibility(View.VISIBLE);
                cbAutoLogin.setVisibility(View.VISIBLE);
                pbLogin.setVisibility(View.GONE);
            }
        }
    }
}