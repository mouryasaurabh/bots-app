package com.docsapp.botsapp.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.docsapp.botsapp.R;
import com.docsapp.botsapp.database.MessageDatabaseManager;
import com.docsapp.botsapp.listener.ConnectionListener;
import com.docsapp.botsapp.model.MessageList;
import com.docsapp.botsapp.model.MessageResponseModel;
import com.docsapp.botsapp.model.ResponseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConnectionListener.ConnectivityReceiverListener {
    public static final String ME="me";
    public static final int SYNC_TRUE=1;
    public static final int SYNC_FALSE=0;
    public static final String CONNECTIVITY_CHANGE="android.net.conn.CONNECTIVITY_CHANGE";

    private RequestQueue mRequestQueue;
    private EditText mMessageEt;
    private TextView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private MessageAdapter mMessageAdapter;
    private MessageDatabaseManager mMessageDatabaseManager;
    private ArrayList<MessageList>mUnsyncMessages=new ArrayList<>();
    private ConnectionListener connectionListener=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.chatbot));
        initDB();

        mMessageEt=findViewById(R.id.message_box);
        mSendButton=findViewById(R.id.send_button);
        mMessageRecyclerView=findViewById(R.id.recycler_view);
        mMessageEt.addTextChangedListener(textWatcher);

        initializeRV();
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClick();
            }
        });
        mRequestQueue = Volley.newRequestQueue(this);
    }

    private void initDB() {
        mMessageDatabaseManager = new MessageDatabaseManager(this);
        mMessageDatabaseManager.open();
    }

    /**
     * The textwatcher controls the visibility of the send button
     */
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s)){
                mSendButton.setVisibility(View.VISIBLE);
            }else{
                mSendButton.setVisibility(View.INVISIBLE);
            }

        }
    };

    /**
     * Initialize the recycler view and add unsent messages to queue for retry
     */
    private void initializeRV() {
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessageRecyclerView.setLayoutManager(llm);
        ArrayList<MessageList> messageLists=mMessageDatabaseManager.fetch();
        for(MessageList messageListObject:messageLists){
            if(messageListObject.getSyncState()==SYNC_FALSE){
                mUnsyncMessages.add(messageListObject);
                pushMessageToServer(messageListObject);
            }
        }
        mMessageAdapter=new MessageAdapter(this,messageLists, new MessageAdapter.onMessageItemClick() {
            @Override
            public void onItemClick(MessageList model) {

            }
        });
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.setNestedScrollingEnabled(false);
        if(mMessageAdapter.getItemCount()>0)
            mMessageRecyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount()-1);
    }

    private void onSendButtonClick() {
        String msg=mMessageEt.getText().toString();
        if(!TextUtils.isEmpty(msg)){
            mMessageEt.setText("");
            hideKeyboard();
            MessageList messageList=new MessageList();
            messageList.setSender(ME);
            messageList.setMessage(msg);
            long id=updateViewAndTable(true,messageList);
            messageList.setMessageId(id);
            mUnsyncMessages.add(messageList);
            pushMessageToServer(messageList);
        }
    }


    private void hideKeyboard( ) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageEt.getWindowToken(), 0);
    }

    /**
     * This method updates the view after recieving a message and clear the pending messages object from queue
     * in case of sending it just pushes data to table
     */
    private long updateViewAndTable(boolean isFromSend,MessageList msgList){
        updateView(msgList);
        final String message = msgList.getMessage();
        final String sender = msgList.getSender();
        final int syncState = msgList.getSyncState();
        if(!isFromSend){
            MessageList messageListObject=mUnsyncMessages.get(0);
            mUnsyncMessages.remove(0);
            mMessageDatabaseManager.updateMessage(messageListObject.getMessageId());

        }
        return mMessageDatabaseManager.insert(message, sender,syncState);
    }
    /**
     * Update recycler view on response
     */
    private void updateView(MessageList messageListObject){
        if(mMessageAdapter!=null){
            mMessageAdapter.updateData(messageListObject);
            if(mMessageAdapter.getItemCount()>0)
            mMessageRecyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount()-1);
        }

    }
    /**
     * This message is to send messages to server
     */
    private void pushMessageToServer(MessageList messageList){
        String url=createMsgUrl(messageList.getMessage());
        if(ConnectionListener.isConnected(this)){
            makeAPICall(url);
        }else{
            Toast.makeText(this, getString(R.string.please_connect), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Creates url for api from message
     */
    private String createMsgUrl(String msg){
        try {
            return "https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message="+
                    URLEncoder.encode(msg, "UTF-8")+"&chatBotID=63906&externalID=chirag1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * Register receiver and interface
     */
    @Override
    protected void onStart() {
        super.onStart();
        connectionListener=new ConnectionListener();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_CHANGE);
        intentFilter.setPriority(100);
        registerReceiver(connectionListener,intentFilter);

        ConnectionListener.initListener(this);
    }

    /**
     * Unregister receiver and interface
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(connectionListener!=null)
            unregisterReceiver(connectionListener);

        ConnectionListener.initListener(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMessageDatabaseManager.close();
    }

    /**
     * Sending message on a separate thread
     */
    private void makeAPICall(final String url){

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {

                                try {
//                                    System.out.println("xxxxx Response: "+jsonObject.toString());
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
                                    messageList.setSyncState(1);
                                    runOnUiThread(new Runnable(){
                                        @Override
                                        public void run(){
                                            updateViewAndTable(false,messageList);
                                        }
                                    });
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

    /**
     * This is interface method which notifies whenever internet is turned on and off.
     * In case of turnoff, we show a toast to connect to internet
     * when it is turned on the pending requests are sent to server
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            Toast.makeText(this, getString(R.string.please_connect), Toast.LENGTH_SHORT).show();
        }else{
            if(mUnsyncMessages.size()!=0){
                for(MessageList messageListObject:mUnsyncMessages){
                    pushMessageToServer(messageListObject);
                }
            }
        }
    }
}
