package com.example.zumirka.todoapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;

import java.util.ArrayList;

public class Task {
    private String taskName="";
    private boolean status;
    private int titleID;
    private int taskID;

    public Task(int taskID, String taskName, int titleID, boolean status)
    {
        this.taskID = taskID;
        this.taskName=taskName;
        this.titleID=titleID;
        this.status=status;
    }

    public String getTaskName() {return taskName;}
    public void setTaskName(String taskName) { this.taskName=taskName;}
    public int getTaskID() {return taskID;}
    public int getTitleID() {return titleID;}
    public void setTaskStatus() {this.status=!status;}
    public boolean getTaskStatus() {return  status;}

    public static ArrayList<Task> getTaskList(Context c, int titleID)
    {
        DataBaseSQLite db = new DataBaseSQLite(c);
        ArrayList<Task> taskList= new ArrayList<Task>();
        Cursor result= db.getAllTasks(titleID);

        if(result.getCount()>0)
        {
            while (result.moveToNext())
            {
                boolean stats;
                if(result.getInt(2)==1)
                    stats=true;
                else
                    stats=false;

                taskList.add(new Task(result.getInt(0),result.getString(1),result.getInt(3),stats));
            }
        }
        db.close();
        return  taskList;
    }
}
