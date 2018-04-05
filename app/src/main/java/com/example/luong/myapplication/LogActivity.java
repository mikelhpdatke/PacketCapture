package com.example.luong.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class LogActivity extends AppCompatActivity {
    TextView textView;
    LogAdapter adapter = new LogAdapter(this);
    static List<String> list = new ArrayList<String>();

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    static void addString(String s){
        list.add(s);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);

        recyclerView = (RecyclerView) findViewById(R.id.log_view);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setList(list);
        recyclerView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.log_button_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }




}
