package com.docsapp.botsapp;

import org.json.JSONArray;

public class ResponseModel {

    private int success;
    private String errorMessage;
    private MessageResponseModel message;
    private JSONArray data;


    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MessageResponseModel getMessage() {
        return message;
    }

    public void setMessage(MessageResponseModel message) {
        this.message = message;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }


}
