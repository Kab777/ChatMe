package com.junyu.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.junyu.moneymanager.dialog.AddTransactionDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.junyu.moneymanager.MMConstant.GROUP_ID;

/**
 * Created by Wanjie on 2016-10-10.
 */

public class TransactionActivity extends AppCompatActivity {
    @BindView(R.id.addTransaction) Button addTransaction;
    private FirebaseDatabase database;
    private DatabaseReference group;
    private DatabaseReference transaction;
    private ArrayList<String> recordArray;

    @OnClick(R.id.addTransaction)
    public void openTransactionDialog() {
        AddTransactionDialog newTransaction = new AddTransactionDialog(this, groupId);
        newTransaction.show();
    }

    private String groupId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        group = database.getReference(MMConstant.GROUPS);
        transaction = database.getReference(MMConstant.TRANSACTIONS);

        ListView lv = (ListView) findViewById(R.id.transactionRecord);
        recordArray = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, recordArray);

        lv.setAdapter(arrayAdapter);
        Intent intent = getIntent();
        groupId = intent.getStringExtra(GROUP_ID);


        final DatabaseReference myGroup = group.child(groupId);
        final DatabaseReference record = transaction.child(groupId);
        record.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recordArray.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(MMConstant.EXPENSE)) {
                        for(DataSnapshot expenseRecord : child.getChildren()) {
                            String temp = "Expense amount is " + expenseRecord.child(MMConstant.AMOUNT).getValue() + " on " + expenseRecord.child(MMConstant.TAG).getValue() + " by " + expenseRecord.child(MMConstant.USER_NAME).getValue();
                            recordArray.add(temp);
                        }
                    } else {
                        for(DataSnapshot incomeRecord : child.getChildren()) {
                            String temp = "Income amount is " + incomeRecord.child(MMConstant.AMOUNT).getValue() + " by " + incomeRecord.child(MMConstant.USER_NAME).getValue();
                            recordArray.add(temp);
                        }
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
