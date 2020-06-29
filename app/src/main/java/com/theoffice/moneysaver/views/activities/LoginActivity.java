package com.theoffice.moneysaver.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.hmf.tasks.Task;
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
            if(!repository.validateUser(huaweiAccount.getUnionId())){
                repository.createUser(huaweiAccount.getUnionId());
            }
        } catch (IOException e) {
            Log.e(AppConstants.MONEY_SAVER_ERROR, "Error at validate user");
        }
        //TODO Load goal list

        String photoPath;

        if (!huaweiAccount.getAvatarUriString().isEmpty()){
            photoPath = huaweiAccount.getAvatarUriString();
        }else {
            photoPath = AppConstants.USER_PLACEHOLDER;
        }

        User user = new User(
                huaweiAccount.getIdToken(),
                huaweiAccount.getDisplayName(),
                photoPath
        );

        ApplicationMoneySaver.setMainUser(user);
        Intent mainActIntent = new Intent(this, MainActivity.class);
        startActivity(mainActIntent);
    }

    private void loginWithHuaweiAccount() {
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(this, authParams);
        startActivityForResult(service.getSignInIntent(), AppConstants.HUAWEI_LOGIN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.HUAWEI_LOGIN_CODE) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                launchMainActivity(authHuaweiIdTask.getResult());
            } else {
                Log.e(AppConstants.MONEY_SAVER_ERROR, "Sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
}