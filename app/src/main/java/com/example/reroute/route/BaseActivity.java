package com.example.reroute.route;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reroute.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
    }

    protected abstract int getLayoutResourceId();

    /**
     * Sets the error icon and message according to the error state
     * @param errorMessage Current error message
     */
    protected void setErrorState(String errorMessage) {
        ImageView errorIcon = findViewById(R.id.error_icon);
        TextView errorText = findViewById(R.id.error_message);

        errorText.setText(errorMessage);
        errorIcon.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the error icon and message
     */
    protected void hideErrorState() {
        ImageView errorIcon = findViewById(R.id.error_icon);
        TextView errorText = findViewById(R.id.error_message);
        errorIcon.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
    }
}
