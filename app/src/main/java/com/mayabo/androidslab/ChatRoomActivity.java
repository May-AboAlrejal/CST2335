package com.mayabo.androidslab;

// resource
//<a target="_blank" href="https://icons8.com/icons/set/portrait-mode-female">Portrait Mode Female</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
//<a target="_blank" href="https://icons8.com/icons/set/gender-neutral-user">Customer</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private MyListAdapter myAdapter;
    private EditText textMessage;
    private ArrayList<Message> messageList = new ArrayList();
    private ChatDataBaseHelper dbOpener = new ChatDataBaseHelper(this);
    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        db = dbOpener.getWritableDatabase();

        ListView messageListView = (ListView) findViewById(R.id.messageListView);

        printCursor();

        messageListView.setAdapter( myAdapter = new MyListAdapter() );

        Button sendButton = findViewById(R.id.sendButton);
        if(sendButton != null){
            sendButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                handleMessage(textMessage.getText().toString(),true, false);
            });
        }

        Button receiveButton = findViewById(R.id.receiveButton);
        if(receiveButton != null){
            receiveButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                handleMessage(textMessage.getText().toString(), false, true);
            });
        }
    }

    private void printCursor(){
        String[] colNames = {ChatDataBaseHelper.COL_ID, ChatDataBaseHelper.COL_MESSAGE,
                ChatDataBaseHelper.COL_SENT, ChatDataBaseHelper.COL_RECEIVED};

        Cursor c = db.query(false, ChatDataBaseHelper.TABLE_NAME, colNames, null, null, null, null, null, null);

        int idIndex = c.getColumnIndex(ChatDataBaseHelper.COL_ID);
        int messageIndex = c.getColumnIndex(ChatDataBaseHelper.COL_MESSAGE);
        int sentIndex = c.getColumnIndex(ChatDataBaseHelper.COL_SENT);
        int receivedIndex = c.getColumnIndex(ChatDataBaseHelper.COL_RECEIVED);


        Log.e("DBNumber ", "The database version number: " + ChatDataBaseHelper.VERSION_NUM);
        Log.e("RowCount ", "The number of rows in the cursor: " + c.getCount());
        Log.e("ColumnCount ", "The number of columns in the cursor: " + c.getColumnCount());
        Log.e("ColumnNames ", "The name of the columns in the cursor: ");
        for(int j = 0; j < c.getColumnCount(); j++){
            Log.e("ColumnNames ", "Column[" +j + "]: " + c.getColumnName(j));
        }
        Log.e("Results ", "Each row of results in the cursor: ");


        while(c.moveToNext()) {
            long id = c.getLong(idIndex);
            String message = c.getString(messageIndex);
            boolean sent = c.getInt(sentIndex)==1 ? true : false;
            boolean received = c.getInt(receivedIndex)==1 ? true : false;

            messageList.add(new Message(id, message, sent, received));
        }

        Log.e("Results ", messageList.toString());
    }


    private void handleMessage(String message, boolean isSent, boolean isReceived){
        ContentValues newRowValues = new ContentValues();

        newRowValues.put(ChatDataBaseHelper.COL_MESSAGE, message);
        newRowValues.put(ChatDataBaseHelper.COL_SENT, isSent? 1:0);
        newRowValues.put(ChatDataBaseHelper.COL_RECEIVED, isReceived? 1:0);

        long id = db.insert(ChatDataBaseHelper.TABLE_NAME, null, newRowValues);

        messageList.add(new Message(id, message, isSent, isReceived));

        myAdapter.notifyDataSetChanged();
        textMessage.setText("");
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Message getItem(int i) {

            return messageList.get(i);
        }

        @Override
        public long getItemId(int i) {

            return getItem(i).getId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View thisRow = view;

            boolean type = ((Message) getItem(i)).isSent();
            if(type) {
                thisRow = getLayoutInflater().inflate(R.layout.message_send_layout, null);
            } else {
                thisRow = getLayoutInflater().inflate(R.layout.message_receive_layout, null);
            }

            TextView messageText = thisRow.findViewById(R.id.messageText);
            Message test = (Message)getItem(i);

            messageText.setText(((Message) getItem(i)).getMessage());

            return thisRow;
        }
    }
}
