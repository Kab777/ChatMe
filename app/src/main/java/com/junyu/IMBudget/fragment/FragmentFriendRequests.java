package com.junyu.IMBudget.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.junyu.IMBudget.MMConstant;
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;
import com.junyu.IMBudget.model.FriendRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.junyu.IMBudget.MMConstant.ACCEPTED;
import static com.junyu.IMBudget.MMConstant.CHAT_ID;
import static com.junyu.IMBudget.MMConstant.FRIENDS;
import static com.junyu.IMBudget.MMConstant.FRIEND_REQUESTS;
import static com.junyu.IMBudget.MMConstant.IMAGE_URL;
import static com.junyu.IMBudget.MMConstant.NAME;
import static com.junyu.IMBudget.MMConstant.ONLINE;
import static com.junyu.IMBudget.MMConstant.PROFILE_IMAGE;
import static com.junyu.IMBudget.MMConstant.USER_ID;


/**
 * Created by Junyu on 10/17/2016.
 */

public class FragmentFriendRequests extends Fragment {
    @BindView(R.id.friendRequests) RecyclerView rvFriendRequests;

    private DatabaseReference fireDb;
    private String userId;
    private FriendRequestsAdapter friendRequestsAdapter;
    private ArrayList<FriendRequest> friendRequests = new ArrayList<>();

    public FragmentFriendRequests() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireDb = FirebaseDatabase.getInstance().getReference();
        userId = MMUserPreference.getUserId(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, view);
        friendRequestsAdapter = new FriendRequestsAdapter(getContext(), friendRequests);
        rvFriendRequests.setAdapter(friendRequestsAdapter);
        rvFriendRequests.setHasFixedSize(true);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference friendRequestDb = fireDb.child(MMConstant.USERS).child(userId).child(FRIEND_REQUESTS);
        // change it to child listener later to make it more efficient
        friendRequestDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendRequests.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    FriendRequest newFriend = child.getValue(FriendRequest.class);
                    newFriend.setSenderId(child.getKey());
                    friendRequests.add(newFriend);

                }
                friendRequestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {
        private List<FriendRequest> friendRequests;
        private Context context;

        public FriendRequestsAdapter(Context context, List<FriendRequest> friendRequests) {
            this.friendRequests = friendRequests;
            this.context = context;
        }

        private Context getContext() {
            return context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.userImage) ImageView userImage;
            @BindView(R.id.userName) TextView userName;
            @BindView(R.id.acceptRequest) TextView acceptRequest;
            @BindView(R.id.added) TextView added;
            @BindView(R.id.requestMsg) TextView requestMsg;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public FriendRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View friendView = inflater.inflate(R.layout.item_friend_request_row, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(friendView);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final FriendRequest friendRequest = friendRequests.get(position);
            final String userName = friendRequest.getName();
            final String chatId = friendRequest.getChatId();
            final String friendId = friendRequest.getSenderId();
            final String msg = friendRequest.getRequestMsg();
            final String imgUrl = friendRequest.getImgUrl();
            Boolean accepted = friendRequest.getAccepted();
            holder.userName.setText(userName);
            if (accepted) {
                holder.acceptRequest.setVisibility(View.GONE);
                holder.added.setVisibility(View.VISIBLE);
            } else {
                holder.acceptRequest.setVisibility(View.VISIBLE);
                holder.added.setVisibility(View.GONE);
            }
            if (imgUrl != null && !imgUrl.equals("")) {
                Picasso.with(context).load(imgUrl).placeholder(R.drawable.ic_person_black_24dp).into(holder.userImage);
            } else {
                Picasso.with(context).load(R.drawable.ic_person_black_24dp).into(holder.userImage);
            }
            holder.requestMsg.setText(msg);

            holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendAcceptRequest(friendId, chatId, imgUrl);
                }
            });
        }

        private void sendAcceptRequest(final String friendId, final String chatId, final String imgUrl) {
            String myName = MMUserPreference.getUserName(context);
            Map<String, Object> friendInfo = new HashMap<>();
            friendInfo.put(USER_ID, userId);
            friendInfo.put(CHAT_ID, chatId);
            friendInfo.put(NAME, myName);
            friendInfo.put(ONLINE, true);
            friendInfo.put(IMAGE_URL, MMUserPreference.getUserImg(context));

            //update friend's list
            DatabaseReference friendRef = fireDb.child(MMConstant.USERS).child(friendId);
            friendRef.child(FRIENDS).child(userId).setValue(friendInfo);
            DatabaseReference nameRef = friendRef.child(NAME);
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String friendName = dataSnapshot.getValue().toString();
                    updateMyInfo(friendName, friendId, chatId, imgUrl);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return friendRequests.size();
        }

        private void updateMyInfo(String friendName, String friendId, String chatId, String imgUrl) {
            DatabaseReference meRef = fireDb.child(MMConstant.USERS).child(userId);
            //set Accepted
            meRef.child(FRIEND_REQUESTS).child(friendId).child(ACCEPTED).setValue(true);

            Map<String, Object> myInfo = new HashMap<>();
            myInfo.put(USER_ID, friendId);
            myInfo.put(NAME, friendName);
            myInfo.put(CHAT_ID, chatId);
            myInfo.put(ONLINE, true);
            myInfo.put(IMAGE_URL, imgUrl);

            //update my friends list
            meRef.child(FRIENDS).child(friendId).setValue(myInfo);

        }
    }

}
