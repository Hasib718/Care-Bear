package com.hasib.carebear.support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hasib.carebear.R;

public class FeedBackActivity extends AppCompatActivity {

    private static final String TAG = "FeedBackActivity";

    private EditText reportText;
    private Button reportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        this.setTitle("FeedBack");

        //Enable back button on Menu Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reportButton = findViewById(R.id.reportButton);
        reportText = findViewById(R.id.reportText);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.email_Hasib),getString(R.string.email_Toufiqul), getString(R.string.email_Wadud) });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback From Care Bear");
                intent.putExtra(Intent.EXTRA_TEXT, "Message:"+reportText.getText());

                try {
                    startActivity(Intent.createChooser(intent, "Please select Email"));
                    Log.d(TAG, "onClick: It's working");

                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(FeedBackActivity.this, "There are no Email Clients", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reportText.setMovementMethod(new ScrollingMovementMethod());
    }

    //This Function is needed for back button.. Without this function
    //back button wouldn't work properly..
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
