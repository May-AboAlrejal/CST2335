package com.mayabo.androidslab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentMessageDetails extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private int id;
    private int position;


    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        id = (int) dataFromActivity.getLong(ChatRoomActivity.MESSAGE_ID);
        position= dataFromActivity.getInt(ChatRoomActivity.MESSAGE_POSITION);

        View result = inflater.inflate(R.layout.activity_message_info, container, false);

        TextView type = result.findViewById(R.id.messageType);
        type.setText(dataFromActivity.getString(ChatRoomActivity.MESSAGE_TYPE));
        TextView idView = result.findViewById(R.id.messageId);
        idView.setText(String.valueOf(id));
        TextView message = result.findViewById(R.id.messageText);
        message.setText(dataFromActivity.getString(ChatRoomActivity.MESSAGE));
        TextView positionView = result.findViewById(R.id.messagePosition);
        positionView.setText(String.valueOf(position));

        Button deleteButton = (Button) result.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(clik -> {
            if (isTablet) {
                ChatRoomActivity parent = (ChatRoomActivity) getActivity();
                parent.deleteMessageId(id, position);
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            } else {
                MessageDetailsActivity parent = (MessageDetailsActivity) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra(ChatRoomActivity.MESSAGE_ID, id);
                backToFragmentExample.putExtra(ChatRoomActivity.MESSAGE_POSITION, position);
                parent.setResult(Activity.RESULT_OK, backToFragmentExample);
                parent.finish();
            }
        });


        return result;
    }
}
