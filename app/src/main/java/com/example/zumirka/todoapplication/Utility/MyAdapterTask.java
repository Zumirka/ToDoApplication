package com.example.zumirka.todoapplication.Utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zumirka.todoapplication.Activity.NewTaskActivity;
import com.example.zumirka.todoapplication.Model.Task;
import com.example.zumirka.todoapplication.R;

import java.util.List;

public class MyAdapterTask extends
        RecyclerView.Adapter<MyAdapterTask.ViewHolder>  {
    private List<Task> taskList;
    private Context mCtx;
    private DataBaseSQLite db;

    public MyAdapterTask(List<Task> tl)
    {
        taskList=tl;
    }


    @Override
    public MyAdapterTask.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mCtx =parent.getContext();
        this.mCtx=mCtx;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View customView= inflater.inflate(R.layout.recycler_view_task,parent,false);
        ViewHolder vH=new ViewHolder(customView);
        return  vH;
    }

    @Override
    public void onBindViewHolder(final MyAdapterTask.ViewHolder holder, int position) {
        final Task item = taskList.get(position);

        TextView title = holder.textViewTaskName;
        title.setText(item.getTaskName());
        final CheckBox checkBoxTask = holder.checkBoxTask;
        holder.checkBoxTask.setChecked(taskList.get(position).getTaskStatus());


        holder.checkBoxTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckBoxChange(item);
                holder.checkBoxTask.setChecked(item.getTaskStatus());
            }
        });
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popMenu = new PopupMenu(mCtx, holder.buttonViewOption);
                popMenu.inflate(R.menu.recycler_menu);
                popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.editMenu:
                                int status;
                                if(item.getTaskStatus() == true)
                                    status=1;
                                else status =0;

                                Intent intent = new Intent(mCtx,NewTaskActivity.class);
                                intent.putExtra("TitleID",item.getTitleID());
                                intent.putExtra("status",status);
                                intent.putExtra("TaskID",item.getTaskID());
                                mCtx.startActivity(intent);

                                break;
                            case R.id.deleteMenu:
                                alertAndDelete(item.getTaskID(),item.getTaskName(), item.getTitleID());
                                break;
                        }
                        return false;
                    }
                });
                popMenu.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckBoxChange(item);
                holder.checkBoxTask.setChecked(item.getTaskStatus());

            }
        });
    }

    public void setCheckBoxChange(Task item)
    {
        item.setTaskStatus();
        db = new DataBaseSQLite(mCtx);
        Boolean updateDB = db.updateTask(item.getTaskID(),item.getTitleID(),item.getTaskName(),item.getTaskStatus());
        if(updateDB)
        {
            refreshList(item.getTitleID());
        }
        db.close();

    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTaskName;
        public CheckBox checkBoxTask;
        public TextView buttonViewOption;


        public ViewHolder(View itemView) {
            super(itemView);

            textViewTaskName = itemView.findViewById(R.id.textViewTaskName);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }

    }
    public void alertAndDelete( final int taskID, String taskName, final int titleID)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setMessage("Czy na pewno chcesz usuąć zadanie o treści " + taskName + "?");
        alertDialogBuilder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db = new DataBaseSQLite(mCtx);
                int result = db.deleteTask(taskID);
                if (result > 0) {
                    refreshList(titleID);
                    db.close();
                    Toast.makeText(mCtx, "Usunięto", Toast.LENGTH_LONG).show();

                } else
                    {
                        db.close();
                        Toast.makeText(mCtx, "Usuwanie nie powiodło się", Toast.LENGTH_LONG).show();
                    }

            }

        });
        alertDialogBuilder.setNegativeButton("Nie",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void refreshList(int titleID)
    {
        taskList.clear();
        taskList = Task.getTaskList(mCtx,titleID);
        notifyDataSetChanged();
    }



}