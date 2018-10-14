package com.docsapp.botsapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
                dbHelper.close();
        }

        public void insert(String message, String desc) {
                ContentValues contentValue = new ContentValues();
                contentValue.put(MessageDatabaseHelper.MESSAGE, message);
                contentValue.put(MessageDatabaseHelper.SENDER, desc);
                database.insert(MessageDatabaseHelper.TABLE_NAME, null, contentValue);
        }

        public Cursor fetch() {
                String[] columns = new String[] { MessageDatabaseHelper._ID, MessageDatabaseHelper.MESSAGE, MessageDatabaseHelper.SENDER };
                Cursor cursor = database.query(MessageDatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
                if (cursor != null) {
                        cursor.moveToFirst();
                }
                return cursor;
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
