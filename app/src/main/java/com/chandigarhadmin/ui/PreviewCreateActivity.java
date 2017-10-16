package com.chandigarhadmin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.chandigarhadmin.App;
import com.chandigarhadmin.R;
import com.chandigarhadmin.interfaces.ResponseCallback;
import com.chandigarhadmin.models.BranchesModel;
import com.chandigarhadmin.models.CreateTicketModel;
import com.chandigarhadmin.models.CreateTicketResponse;
import com.chandigarhadmin.models.RequestParams;
import com.chandigarhadmin.session.SessionManager;
import com.chandigarhadmin.utils.Constant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

import static com.chandigarhadmin.utils.Constant.INPUT_TICKET_DESC;
import static com.chandigarhadmin.utils.Constant.INPUT_TICKET_ID;
import static com.chandigarhadmin.utils.Constant.INPUT_TICKET_SUBJECT;

/**
 * creating ticket or preview ticket class
 */

public class PreviewCreateActivity extends AppCompatActivity implements ResponseCallback {
    @BindView(R.id.spdepartment)
    Spinner spDeparment;
    @BindView(R.id.tvsubject_value)
    EditText etSubject;
    @BindView(R.id.tvdescription_value)
    EditText etDescription;
    @BindView(R.id.createbtn)
    Button createButton;
    @BindView(R.id.crossicon)
    ImageView close;
    private String brnachId, ticketSubject, ticketDescription;
    final List<String> branchIdList = new ArrayList<>();
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_preview);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        progressDialog=Constant.createDialog(this,null);
        getIntentData();
        getBranches();
        setPreviewData();
    }

    @OnClick(R.id.createbtn)
    public void createTicketButtonClick() {
        if (etSubject.getText().toString().trim().length() > 0) {
            if (etDescription.getText().toString().trim().length() > 0) {
                createTicket();

            } else {
                etDescription.setError(getResources().getString(R.string.error_desc));
            }

        } else {
            etSubject.setError(getResources().getString(R.string.error_subject));
        }

    }

    @OnClick(R.id.crossicon)
    public void closeButtonClick() {
        finish();

    }

    private void getIntentData() {
        if (getIntent().hasExtra(INPUT_TICKET_ID)) {
            brnachId = getIntent().getStringExtra(INPUT_TICKET_ID);
        }
        if (getIntent().hasExtra(INPUT_TICKET_SUBJECT)) {
            ticketSubject = getIntent().getStringExtra(INPUT_TICKET_SUBJECT);
        }
        if (getIntent().hasExtra(INPUT_TICKET_DESC)) {
            ticketDescription = getIntent().getStringExtra(INPUT_TICKET_DESC);
        }
    }


    private void getBranches() {
        progressDialog.show();
        App.getApiController().getBranches(this, RequestParams.TYPE_GET_BRANCHES);
    }

    @Override
    public void onResponse(Response response, String type) {
        progressDialog.dismiss();
        if (response.isSuccessful()) {
            if (type.equalsIgnoreCase(RequestParams.TYPE_GET_BRANCHES)) {
                List<BranchesModel> branches = (List<BranchesModel>) response.body();
                setSpinnerData(branches);

            } else if (type.equalsIgnoreCase(RequestParams.TYPE_CREATE_TICKET)) {
                CreateTicketResponse createTicketResponse = (CreateTicketResponse) response.body();
                Intent intent = new Intent();
                // intent.putExtra(Constant.INPUT_TICKET_ID, createTicketResponse.getId());
                intent.putExtra(Constant.INPUT_TICKET_CREATE, createTicketResponse);
                setResult(Constant.PC_REQUEST_CODE, intent);
                finish();

            }
        } else {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("error")) {
                    Constant.showToastMessage(this, jObjError.getString("error"));
                }
            } catch (Exception e) {
                Constant.showToastMessage(this, getResources().getString(R.string.something_wrong));
            }
        }

    }

    @Override
    public void onFailure(String message) {

    }

    private void setSpinnerData(List<BranchesModel> branchesList) {
        List<String> branchNames = new ArrayList<>();

        int indexbranch = -1;
        branchIdList.clear();
        for (int i = 0; i < branchesList.size(); i++) {
            if (!branchesList.get(i).getName().trim().equalsIgnoreCase("Default branch")) {
                if (null != brnachId && brnachId.equals(branchesList.get(i).getId())) {
                    indexbranch = i;
                }
                branchNames.add(branchesList.get(i).getName());
                branchIdList.add(branchesList.get(i).getId());
            }
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spDeparment.setAdapter(dataAdapter);
        if (indexbranch != -1) {
            spDeparment.setSelection(indexbranch - 1);
        }

    }

    private void setPreviewData() {
        if (null != ticketSubject) {
            etSubject.setText(ticketSubject);
        }
        if (null != ticketDescription) {
            etDescription.setText(ticketDescription);
        }
    }

    private void createTicket() {
        progressDialog.show();
        CreateTicketModel model = new CreateTicketModel();
        model.setBranch(branchIdList.get(spDeparment.getSelectedItemPosition()));
        model.setSubject(etSubject.getText().toString().trim());
        model.setDescription(etDescription.getText().toString().trim());
        model.setStatus("new");
        model.setPriority("high");
        model.setSource("email");
        model.setReporter("diamante_" + sessionManager.getKeyUserId());
        App.getApiController().createTicket(this, model, RequestParams.TYPE_CREATE_TICKET);

    }
}
