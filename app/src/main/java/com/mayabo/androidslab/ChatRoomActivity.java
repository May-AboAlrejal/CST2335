package com.mayabo.androidslab;

// resource
//<a target="_blank" href="https://icons8.com/icons/set/portrait-mode-female">Portrait Mode Female</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
//<a target="_blank" href="https://icons8.com/icons/set/gender-neutral-user">Customer</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>

import android.content.Intent;
import android.os.Bundle;
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

    private BaseAdapter myAdapter;
    private EditText textMessage;
    ArrayList<Message> messageList = new ArrayList<Message>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        ListView messageListView = (ListView) findViewById(R.id.messageListView);
        messageListView.setAdapter( myAdapter = new MyListAdapter() );


        Button sendButton = findViewById(R.id.sendButton);
        if(sendButton != null){
            sendButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                messageList.add(new Message(textMessage.getText().toString(), "SEND"));
                myAdapter.notifyDataSetChanged();
                textMessage.setText("");
            });
        }

        Button receiveButton = findViewById(R.id.receiveButton);
        if(receiveButton != null){
            receiveButton.setOnClickListener(clk -> {
                textMessage = findViewById(R.id.message);
                messageList.add(new Message(textMessage.getText().toString(), "RECEIVE"));
                myAdapter.notifyDataSetChanged();
                textMessage.setText("");
            });
        }
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int i) {
            return messageList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View thisRow = view;

            String type = ((Message) getItem(i)).getType();
            if (view == null || type.equals("SEND")) {
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
