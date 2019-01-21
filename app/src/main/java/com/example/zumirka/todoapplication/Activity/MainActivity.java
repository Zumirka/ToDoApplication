package com.example.zumirka.todoapplication.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.zumirka.todoapplication.Model.Title;
import com.example.zumirka.todoapplication.R;
import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;
import com.example.zumirka.todoapplication.Utility.MyAdapterMain;
import com.example.zumirka.todoapplication.Utility.SaveFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DataBaseSQLite db;
    private ArrayList<Title> titleList;
    private FloatingActionButton floatingActionBTN;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerViewMain);
        floatingActionBTN = findViewById(R.id.floatingActionBTN);
        createAndRefreshScreen();

    }

    public void createAndRefreshScreen()
    {
        //pobieranie danych z bazy do ArrayAdaptera
        titleList = Title.getTitleList(this);
        //dodanie do adaptera listy którą chcemy wyświetlić oraz dodanie adaptera do widoku + wyświetlenie tego
        final MyAdapterMain mAdapter = new MyAdapterMain(titleList);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.notifyDataSetChanged();
    }
    public void floatingBTNOnClick (View v)
    {
        Intent intent= new Intent(MainActivity.this, NewTitleActivity.class);
        intent.putExtra("TitleID",-1);
        startActivity(intent);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        createAndRefreshScreen();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.saveFile:
                askPermissionAndWriteFile();
                return true;
            case R.id.closeApp:
                Toast.makeText(MainActivity.this,this.getString(R.string.closeApp),Toast.LENGTH_LONG).show();
                this.finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //pozwolenie na zapis pliku w pamięci wewnętrznej
    private void askPermissionAndWriteFile() {
        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //
        if (canWrite) {
            SaveFile.SaveFile(this);
        }
    }
    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(new String[]{permissionName}, requestId);
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            switch (requestCode) {

                case REQUEST_ID_WRITE_PERMISSION: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SaveFile.SaveFile(this);
                    }
                }
            }
        } else
            {
            Toast.makeText(getApplicationContext(), this.getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
        }
    }



}
