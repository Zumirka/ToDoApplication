package com.example.zumirka.todoapplication.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zumirka.todoapplication.R;
import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;

public class NewTitleActivity extends AppCompatActivity {

    private DataBaseSQLite db;
    private EditText titleEditText;
    private String titleText;
    private boolean titleDB = false;
    private int titleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_title);
        titleEditText = findViewById(R.id.titleEditText);
        //strzałka powrotu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DataBaseSQLite(this);
        titleID = (Integer) getIntent().getSerializableExtra("TitleID");
        if (titleID != -1) {
            Button editTitleBTN = findViewById(R.id.createTitleBTN);
            editTitleBTN.setText(getText(R.string.acceptEdit));
            Cursor res = db.getTitle(titleID);
            if (res.getCount() > 0) {
                while (res.moveToNext()) {
                    titleEditText.setText(res.getString(1));
                }
                titleEditText.setSelection(titleEditText.getText().length());
            } else {
                finish();
            }
        }


    }

    public void createTitleBTNOnClick(View view) {

        titleText = titleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(titleText))
        {
            titleEditText.setError(this.getString(R.string.empty));
            titleEditText.setText("");
            return;
        }
        else
        {
            if (titleID != -1) {
                titleDB = db.updateTitle(titleID, titleText);
                if (titleDB) {
                    titleEditText.setText("");
                    db.close();
                    finish();
                } else
                    Toast.makeText(NewTitleActivity.this, this.getString(R.string.notInserted), Toast.LENGTH_LONG).show();

            } else {
                titleDB = db.insertTitleData(titleText);
                if (titleDB) {
                    titleID = db.lastAddedTitleID();
                    Intent intent = new Intent(NewTitleActivity.this, TaskActivity.class);
                    intent.putExtra("TitleID", titleID);
                    startActivity(intent);
                    titleEditText.setText("");
                    Toast.makeText(NewTitleActivity.this,this.getString(R.string.addTitle)+" "+titleText,Toast.LENGTH_LONG).show();
                    db.close();
                    finish();
                } else
                    Toast.makeText(NewTitleActivity.this, this.getString(R.string.notInserted), Toast.LENGTH_LONG).show();
            }
        }
    }
    public void cancelTitleBTNOnClick(View v)
    {
        titleEditText.setText("");
        finish();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
