package com.chandigarhadmin.ui;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chandigarhadmin.App;
import com.chandigarhadmin.R;
import com.chandigarhadmin.adapter.ChatAdapter;
import com.chandigarhadmin.interfaces.ResponseCallback;
import com.chandigarhadmin.interfaces.SelectionCallbacks;
import com.chandigarhadmin.models.BranchesModel;
import com.chandigarhadmin.models.ChatPojoModel;
import com.chandigarhadmin.models.CreateTicketModel;
import com.chandigarhadmin.models.CreateTicketResponse;
import com.chandigarhadmin.models.GetTicketResponse;
import com.chandigarhadmin.models.RequestParams;
import com.chandigarhadmin.session.SessionManager;
import com.chandigarhadmin.utils.Constant;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;

public class AdminAgentActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AIListener, SelectionCallbacks, ResponseCallback, GoogleApiClient.OnConnectionFailedListener {
    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final static int MY_PERMISSIONS_RECORD_AUDIO = 1;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.querystringet)
    EditText etInputBox;
    @BindView(R.id.btn_chat_search)
    ImageView sendicon;
    @BindView(R.id.keyboardicon)
    ImageView keyboardicon;
    @BindView(R.id.micicon)
    ImageView micicon;
    @BindView(R.id.recognition_view)
    RecognitionProgressView recognitionProgressView;
    private AIService aiService;
    private SpeechRecognizer speechRecognizer;
    private AIConfiguration aiConfiguration;
    private ArrayList<ChatPojoModel> chatBotResponseList;
    private SessionManager sessionManager;
    private ChatAdapter mAdapter;
    private TextToSpeech textToSpeech;
    private CreateTicketResponse createTicketResponse;
    private String branchName;
    private List<BranchesModel> branches;
    private GoogleApiClient mGoogleApiClient;


    @OnClick(R.id.btn_chat_search)
    public void sendClick() {
        if (!etInputBox.getText().toString().equalsIgnoreCase("")) {
            String input = etInputBox.getText().toString();
            setChatInputs(input, true);
            etInputBox.setText("");
            sendRequest(input);
        }
    }

    @OnClick(R.id.keyboardicon)
    public void keyboardClick() {
        keyboardicon.setVisibility(View.GONE);
        micicon.setVisibility(View.VISIBLE);
        recognitionProgressView.setVisibility(View.GONE);
        etInputBox.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.micicon)
    public void micClick() {
        keyboardicon.setVisibility(View.VISIBLE);
        sendicon.setVisibility(View.GONE);
        recognitionProgressView.setVisibility(View.VISIBLE);
        micicon.setVisibility(View.GONE);
        etInputBox.setVisibility(View.GONE);
        startRecognition();
        recognitionProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRecognition();
            }
        }, 50);
    }

    @OnClick(R.id.recognition_view)
    public void recognitionClick() {
        startRecognition();
        recognitionProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRecognition();
            }
        }, 50);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        ButterKnife.bind(this);
        requestAudioPermissions();
        sessionManager = new SessionManager(this);
        initializeAI();
        initializeViews();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
        setChatInputs(getResources().getString(R.string.assistance), false);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(this, MyAccountActivity.class));
                return true;
            case R.id.createticket:
                previewCreateTicket(null);
                return true;
            case R.id.view_tickets:
                Intent intent1 = new Intent(AdminAgentActivity.this, AllTicketsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.settings:
            case R.id.what_you_do:
            case R.id.help:
            case R.id.send_feedback:
                Constant.showToastMessage(AdminAgentActivity.this, getResources().getString(R.string.out_of_scope));
                return true;
            case R.id.logout_menu:
                sessionManager.logoutUser();
                signOut();
                Intent intent = new Intent(AdminAgentActivity.this, LanguageSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    //Method to initialize AI
    private void initializeAI() {
        aiConfiguration = new AIConfiguration(Constant.AI_CONFIGURATION_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, aiConfiguration);
        aiService.setListener(this);
    }

    //Method to initialize recyclerview
    private void initializeViews() {
        chatBotResponseList = new ArrayList<>();
        mAdapter = new ChatAdapter(this, chatBotResponseList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        // mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        etInputBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (!etInputBox.getText().toString().equalsIgnoreCase("")) {
                        String input = etInputBox.getText().toString();
                        setChatInputs(input, true);
                        etInputBox.setText("");
                        sendRequest(input);
                    }
                    return true;
                }
                return false;
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AdminAgentActivity.this);

        int[] colors = {
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorPrimary)
        };
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });
        recognitionProgressView.setColors(colors);
        int[] heights = {20, 24, 18, 23, 16};
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.play();


    }

    //AIRequest should have query OR event
    private void sendRequest(String queryString) {
       // createTicketResponse = null;
        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {
            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                try {
                    return aiService.textRequest(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };
        task.execute(queryString);
    }

    @Override
    public void onResult(AIResponse response) {

        Result result = response.getResult();
        if (Constant.isNetworkAvailable(AdminAgentActivity.this)) {
            if (result.getAction().equalsIgnoreCase(getResources().getString(R.string.createticket))) {

                if (result.getParameters().get(getResources().getString(R.string.department)).toString().equalsIgnoreCase("[]")) {
                    App.getApiController().getBranches(this, RequestParams.TYPE_GET_BRANCHES);
                } else if (result.getParameters().get(getResources().getString(R.string.ticketsubject)).toString().equalsIgnoreCase("[]")) {
                    setChatInputs(response.getResult().getFulfillment().getSpeech(), false);
                } else if (result.getParameters().get(getResources().getString(R.string.ticketdesc)).toString().equalsIgnoreCase("[]")) {
                    setChatInputs(response.getResult().getFulfillment().getSpeech(), false);
                } else if (result.getFulfillment().getSpeech().equalsIgnoreCase(getResources().getString(R.string.save_ticket))) {
                    // TODO: 29/09/17 need to show the preview
                    //previewTicket(result);
                    previewCreateTicket(result);

                }
            } else if (result.getAction().equalsIgnoreCase(getResources().getString(R.string.fetchalltickets))) {
                getTickets();
            } else {
                setChatInputs(response.getResult().getFulfillment().getSpeech(), false);
            }
        } else {
            Constant.showToastMessage(AdminAgentActivity.this, getString(R.string.no_internet));
        }

    }

    @Override
    public void onError(AIError error) {
        // do nothing
    }

    @Override
    public void onAudioLevel(float level) {
        // do nothing
    }

    @Override
    public void onListeningStarted() {
        // do nothing
    }

    @Override
    public void onListeningCanceled() {
        // do nothing

    }

    @Override
    public void onListeningFinished() {
        // do nothing
    }


    private void setChatInputs(String input, boolean align) {
        ChatPojoModel chatPojoModel = new ChatPojoModel();
        chatPojoModel.setAlignRight(align);
        chatPojoModel.setInput(input);
        chatPojoModel.setDepartmentResponse(null);
        chatPojoModel.setCreateTicketResponse(createTicketResponse);
        chatBotResponseList.add(chatPojoModel);
        mAdapter.notifyDataSetChanged();
        if (!align)
            textToSpeech.speak(input, TextToSpeech.QUEUE_FLUSH, null);
        //making code to autoscroll when layout changes
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void parseBranches(List<BranchesModel> branchesModels) {
        ChatPojoModel chatPojoModel = new ChatPojoModel();
        chatPojoModel.setAlignRight(false);
        chatPojoModel.setDepartmentResponse(branchesModels);
        chatBotResponseList.add(chatPojoModel);

        mAdapter.notifyDataSetChanged();
        //making code to autoscroll when layout changes
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void parseTickets(List<GetTicketResponse> ticketResponseList) {
        ChatPojoModel chatPojoModel = new ChatPojoModel();
        chatPojoModel.setAlignRight(false);
        chatPojoModel.setGetTicketResponse(ticketResponseList);
        chatBotResponseList.add(chatPojoModel);
        mAdapter.notifyDataSetChanged();
        //making code to autoscroll when layout changes
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void showResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (!matches.isEmpty()) {
            setChatInputs(matches.get(0), true);
            sendRequest(matches.get(0));
        }
        recognitionProgressView.stop();
        recognitionProgressView.play();
    }

    @Override
    public void onResultSelection(String id, String branchName) {
        this.branchName = branchName;
        sendRequest(branchName);
    }

    /**
     * saving ticket
     */
    private void createTicket(String branchId, String subject, String description) {
        CreateTicketModel model = new CreateTicketModel();
        model.setBranch(branchId);
        model.setSubject(subject);
        model.setDescription(description);
        model.setStatus("new");
        model.setPriority("high");
        model.setSource("email");
        model.setReporter("diamante_" + sessionManager.getKeyUserId());
        App.getApiController().createTicket(this, model, RequestParams.TYPE_CREATE_TICKET);
    }

    /**
     * getting all tickets
     */
    private void getTickets() {
        App.getApiController().getAllTickets(this, sessionManager.getKeyUserId(), RequestParams.TYPE_GET_ALL_TICKET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AIService.getService(this, aiConfiguration).cancel();
    }

    private void startRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer.startListening(intent);
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Constant.showToastMessage(this, getString(R.string.grant_audio));

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
            } else {

            }
        }
    }



    private void previewCreateTicket(final Result result) {
        Intent intent = new Intent(AdminAgentActivity.this, PreviewCreateActivity.class);
        if (null != result) {
            intent.putExtra(Constant.INPUT_TICKET_ID, result.getParameters().get(getResources().getString(R.string.department)).toString().replaceAll("\"", "").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", ""));
            intent.putExtra(Constant.INPUT_TICKET_SUBJECT, result.getParameters().get(getResources().getString(R.string.ticketsubject)).toString().replaceAll("\"", "").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", ""));
            intent.putExtra(Constant.INPUT_TICKET_DESC, result.getParameters().get(getResources().getString(R.string.ticketdesc)).toString().replaceAll("\"", "").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", ""));
        }
        startActivityForResult(intent, Constant.PC_REQUEST_CODE);
    }

    @Override
    public void onResponse(Response response, String type) {
        if (response.isSuccessful()) {
            if (type.equalsIgnoreCase(RequestParams.TYPE_GET_BRANCHES)) {
                if (null != branches) {
                    branches.clear();
                }
                branches = (List<BranchesModel>) response.body();
                setChatInputs(getResources().getString(R.string.select_department), false);
                parseBranches(branches);
            } else if (type.equalsIgnoreCase(RequestParams.TYPE_GET_ALL_TICKET)) {
                List<GetTicketResponse> tickets = (List<GetTicketResponse>) response.body();
                setChatInputs(getResources().getString(R.string.here_go), false);
                parseTickets(tickets);
            } else if (type.equalsIgnoreCase(RequestParams.TYPE_CREATE_TICKET)) {
              //  createTicketResponse = (CreateTicketResponse) response.body();
              //  setChatInputs(getResources().getString(R.string.feedback_created) + createTicketResponse.getId(), false);
            }
        } else {
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("error")) {
                    Constant.showToastMessage(AdminAgentActivity.this, jObjError.getString("error"));
                }
            } catch (Exception e) {
                Constant.showToastMessage(AdminAgentActivity.this, getResources().getString(R.string.something_wrong));
            }
        }

    }

    @Override
    public void onFailure(String message) {
        Constant.showToastMessage(AdminAgentActivity.this, message);
    }

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.PC_REQUEST_CODE) {
            if (resultCode == Constant.PC_REQUEST_CODE && null != data && data.hasExtra(Constant.INPUT_TICKET_CREATE)) {
                createTicketResponse=data.getParcelableExtra(Constant.INPUT_TICKET_CREATE);
                setChatInputs(getResources().getString(R.string.feedback_created), false);
            }

        }
    }
}
