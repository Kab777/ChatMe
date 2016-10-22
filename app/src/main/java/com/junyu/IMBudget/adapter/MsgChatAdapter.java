package com.junyu.IMBudget.adapter;

import android.content.Context;
import android.icu.text.DateIntervalInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junyu.IMBudget.R;
import com.junyu.IMBudget.model.MessageChatModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.junyu.IMBudget.utils.Time.formateDateFromString;

/**
 * Created by Junyu on 10/17/2016.
 */

public class MsgChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MessageChatModel> chatLists;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;

    private Context context;

    public MsgChatAdapter(Context context, List<MessageChatModel> msgs) {
        this.chatLists = msgs;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View v1 = inflater.inflate(R.layout.item_sender_layout, viewGroup, false);
                viewHolder = new SenderViewHolder(v1);
                break;
            case RECIPIENT:
                View v2 = inflater.inflate(R.layout.item_recipient_layout, viewGroup, false);
                viewHolder = new RecipientViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_sender_layout, viewGroup, false);
                viewHolder = new SenderViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case SENDER:
                SenderViewHolder viewHolderSender = (SenderViewHolder) viewHolder;
                MessageChatModel senderFireMessage = chatLists.get(position);
                TextView dateSend = viewHolderSender.senderDate;
                viewHolderSender.senderMsg.setText(senderFireMessage.getContent());
                if (showMsgDate(position)) {
                    String forMatedDate = formateDateFromString(senderFireMessage.getTimestamp());
                    dateSend.setText(forMatedDate);
                    dateSend.setVisibility(View.VISIBLE);
                } else {
                    dateSend.setVisibility(View.GONE);
                }
                break;
            case RECIPIENT:
                RecipientViewHolder viewHolderRecipient = (RecipientViewHolder) viewHolder;
                MessageChatModel recipientFireMessage = chatLists.get(position);
                TextView dateReceive = viewHolderRecipient.recipientDate;
                viewHolderRecipient.recipientMsg.setText(recipientFireMessage.getContent());
                if (showMsgDate(position)) {
                    String forMatedDate = formateDateFromString(recipientFireMessage.getTimestamp());
                    dateReceive.setText(forMatedDate);
                    dateReceive.setVisibility(View.VISIBLE);
                } else {
                    dateReceive.setVisibility(View.GONE);
                }
                break;
        }
    }

    private boolean showMsgDate(int position) {
        if (position == 0) {
            return true;
        } else if (position == chatLists.size()) {
            return false;
        }
        MessageChatModel msgPrev = chatLists.get(position - 1);
        MessageChatModel msgCur = chatLists.get(position);
        SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", java.util.Locale.getDefault());
        try {

            Date preDate = input.parse(msgPrev.getTimestamp());
            Date curDate = input.parse(msgCur.getTimestamp());


            // show date if interview is more than 15 mins
            if (curDate.getTime() - preDate.getTime() >= 15*60*1000) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatLists.get(position).getRecipientOrSenderStatus() == SENDER) {
            return SENDER;
        } else {
            return RECIPIENT;
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.senderMsg) TextView senderMsg;
        @BindView(R.id.senderDate) TextView senderDate;

        public SenderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class RecipientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipientMsg) TextView recipientMsg;
        @BindView(R.id.recipientDate) TextView recipientDate;

        public RecipientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addNewMsg(MessageChatModel newFireChatMessage) {

        /*add new message chat to list*/
        chatLists.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }
}
