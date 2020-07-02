package com.theoffice.moneysaver.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnHuaweiLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnHuaweiLogin = findViewById(R.id.btn_login_huawei);
        btnHuaweiLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_huawei:
                loginWithHuaweiAccount();
                break;
        }
    }

    private void launchMainActivity(AuthHuaweiId huaweiAccount){
        MoneySaverRepository repository = MoneySaverRepository.getInstance();
        try {
            String userId = repository.getUserId(huaweiAccount.getUnionId());
            if(userId == null){
                userId = repository.createUser(huaweiAccount.getUnionId());
            }
            String photoPath = huaweiAccount.getAvatarUriString().isEmpty()
                    ? AppConstants.USER_PLACEHOLDER : huaweiAccount.getAvatarUriString();
            User user = new User(userId, huaweiAccount.getDisplayName(), photoPath);
            ApplicationMoneySaver.setMainUser(user);
            savePushToken();
            Intent mainActIntent = new Intent(this, MainActivity.class);
            startActivity(mainActIntent);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(AppConstants.MONEY_SAVER_ERROR, "Error at validate user");
        }
    }

    private void loginWithHuaweiAccount() {
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
            }
        }
    }
}