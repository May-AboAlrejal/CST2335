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

    public static final String MESSAGE = "MESSAGE";
    public static final String MESSAGE_TYPE = "TYPE";
    public static final String MESSAGE_ID = "ID";
    public static final String MESSAGE_POSITION = "POSITION";
    public static final int EMPTY_ACTIVITY = 345;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        db = dbOpener.getWritableDatabase();

        ListView messageListView = findViewById(R.id.messageListView);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;


        printCursor();

        messageListView.setAdapter(myAdapter = new MyListAdapter());

        Button sendButton = findViewById(R.id.sendButton);
        if (sendButton != null) {
            sendButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                handleMessage(textMessage.getText().toString(), true, false);
            });
        }

        Button receiveButton = findViewById(R.id.receiveButton);
        if (receiveButton != null) {
            receiveButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                handleMessage(textMessage.getText().toString(), false, true);
            });
        }

        messageListView.setOnItemClickListener((list, item, position, id) -> {

            String typeMessage = messageList.get(position).isSent() ? "Send" : "Received";
            Bundle dataToPass = new Bundle();
            dataToPass.putString(MESSAGE, messageList.get(position).getMessage());
            dataToPass.putLong(MESSAGE_ID, messageList.get(position).getId());
            dataToPass.putInt(MESSAGE_POSITION, position);
            dataToPass.putString(MESSAGE_TYPE, typeMessage);

            if (isTablet) {
                FragmentMessageDetails dFragment = new FragmentMessageDetails();
                dFragment.setArguments(dataToPass);
                dFragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentLocation, dFragment)
                        .addToBackStack("AnyName")
                        .commit();
            } else {
                Intent nextActivity = new Intent(ChatRoomActivity.this, MessageDetailsActivity.class);
                nextActivity.putExtras(dataToPass);
                startActivityForResult(nextActivity, EMPTY_ACTIVITY);
            }
        });

    }

    private void printCursor() {
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
        for (int j = 0; j < c.getColumnCount(); j++) {
            Log.e("ColumnNames ", "Column[" + j + "]: " + c.getColumnName(j));
        }
        Log.e("Results ", "Each row of results in the cursor: ");


        while (c.moveToNext()) {
            long id = c.getLong(idIndex);
            String message = c.getString(messageIndex);
            boolean sent = c.getInt(sentIndex) == 1 ? true : false;
            boolean received = c.getInt(receivedIndex) == 1 ? true : false;

            messageList.add(new Message(id, message, sent, received));
        }

        Log.e("Results ", messageList.toString());
    }


    private void handleMessage(String message, boolean isSent, boolean isReceived) {
        ContentValues newRowValues = new ContentValues();

        newRowValues.put(ChatDataBaseHelper.COL_MESSAGE, message);
        newRowValues.put(ChatDataBaseHelper.COL_SENT, isSent ? 1 : 0);
        newRowValues.put(ChatDataBaseHelper.COL_RECEIVED, isReceived ? 1 : 0);

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
            if (type) {
                thisRow = getLayoutInflater().inflate(R.layout.message_send_layout, null);
            } else {
                thisRow = getLayoutInflater().inflate(R.layout.message_receive_layout, null);
            }

            TextView messageText = thisRow.findViewById(R.id.messageText);
            Message test = (Message) getItem(i);

            messageText.setText(((Message) getItem(i)).getMessage());

            return thisRow;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMPTY_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra(MESSAGE_ID, 0);
                int position = data.getIntExtra(MESSAGE_POSITION, 0);
                deleteMessageId(id, position);
            }
        }
    }

    public void deleteMessageId(int id, int position) {
        Log.e("Delete this message:", " id = " + id + ", and position = " + position);
        int delete = db.delete(ChatDataBaseHelper.TABLE_NAME, ChatDataBaseHelper.COL_ID + "=?", new String[] {Long.toString(id)});
        Log.e("Number of rows deleted:", " deleted rows = " + delete);
        messageList.remove(position);
        myAdapter.notifyDataSetChanged();
    }
}
