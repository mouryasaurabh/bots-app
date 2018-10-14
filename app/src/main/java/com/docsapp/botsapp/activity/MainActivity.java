package com.docsapp.botsapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private  NetworkTask mNetworkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQueue = Volley.newRequestQueue(this);
        mNetworkTask=new NetworkTask();
        String url=createMsgUrl("hi there");
        mNetworkTask.execute(url);
        //test comment
    }


    public String createMsgUrl(String msg){
        try {
            return "https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message="+
                    URLEncoder.encode(msg, "UTF-8")+"&chatBotID=63906&externalID=chirag1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

    }


    public class NetworkTask extends AsyncTask<String,Void,Void> {



        @Override
        protected Void doInBackground(String... strings) {
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
                                pushTODB();
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
        protected void onPostExecute(Void o) {
        }
    }




}
