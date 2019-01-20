package com.example.zumirka.todoapplication.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zumirka.todoapplication.R;
import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;

public class NewTaskActivity extends TaskActivity {

    private int titleID;
    private int taskID;
    private int status;
    private boolean s;
    private EditText taskEditText;
    private CheckBox checkStatus;
    private String taskText;
    private DataBaseSQLite db;
    private boolean taskDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        taskEditText = findViewById(R.id.taskEditText);
        checkStatus = findViewById(R.id.taskStatusCB);
        db = new DataBaseSQLite(this);
        titleID = (Integer) getIntent().getSerializableExtra("TitleID");
        status = (Integer) getIntent().getSerializableExtra("status");
        taskID = (Integer) getIntent().getSerializableExtra("TaskID");
        checkStatus();

        if (status != -1) {
            Button editTaskBTN = findViewById(R.id.createTaskBTN);
            editTaskBTN.setText(getText(R.string.acceptEdit));
            Cursor res = db.getTask(taskID);
            if (res.getCount() > 0) {
                while (res.moveToNext()) {
                    taskEditText.setText(res.getString(1));
                }
                taskEditText.setSelection(taskEditText.getText().length());
                checkStatus.setChecked(s);
            }
        }
        else
            {
            findViewById(R.id.taskStatusCB).setVisibility(View.INVISIBLE);
        }
    }

    public void createTaskBTNOnClick(View view)
    {
        taskText = taskEditText.getText().toString().trim();
        if (TextUtils.isEmpty(taskText))
        {
            taskEditText.setError(this.getString(R.string.empty));
            taskEditText.setText("");
        }
        else
        {
            if (status != -1) {
                s = checkStatus.isChecked();
                taskDB = db.updateTask(taskID, titleID, taskText, s);
                if (taskDB) {
                    taskEditText.setText("");
                    createAndRefreshScreen();
                    setTitleID(titleID);
                    db.close();
                    finish();
                } else
                    Toast.makeText(NewTaskActivity.this, this.getString(R.string.notInserted), Toast.LENGTH_LONG).show();
            } else {
                taskDB = db.insertTaskData(titleID, taskText);
                if (taskDB) {
                    titleID = db.lastAddedTitleID();
                    setTitleID(titleID);
                    createAndRefreshScreen();
                    taskEditText.setText("");
                    db.close();
                    finish();
                } else
                    Toast.makeText(NewTaskActivity.this, this.getString(R.string.notInserted), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void cancelTaskBTNOnClick(View v)
    {
        taskEditText.setText("");
        finish();
    }


    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void checkStatus() {
        if (status != -1)
        {
            if (status == 1)
            {
                s = true;
                checkStatus.setText(getText(R.string.done));
            }

        }
    }

}
