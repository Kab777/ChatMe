package com.junyu.IMBudget.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junyu.IMBudget.ChatMeApplication;
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;
import com.junyu.IMBudget.adapter.MsgChatAdapter;
import com.junyu.IMBudget.api.AliceChatService;
import com.junyu.IMBudget.model.AliceMsg;
import com.junyu.IMBudget.model.MessageChatModel;
import com.junyu.IMBudget.utils.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.R.attr.bitmap;
import static com.junyu.IMBudget.MMConstant.CHAT_BOT_ID;
import static com.junyu.IMBudget.MMConstant.MSG_FORMAT;
import static com.junyu.IMBudget.MMConstant.MUNDO_ID;
import static com.junyu.IMBudget.MMConstant.MUNDO_NAME;
import static com.junyu.IMBudget.MMConstant.CHAT_ID;
import static com.junyu.IMBudget.MMConstant.CONTENT;
import static com.junyu.IMBudget.MMConstant.MESSAGES;
import static com.junyu.IMBudget.MMConstant.NAME;
import static com.junyu.IMBudget.MMConstant.TIMESTAMP;
import static com.junyu.IMBudget.MMConstant.USER_ID;
import static com.junyu.IMBudget.adapter.MsgChatAdapter.RECIPIENT;
import static com.junyu.IMBudget.adapter.MsgChatAdapter.SENDER;

/**
 * Created by Junyu on 10/17/2016.
 */

public class ActivityFriendChat extends AppCompatActivity {
    @BindView(R.id.chatRv) RecyclerView chatRv;
    @BindView(R.id.userMsg) EditText userMsg;
    @BindView(R.id.sendMsg) ImageView sendMsg;

    @Inject
    Retrofit retrofit;

    private String chatId;
    private String userId;
    private Boolean chatWhAlice = false;
    private DatabaseReference fireDb;
    private DatabaseReference chatDb;
    private MsgChatAdapter msgChatAdapter;

    @OnClick(R.id.sendMsg)
    public void sendUserMsg() {
        String msg = userMsg.getText().toString().trim();
        if (msg.length() == 0) {
            userMsg.setText("");
            return;
        }
        DatabaseReference friendDb = fireDb.child(MESSAGES).child(chatId);
        String newMsgId = friendDb.push().getKey();
        Map<String, String> msgInfo = new HashMap<>();
        msgInfo.put(USER_ID, userId);
        msgInfo.put(NAME, MMUserPreference.getUserName(this));
        msgInfo.put(CONTENT, msg);
        msgInfo.put(TIMESTAMP, Time.getCurTimeAsString());
        friendDb.child(newMsgId).setValue(msgInfo);
        userMsg.setText("");
        if (chatWhAlice) {
            //ask Alice to respond here

            AliceChatService service = retrofit.create(AliceChatService.class);
            Observable<AliceMsg> call = service.getMsg(CHAT_BOT_ID, msg, userId, MSG_FORMAT);
            call
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AliceMsg>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e.getMessage());
                        }

                        @Override
                        public void onNext(AliceMsg aliceMsg) {
                            DatabaseReference friendDb = fireDb.child(MESSAGES).child(chatId);
                            String newMsgId = friendDb.push().getKey();
                            Map<String, String> msgInfo = new HashMap<>();
                            msgInfo.put(USER_ID, MUNDO_ID);
                            msgInfo.put(NAME, MUNDO_NAME);
                            msgInfo.put(CONTENT, aliceMsg.getBotsay());
                            msgInfo.put(TIMESTAMP, Time.getCurTimeAsString());
                            friendDb.child(newMsgId).setValue(msgInfo);
                        }
                    });

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);
        ButterKnife.bind(this);
        ChatMeApplication.getNetComponent(this).inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fireDb = FirebaseDatabase.getInstance().getReference();
        Intent incomingIntent = getIntent();
        getSupportActionBar().setTitle("  " + incomingIntent.getStringExtra(NAME));
        getSupportActionBar().setIcon(new BitmapDrawable(getResources(), createCircleBitmap((Bitmap) incomingIntent.getParcelableExtra("image"))));
        chatId = incomingIntent.getStringExtra(CHAT_ID);
        String friendUserId = incomingIntent.getStringExtra(USER_ID);


        if (friendUserId.equals(MUNDO_ID)) {
            chatWhAlice = true;
        } else {
            chatWhAlice = false;
        }
        userId = MMUserPreference.getUserId(this);
        chatRv.setLayoutManager(new LinearLayoutManager(this));
        chatRv.setHasFixedSize(true);
        chatRv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                chatRv.scrollToPosition(msgChatAdapter.getItemCount() - 1);
            }
        });
        msgChatAdapter = new MsgChatAdapter(this, new ArrayList<MessageChatModel>());
        chatRv.setAdapter(msgChatAdapter);
        chatDb = fireDb.child(MESSAGES).child(chatId);


        chatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Timber.v(dataSnapshot.toString());
//                Timber.v(dataSnapshot.getValue().toString());

                MessageChatModel newMessage = dataSnapshot.getValue(MessageChatModel.class);
                Timber.v(newMessage.toString());
                if (newMessage.getUserId().equals(userId)) {
                    newMessage.setRecipientOrSenderStatus(SENDER);
                } else {
                    newMessage.setRecipientOrSenderStatus(RECIPIENT);
                }
                msgChatAdapter.addNewMsg(newMessage);
                chatRv.scrollToPosition(msgChatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public Bitmap createCircleBitmap(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }
}
