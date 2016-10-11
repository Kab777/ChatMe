package com.junyu.moneymanager;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Wanjie on 2016-10-09.
 */

public class IncomeActivity extends AppCompatActivity{
    @BindView(R.id.incomeSubmit) Button incomeSubmit;
    @BindView(R.id.incomeAmount) EditText incomeAmount;
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference group = database.getReference(MMConstant.GROUPS);
    public static final DatabaseReference transaction = database.getReference(MMConstant.TRANSACTIONS);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);
        final String id = MMUserPreference.getUserId(this);
        incomeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference myGroup = group.child(id);
                final DatabaseReference income = transaction.child(id).child(MMConstant.INCOME);
                final DatabaseReference balance = myGroup.child(MMConstant.BALANCE);

                DatabaseReference incomeChild = income.push();
                HashMap<String, String> post2 = new HashMap<>();
                post2.put("amount", incomeAmount.getText().toString());
                income.child(incomeChild.getKey()).setValue(post2);

                balance.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        balance.setValue(Integer.parseInt(incomeAmount.getText().toString()) + Integer.parseInt(dataSnapshot.getValue().toString()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });




    }


}
