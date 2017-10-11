package com.chandigarhadmin.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chandigarhadmin.R;
import com.chandigarhadmin.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    @BindView(R.id.titletv)
    TextView tvTitle;
    @BindView(R.id.proceedbtn)
    Button btnProceed;

    @BindView(R.id.etlastname)
    EditText etlastname;
    @BindView(R.id.etusername)
    EditText etUserName;

    @OnClick(R.id.proceedbtn)
    public void clickProceedButton() {
        if (validateInputs()) {
            Intent it = new Intent(LoginActivity.this, OTPActivity.class);
            it.putExtra(Constant.INPUT_FIRST_NAME, etUserName.getText().toString());
            it.putExtra(Constant.INPUT_LAST_NAME, etlastname.getText().toString());
            startActivity(it);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

        ButterKnife.bind(this);
    }

    private boolean validateInputs() {

        if (!TextUtils.isEmpty(etUserName.getText().toString().trim()) && etUserName.getText().length() > 3) {

            return validateLastName();
        } else {
            etUserName.setError(getString(R.string.username_error));
        }
        return false;
    }

    private boolean validateLastName() {
        if (!TextUtils.isEmpty(etlastname.getText().toString().trim()) && etlastname.getText().length() > 3) {
            return true;
        } else {
            etlastname.setError(getString(R.string.username_error));
        }
        return false;
    }
}
