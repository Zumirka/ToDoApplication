package com.example.zumirka.todoapplication.Utility;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;

import com.example.zumirka.todoapplication.Activity.MainActivity;
import com.example.zumirka.todoapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SaveFile {

    public static void SaveFile(Context mCtx)
    {
            String fileName = mCtx.getString(R.string.appNameToFile)+".txt";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String path=dir.getAbsolutePath()+"/"+fileName;

            DataBaseSQLite db = new DataBaseSQLite(mCtx);
            Cursor c=db.getTitleWithTask();
            StringBuilder builder=new StringBuilder();
            String lastTitle="";
            while(c.moveToNext()){
                if(!lastTitle.equals(c.getString(0))){
                    builder.append("\nTitle: ");
                    builder.append(c.getString(0));
                }
                if(c.isNull(1)||c.isNull(2)){
                    builder.append("\n");
                    continue;
                }
                builder.append("\n \tTask: ");
                builder.append(c.getString(1));
                builder.append("  ");
                builder.append((c.getString(2).equals("0"))?"TODO":"DONE");
                builder.append("\n");
                lastTitle=c.getString(0);
            }
            String content = builder.toString();
            db.close();
            try{

                File file = new File(path);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter myOutWriter= new OutputStreamWriter(fos);
                myOutWriter.append(content);
                myOutWriter.close();
                fos.close();
                Toast.makeText(mCtx,mCtx.getString(R.string.saveFile),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
