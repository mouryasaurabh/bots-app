package com.docsapp.botsapp.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.docsapp.botsapp.R;
import com.docsapp.botsapp.model.MessageList;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewholder>{
    private ArrayList<MessageList> messageList;
    private Context mContext;
    private onMessageItemClick itemClickListener;

    public void updateData(MessageList messageListObject) {
        messageList.add(messageListObject);
        notifyDataSetChanged();
    }

    public MessageAdapter(Context context, ArrayList<MessageList> messageList, onMessageItemClick itemClickListener){
        this.messageList=messageList;
        mContext=context;
        this.itemClickListener=itemClickListener;
    }
    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        MessageViewholder viewholder = new MessageViewholder(view);
        return viewholder ;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewholder holder, int position) {
        if(messageList.get(position).getSender().equals(MainActivity.ME)){
            holder.mItemView.setBackgroundResource(R.drawable.outgoing_message);
        }else{
            holder.mItemView.setBackgroundResource(R.drawable.incoming_message);
        }
        holder.mMessageText.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        if(messageList.size()==0){
            return 0;
        }else{
            return messageList.size();
        }
    }

    public class MessageViewholder extends RecyclerView.ViewHolder{
        private TextView mMessageText;
        private View mItemView;
        public MessageViewholder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.message_txt);
            mItemView=itemView;


        }
        private void setOnclickViewItem(final MessageList messageList) {
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(messageList);
                }
            });
        }

    }

    public interface onMessageItemClick{
        void onItemClick(MessageList model);
    }
}
