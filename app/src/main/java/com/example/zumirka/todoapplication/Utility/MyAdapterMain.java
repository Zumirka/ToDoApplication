package com.example.zumirka.todoapplication.Utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zumirka.todoapplication.Activity.NewTitleActivity;
import com.example.zumirka.todoapplication.Activity.TaskActivity;
import com.example.zumirka.todoapplication.Model.Title;
import com.example.zumirka.todoapplication.R;

import java.util.List;

public class MyAdapterMain extends
        RecyclerView.Adapter<MyAdapterMain.ViewHolder> {
    private List<Title> titleList;
    private Context mCtx;
    private DataBaseSQLite db;

    public MyAdapterMain(List<Title> tl) {
        titleList = tl;
    }


    @Override
    public MyAdapterMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mCtx = parent.getContext();
        this.mCtx = mCtx;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View customView = inflater.inflate(R.layout.recycler_view_main, parent, false);
        ViewHolder vH = new ViewHolder(customView);
        return vH;
    }

    @Override
    public void onBindViewHolder(final MyAdapterMain.ViewHolder holder, int position) {
        final Title item = titleList.get(position);

        TextView title = holder.textViewTitleName;
        title.setText(item.getTitleName());
        TextView count = holder.textViewCounter;
        int[] res = getCountOfTasksFromTitle(item.gettitleID());
        ProgressBar bar = holder.progress;
        bar.setMax(res[0]);
        bar.setProgress(res[1]);
        count.setText(res[1]+"/"+res[0]);


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
                                // otwiera okno edycji tytułu. Wysyła tam ID Taska żeby było wiadomo który chcemy edytować w bazie
                                Intent intent = new Intent(mCtx,NewTitleActivity.class);
                                intent.putExtra("TitleID", item.gettitleID());
                                mCtx.startActivity(intent);
                                break;
                            case R.id.deleteMenu:
                                alertAndDelete(item.gettitleID(), item.getTitleName());
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
                Intent intent = new Intent(mCtx,TaskActivity.class);
                intent.putExtra("TitleID", item.gettitleID());
                mCtx.startActivity(intent);
            }
        });
    }

    public int[] getCountOfTasksFromTitle(int titleID) {
        int[] count = new int[2];
        DataBaseSQLite db = new DataBaseSQLite(mCtx);
        Cursor result = db.getCountOfTasksFromTitle(titleID);
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                count[0] = result.getInt(0);
                count[1] = result.getInt(1);
            }
        }
        db.close();
        return count;
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitleName;
        public TextView textViewCounter;
        public TextView buttonViewOption;
        public ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitleName = itemView.findViewById(R.id.textViewTitleName);
            textViewCounter = itemView.findViewById(R.id.textViewCounter);
            progress = itemView.findViewById(R.id.progressBar);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }
    }

    public void alertAndDelete(final int titleID, String titleName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
        alertDialogBuilder.setMessage(mCtx.getString(R.string.deleteTitle)+" "+ titleName + "?");
        alertDialogBuilder.setPositiveButton(mCtx.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db = new DataBaseSQLite(mCtx);
                int result = db.deleteTitle(titleID);
                if (result > 0) {
                    refreshList();
                    db.close();
                    Toast.makeText(mCtx, mCtx.getString(R.string.isDelete), Toast.LENGTH_LONG).show();
                } else
                    {
                        db.close();
                        Toast.makeText(mCtx, mCtx.getString(R.string.notDelete), Toast.LENGTH_LONG).show();
                    }
            }

        });
        alertDialogBuilder.setNegativeButton(mCtx.getString(R.string.no),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void refreshList()
    {
        titleList.clear();
        titleList = Title.getTitleList(mCtx);
        notifyDataSetChanged();
    }

}
