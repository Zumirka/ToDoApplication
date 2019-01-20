package com.example.zumirka.todoapplication.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zumirka.todoapplication.Model.Task;

public class DataBaseSQLite extends SQLiteOpenHelper {
    // Nazwa bazy
    public static final String DATABASE_NAME="TODOList.db";

    //Nazwa tabeli
    public static final String TABLE_NAME="Title";
    public static final String TABLE_NAME2="Tasks";

    //nazwa kolumny dla tabeli title
    public static final String COL_1="ID";
    public static final String COL_2="TitleName";

    //nazwa kolumny dla tabeli Tasks
    public static final String COL_11="ID";
    public static final String COL_12="TitleID";
    public static final String COL_13="TaskName";
    public static final String COL_14="Status";

    // kreowanie tabeli 1
    public static final String table_1_create="CREATE TABLE " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY autoincrement NOT NULL,"+ COL_2+ " TEXT NOT NULL)";

    //kreowanie tabeli 2
    public static final String table_2_create="CREATE TABLE " + TABLE_NAME2 +" (" + COL_11+" integer PRIMARY KEY autoincrement NOT NULL,"
            +COL_13+ " TEXT,"+COL_14+ " integer NOT NULL DEFAULT 0,"
            +COL_12 +" integer NOT NULL,"
            + " FOREIGN KEY("+ COL_12 +") REFERENCES "+TABLE_NAME+"("+ COL_1+ "))";



    // konstruktor
    public DataBaseSQLite(Context context) {
        super(context, DATABASE_NAME,null, 1);

    }

    //metody bazowe
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //kreowanie tabeli 1 i 2
        sqLiteDatabase.execSQL(table_1_create);
        sqLiteDatabase.execSQL(table_2_create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  " + TABLE_NAME );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  " + TABLE_NAME2 );
        onCreate(sqLiteDatabase);
    }

    //dodawanie do bazy tytułu
    public boolean insertTitleData(String title) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_2,title);
        long result=sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        //insert jeśli sie nie powiedzie zwraca -1 dlatego sprawdzamy czy sie udało dodać czy nie
        if(result==-1)
            return false;
        else return true;
    }

    //dodawanie do bazy taska
    public boolean insertTaskData(int titleID, String task)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_12,titleID);
        contentValues.put(COL_13, task);
        long result=sqLiteDatabase.insert(TABLE_NAME2,null,contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    //pobieranie z tabeli 1(Title) wszystkich elementów
    public Cursor getAllTitle()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME + " ORDER BY " + COL_2,null);
        return res;
    }
    //pobieranie z tabeli 1 wszystkich tasków do title
    public Cursor getAllTasks(int titleID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME2+" WHERE "+ COL_12 + "=" + titleID + " ORDER BY "+ COL_14 + " ASC, " + COL_13 + " ASC",null);
        return res;
    }
    //ID ostatnio dodanego title do tabeli
    public int lastAddedTitleID()
    {
        int lastAddedTitleID;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res=sqLiteDatabase.rawQuery("SELECT last_insert_rowid() FROM "+TABLE_NAME+" LIMIT 1",null);
        res.moveToFirst();
        lastAddedTitleID=  Integer.parseInt(res.getString(0));
        return lastAddedTitleID;
    }
    public boolean updateTask(int taskID, int titleID, String taskName, boolean taskStatus)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_11, taskID);
        contentValues.put(COL_12, titleID);
        contentValues.put(COL_13, taskName);
        contentValues.put(COL_14, taskStatus);
        long result= sqLiteDatabase.update(TABLE_NAME2, contentValues,"ID= ?",new String[] {String.valueOf(taskID)});
        if(result==-1)
        return false;
        else
            return true;
    }
    public boolean updateTitle (int titleID, String titleName)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_1, titleID);
        contentValues.put(COL_2, titleName);
        long result= sqLiteDatabase.update(TABLE_NAME, contentValues,"ID= ?",new String[] {String.valueOf(titleID)});
        if(result==-1)
            return false;
        else
            return true;
    }


    public Integer deleteTitle(int ID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
            int res2 = sqLiteDatabase.delete(TABLE_NAME2, "TitleID=?", new String[]{String.valueOf(ID)});
            int res1 = sqLiteDatabase.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(ID)});
            if ((res2 > 0 && res1 > 0) || res1 > 0)
                return 1;
        return -1;
    }
    public Integer deleteTask(int taskID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        int result= sqLiteDatabase.delete(TABLE_NAME2, "ID=?", new String[]{String.valueOf(taskID)});
        return result;

    }

    public Cursor getTitle(int titleID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("SELECT *  FROM " + TABLE_NAME + " WHERE " + COL_1 + " = " + titleID,null);
        return  res;

    }
    public Cursor getTask(int taskID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("SELECT *  FROM " + TABLE_NAME2 + " WHERE " + COL_11 + " = " + taskID,null);
        return  res;

    }


    public Cursor getcountOfTasksFromTitle(int titleID)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        Cursor res=sqLiteDatabase.rawQuery("SELECT COUNT(*)AS count, SUM(CASE WHEN " +COL_14+ "=1 THEN 1 ELSE 0 END) AS done" +
                " FROM "+TABLE_NAME2+" WHERE "+ COL_12+ "=" + titleID,null);
        return res;

    }
    public Cursor getTitleWithTask(){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        Cursor res=sqLiteDatabase.rawQuery("SELECT " +COL_2+ "," +COL_13+ "," +COL_14+ " FROM " +TABLE_NAME+
                " AS TIT LEFT JOIN " +TABLE_NAME2+ " ON TIT."+COL_1+ "=" +COL_12 + " ORDER BY "+COL_2+","+COL_13 ,null);
        return res;
    }

}
