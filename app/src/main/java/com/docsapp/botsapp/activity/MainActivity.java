package com.docsapp.botsapp.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.docsapp.botsapp.R;
import com.docsapp.botsapp.model.MessageList;
import com.docsapp.botsapp.model.MessageResponseModel;
import com.docsapp.botsapp.model.ResponseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    /* private  NetworkTask mNetworkTask;*/
    private EditText mMessageEt;
    private TextView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQueue = Volley.newRequestQueue(this);
        mMessageEt=findViewById(R.id.message_box);
        mSendButton=findViewById(R.id.send_button);
        mMessageRecyclerView=findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessageRecyclerView.setLayoutManager(llm);
        ArrayList<MessageList> messageLists=new ArrayList();
        initializeRV(messageLists);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=mMessageEt.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    pushToTable(msg);
                    mMessageEt.setText("");
                    hideKeyboard();
                }
            }
        });
    }


    private void initializeRV(ArrayList<MessageList> messageLists) {
        mMessageAdapter=new MessageAdapter(this,messageLists, new MessageAdapter.onMessageItemClick() {
            @Override
            public void onItemClick(MessageList model) {

            }
        });
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setNestedScrollingEnabled(false);
    }

    private void updateView(MessageList messageListObject){
        if(mMessageAdapter!=null){
            mMessageAdapter.updateData(messageListObject);
        }

    }

    private void hideKeyboard( ) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageEt.getWindowToken(), 0);
    }



    private String createMsgUrl(String msg){
        try {
            return "https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message="+
                    URLEncoder.encode(msg, "UTF-8")+"&chatBotID=63906&externalID=chirag1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

    private void pushToTable(String msg){
        MessageList messageList=new MessageList();
        messageList.setSender("me");
        messageList.setMessage(msg);
        updateView(messageList);
        pushMessageToServer(msg);

    }

    private void pushMessageToServer(String message){
        String url=createMsgUrl(message);

        makeAPICall(url);
//        mNetworkTask=new NetworkTask();
//        mNetworkTask.execute(url);
    }


   /* public class NetworkTask extends AsyncTask<String,Void,MessageList> {

        @Override
        protected MessageList doInBackground(String... strings) {
            String url = strings[0];

            System.out.println("xxxxx Request URL: "+url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            try {
                                System.out.println("xxxxx Response: "+jsonObject.toString());
                                JSONObject messageJsonObject = jsonObject.getJSONObject("message");

                                MessageResponseModel messageResponseModel=new MessageResponseModel();
                                messageResponseModel.setChatBotName( messageJsonObject.getString("chatBotName"));
                                messageResponseModel.setChatBotID(messageJsonObject.getInt("chatBotID"));
                                messageResponseModel.setMessage(messageJsonObject.getString("message"));
                                messageResponseModel.setEmotion(messageJsonObject.getString("emotion"));

                                ResponseModel responseModel=new ResponseModel();
                                responseModel.setMessage(messageResponseModel);
                                responseModel.setErrorMessage(jsonObject.getString("errorMessage"));
                                responseModel.setSuccess(jsonObject.getInt("success"));
                                responseModel.setData(jsonObject.getJSONArray("data"));

//                                updateTable();
                            }
                            catch(JSONException e) {
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
//                            Toast.makeText(this,getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                        }
                    });
            mRequestQueue.add(request);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(MessageList messageList) {
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void makeAPICall(final String url){

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {

                                try {
                                    System.out.println("xxxxx Response: "+jsonObject.toString());
                                    JSONObject messageJsonObject = jsonObject.getJSONObject("message");

                                    MessageResponseModel messageResponseModel=new MessageResponseModel();
                                    messageResponseModel.setChatBotName( messageJsonObject.getString("chatBotName"));
                                    messageResponseModel.setChatBotID(messageJsonObject.getInt("chatBotID"));
                                    messageResponseModel.setMessage(messageJsonObject.getString("message"));
                                    messageResponseModel.setEmotion(messageJsonObject.getString("emotion"));

                                    ResponseModel responseModel=new ResponseModel();
                                    responseModel.setMessage(messageResponseModel);
                                    responseModel.setErrorMessage(jsonObject.getString("errorMessage"));
                                    responseModel.setSuccess(jsonObject.getInt("success"));
                                    responseModel.setData(jsonObject.getJSONArray("data"));
                                    final MessageList messageList=new MessageList();
                                    messageList.setSender(messageResponseModel.getChatBotName());
                                    messageList.setMessage(messageResponseModel.getMessage());
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run(){
                                            updateView(messageList);


                                        }
                                    });

//                                updateTable();
                                }
                                catch(JSONException e) {
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
//                            Toast.makeText(this,getString(R.string.something_went_wrong),Toast.LENGTH_SHORT).show();
                            }
                        });
                mRequestQueue.add(request);
            }
        });
        thread.start();

    }



}
