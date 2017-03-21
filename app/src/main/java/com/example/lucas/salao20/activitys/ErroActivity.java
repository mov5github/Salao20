package com.example.lucas.salao20.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lucas.salao20.R;

public class ErroActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erro);
        this.textView = (TextView) findViewById(R.id.tverro);
        if (getIntent().getStringExtra("erro") != null){
            this.textView.setText(getIntent().getStringExtra("erro"));
        }
    }
}
