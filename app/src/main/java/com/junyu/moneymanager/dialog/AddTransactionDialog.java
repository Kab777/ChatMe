package com.junyu.moneymanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.junyu.moneymanager.MMConstant;
import com.junyu.moneymanager.MMUserPreference;
import com.junyu.moneymanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Wanjie on 2016-10-10.
 */

public class AddTransactionDialog extends Dialog {

    @BindView(R.id.submit) Button submit;
    @BindView(R.id.amount) EditText amount;
    @BindView(R.id.transactionSpinner) Spinner transactionSpinner;
    @BindView(R.id.tagSpinner) Spinner tagSpinner;
    private FirebaseDatabase database;
    private DatabaseReference group;
    private DatabaseReference transaction;

    private Context context;
    private String groupId;

    public AddTransactionDialog(Context context, String groupId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
        this.groupId = groupId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaction);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        group = database.getReference(MMConstant.GROUPS);
        transaction = database.getReference(MMConstant.TRANSACTIONS);
        final DatabaseReference myGroup = group.child(groupId);
        final DatabaseReference income = transaction.child(groupId).child(MMConstant.INCOME);
        final DatabaseReference balance = myGroup.child(MMConstant.BALANCE);
        final DatabaseReference expense = transaction.child(groupId).child(MMConstant.EXPENSE);

        transactionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tagSpinner.setVisibility(View.GONE);
                } else {
                    tagSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String type = transactionSpinner.getSelectedItem().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                if (type.equals(MMConstant.INCOME)) {
                    DatabaseReference incomeChild = income.push();
                    income.child(incomeChild.getKey()).child(MMConstant.AMOUNT).setValue(amount.getText().toString());
                    income.child(incomeChild.getKey()).child(MMConstant.USER_NAME).setValue(MMUserPreference.getUserName(context));
                    income.child(incomeChild.getKey()).child(MMConstant.TIMESTAMP).setValue(currentDateandTime);

                    balance.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            balance.setValue(Integer.parseInt(amount.getText().toString()) + Integer.parseInt(dataSnapshot.getValue().toString()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    DatabaseReference outcomeChild = expense.push();
                    expense.child(outcomeChild.getKey()).child(MMConstant.AMOUNT).setValue(amount.getText().toString());
                    expense.child(outcomeChild.getKey()).child(MMConstant.USER_NAME).setValue(MMUserPreference.getUserName(context));
                    expense.child(outcomeChild.getKey()).child(MMConstant.TIMESTAMP).setValue(currentDateandTime);

                    Spinner tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
                    String tag = tagSpinner.getSelectedItem().toString();
                    expense.child(outcomeChild.getKey()).child(MMConstant.TAG).setValue(tag);

                    balance.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            balance.setValue(Integer.parseInt(dataSnapshot.getValue().toString()) - Integer.parseInt(amount.getText().toString()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                Toast.makeText(context, "new transaction record added",
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
