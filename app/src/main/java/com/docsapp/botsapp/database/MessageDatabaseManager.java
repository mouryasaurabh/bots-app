package com.docsapp.botsapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

        public void insert(String message, String desc) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(MessageDatabaseHelper.MESSAGE, message);
                contentValue.put(MessageDatabaseHelper.SENDER, desc);
                if(database.isOpen())
                        database.insert(MessageDatabaseHelper.TABLE_NAME, null, contentValue);
                else{
                        database = dbHelper.getWritableDatabase();
                        database.insert(MessageDatabaseHelper.TABLE_NAME, null, contentValue);
                        database.close();
                }
        }

        public ArrayList<MessageList> fetch() {
                String[] columns = new String[] { MessageDatabaseHelper._ID, MessageDatabaseHelper.MESSAGE, MessageDatabaseHelper.SENDER };
                Cursor cursor = database.query(MessageDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
                ArrayList<MessageList>messageLists=new ArrayList<>();
                if (cursor.moveToFirst()) {
                        //Loop through the table rows
                        do {
                                MessageList messageListObject = new MessageList();
                                messageListObject.setMessage(cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.MESSAGE)));
                                messageListObject.setSender(cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.SENDER)));
                                messageLists.add(messageListObject);
                        } while (cursor.moveToNext());
                }
                return messageLists;
        }

        public int update(long _id, String message, String desc) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MessageDatabaseHelper.MESSAGE, message);
                contentValues.put(MessageDatabaseHelper.SENDER, desc);
                int i = database.update(MessageDatabaseHelper.TABLE_NAME, contentValues, MessageDatabaseHelper._ID + " = " + _id, null);
                return i;
        }

        public void delete(long _id) {
                database.delete(MessageDatabaseHelper.TABLE_NAME, MessageDatabaseHelper._ID + "=" + _id, null);
        }

}
