package com.docsapp.botsapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.docsapp.botsapp.activity.MainActivity;
import com.docsapp.botsapp.model.MessageList;

import java.util.ArrayList;

public class MessageDatabaseManager {

    private MessageDatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public MessageDatabaseManager(Context c) {
        context = c;
    }

    public MessageDatabaseManager open() throws SQLException {
        dbHelper = new MessageDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if(!database.isDbLockedByCurrentThread()){
            dbHelper.close();
        }
    }

    public long insert(String message, String desc, int syncState) {
        long id;
        ContentValues contentValue = new ContentValues();
        contentValue.put(MessageDatabaseHelper.MESSAGE, message);
        contentValue.put(MessageDatabaseHelper.SENDER, desc);
        contentValue.put(MessageDatabaseHelper.IS_SYNCED, syncState);
        if(database.isOpen())
            id= database.insert(MessageDatabaseHelper.TABLE_NAME, null, contentValue);
        else{
            database = dbHelper.getWritableDatabase();
            id= database.insert(MessageDatabaseHelper.TABLE_NAME, null, contentValue);
            database.close();
        }
        return id;
    }


    public ArrayList<MessageList> fetch() {
        String[] columns = new String[] { MessageDatabaseHelper._ID, MessageDatabaseHelper.MESSAGE, MessageDatabaseHelper.SENDER,  MessageDatabaseHelper.IS_SYNCED  };
        Cursor cursor = database.query(MessageDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        ArrayList<MessageList>messageLists=new ArrayList<>();
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                MessageList messageListObject = new MessageList();
                messageListObject.setMessageId(cursor.getLong(cursor.getColumnIndex(MessageDatabaseHelper._ID)));
                messageListObject.setMessage(cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.MESSAGE)));
                messageListObject.setSender(cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.SENDER)));
                messageListObject.setSyncState(cursor.getInt(cursor.getColumnIndex(MessageDatabaseHelper.IS_SYNCED)));
                messageLists.add(messageListObject);
            } while (cursor.moveToNext());
        }
        return messageLists;
    }

    public int updateMessage(long _id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessageDatabaseHelper.IS_SYNCED, MainActivity.SYNC_TRUE);
        int i = database.update(MessageDatabaseHelper.TABLE_NAME, contentValues, MessageDatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void deleteMessage(long _id) {
        database.delete(MessageDatabaseHelper.TABLE_NAME, MessageDatabaseHelper._ID + "=" + _id, null);
    }

}
