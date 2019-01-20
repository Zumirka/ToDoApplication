package com.example.zumirka.todoapplication.Model;

import android.content.Context;
import android.database.Cursor;

import com.example.zumirka.todoapplication.Utility.DataBaseSQLite;

import java.util.ArrayList;

public class Title {
    private String titleName="";
    private int  titleID;

    //constructor
    public Title(String titlename, int titleid)
    {
        titleName=titlename;
        titleID=titleid;
    }


    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int gettitleID() { return titleID; }

    public static ArrayList<Title> getTitleList(Context c)
    {
        DataBaseSQLite db = new DataBaseSQLite(c);
        ArrayList<Title> titleList= new ArrayList<Title>();
        Cursor result= db.getAllTitle();

        if(result.getCount()>0)
        {
            while (result.moveToNext())
            {
                titleList.add(new Title(result.getString(1), result.getInt(0)));
            }
        }
        db.close();
        return  titleList;
    }

}
