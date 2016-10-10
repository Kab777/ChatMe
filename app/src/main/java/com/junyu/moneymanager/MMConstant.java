package com.junyu.moneymanager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Junyu on 10/9/2016.
 */

public class MMConstant {
    public static final String USERS = "Users";
    public static final String USER_ID = "userId";
    public static final String GROUP_ID = "groupId";
    public static final String NAME = "name";
    public static final String PREFERENCE_NAME = "userInfo";

    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference group = database.getReference("Groups");
    public static final DatabaseReference transaction = database.getReference("Transactions");
}
