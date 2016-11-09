package com.cloudycrew.cloudycar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class SignUpCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_complete);

        CardView riderCV = (CardView)findViewById(R.id.rider_card);
        riderCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRiderSummary();
            }
        });

        CardView driverCV = (CardView)findViewById(R.id.driver_card);
        driverCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDriverSummary();
            }
        });

    }

    private void startRiderSummary() {
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("mode", "rider");
        startActivity(intent);
    }

    private void startDriverSummary() {
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("mode", "driver");
        startActivity(intent);
    }
}