package com.jarvis.marshall.view.home.task;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jarvis.marshall.R;
import com.jarvis.marshall.dataAccess.TaskDA;
import com.jarvis.marshall.model.Task;

import java.util.ArrayList;

/**
 * Created by Jarvis on 13/02/2018.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ListHolder> {
    private Context context;
    private ArrayList<Task> taskArrayList;
    private LayoutInflater inflater;

    public TaskAdapter(Context context, ArrayList<Task> taskArrayList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.taskArrayList = taskArrayList;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.viewholder_task, parent, false);
        return new ListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Task task = taskArrayList.get(position);
        final TaskDA taskDA = new TaskDA();

        holder.title.setText(task.getName());
        if(task.getStatus().equals("Task done")){
            holder.check.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        }
        holder.status.setText(task.getStatus());

        holder.date.setText(task.getDeadlineDate());
        holder.time.setText(task.getDeadlineTime());

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.status.getText().toString().equals("Undone")){
                    holder.check.setColorFilter(context.getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    holder.status.setText("Task done");
                    taskDA.setStatus(task.getKey(),"Task done");
                } else {
                    holder.check.setColorFilter(context.getApplicationContext().getResources().getColor(R.color.material_grey_500));
                    holder.status.setText("Undone");
                    taskDA.setStatus(task.getKey(),"Undone");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public class ListHolder extends RecyclerView.ViewHolder {
        private TextView title, date,time, status;
        private ImageView check;
        public ListHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.vh_task_name);
            date = itemView.findViewById(R.id.vh_task_deadlineDate);
            time = itemView.findViewById(R.id.vh_task_deadlineTime);
            status = itemView.findViewById(R.id.vh_task_status);
            check = itemView.findViewById(R.id.vh_task_checkImage);
        }
    }
}
