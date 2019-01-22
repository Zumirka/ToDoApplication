package com.example.zumirka.todoapplication.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.zumirka.todoapplication.Model.Task;
import com.example.zumirka.todoapplication.R;
import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;
import com.example.zumirka.todoapplication.Utility.MyAdapterTask;


import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    private int titleID;
    private DataBaseSQLite db;
    private FloatingActionButton floatingActionBTN;
    private ArrayList<Task> taskList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        floatingActionBTN= findViewById(R.id.floatingActionBTNtask);
        titleID = (Integer) getIntent().getSerializableExtra("TitleID");
        mRecyclerView= findViewById(R.id.recyclerViewTask);
        db =new DataBaseSQLite(this);
        Cursor res=db.getTitle(titleID);
        if (res.getCount() > 0) {
            while (res.moveToNext()) {
                setTitle(this.getString(R.string.taskTitle)+" "+res.getString(1));
            }
        }
        db.close();
        createAndRefreshScreen();


    }
    public void floatingBTNTaskOnClick (View v)
    {
        Intent intent= new Intent(TaskActivity.this,NewTaskActivity.class);
        intent.putExtra("TitleID",titleID);
        intent.putExtra("status",-1);
        intent.putExtra("TaskID",-1);
        startActivity(intent);
    }
    public void setTitleID(int id)
    {
        this.titleID=id;
    }

    public void createAndRefreshScreen()
    {
        taskList = Task.getTaskList(this,titleID);
        MyAdapterTask mAdapter= new MyAdapterTask(taskList);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void onRestart()
    {
        super.onRestart();
        createAndRefreshScreen();

    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
