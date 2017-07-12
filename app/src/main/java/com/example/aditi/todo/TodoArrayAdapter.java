package com.example.aditi.todo;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aditi on 6/28/2017.
 */

public class TodoArrayAdapter extends ArrayAdapter<Todo> {

    ArrayList<Todo> todoArrayList;
    Context context;


    public TodoArrayAdapter(@NonNull Context context, ArrayList<Todo> todoArrayList) {
        super(context, 0, todoArrayList);
        this.todoArrayList = todoArrayList;
        this.context = context;
    }

    static class TodoViewHolder {
        TextView titleTextView;
        TextView categoryTextView;
        TextView descriptionTextView;
        TextView dateTextView;

        public TodoViewHolder(TextView titleTextView, TextView categoryTextView, TextView descriptionTextView, TextView dateTextView) {
            this.titleTextView = titleTextView;
            this.categoryTextView = categoryTextView;
            this.descriptionTextView = descriptionTextView;
            this.dateTextView = dateTextView;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            TextView titleTV = (TextView) convertView.findViewById(R.id.titleTextView);
            TextView categoryTV = (TextView) convertView.findViewById(R.id.categoryTextView);
            TextView descriptionTV = (TextView) convertView.findViewById(R.id.descriptionTextView);
            TextView dateTV = (TextView) convertView.findViewById(R.id.dateTextView);
            TodoViewHolder todoViewHolder = new TodoViewHolder(titleTV, categoryTV, descriptionTV, dateTV);
            convertView.setTag(todoViewHolder);
        }

        Todo todo = todoArrayList.get(position);
        TodoViewHolder todoViewHolder = (TodoViewHolder) convertView.getTag();
        todoViewHolder.titleTextView.setText(todo.title);
        todoViewHolder.categoryTextView.setText("("+todo.category+")");
        if(todo.description.length() < 40)
            todoViewHolder.descriptionTextView.setText(todo.description);
        else
            todoViewHolder.descriptionTextView.setText(todo.description.substring(0,40) + "...");
        long epochTime = todo.date;
        Date date = new Date(epochTime);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        format.getTimeZone();
        String text = format.format(date);
        todoViewHolder.dateTextView.setText(text);

        return convertView;
    }
}
