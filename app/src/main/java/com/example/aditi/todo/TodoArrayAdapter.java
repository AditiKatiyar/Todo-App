package com.example.aditi.todo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aditi on 6/28/2017.
 */


// filter function is at the end, searchView is in acitivty_main.xml*******************

public class TodoArrayAdapter extends ArrayAdapter<Todo> {

    ArrayList<Todo> todoArrayList;
    ArrayList<Todo> extraTodoList;
    Context context;


    public TodoArrayAdapter(@NonNull Context context, ArrayList<Todo> todoArrayList) {
        super(context, 0);
        this.todoArrayList = todoArrayList;
        this.context = context;
        /*extraTodoList = new ArrayList<>();
        extraTodoList.addAll(todoArrayList);*/

        extraTodoList = todoArrayList;
        getFilter();
    }

    @Override
    public int getCount() {
        return todoArrayList.size();
    }

    static class TodoViewHolder {
        TextView titleTextView;
        TextView categoryTextView;
        TextView descriptionTextView;
        TextView dateTextView;
        ImageView imageView;

        public TodoViewHolder(TextView titleTextView, TextView categoryTextView, TextView descriptionTextView, TextView dateTextView, ImageView imageView) {
            this.titleTextView = titleTextView;
            this.categoryTextView = categoryTextView;
            this.descriptionTextView = descriptionTextView;
            this.dateTextView = dateTextView;
            this.imageView = imageView;
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
            ImageView imageV = (ImageView) convertView.findViewById(R.id.image_view);
            TodoViewHolder todoViewHolder = new TodoViewHolder(titleTV, categoryTV, descriptionTV, dateTV, imageV);
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
        int deadlinePassed = todo.deadlinePassed;
        int done = todo.done;
        if (deadlinePassed == 1)
        {
            if (done == 1)
            {
                todoViewHolder.imageView.setBackgroundResource(R.drawable.tick);
            }
            else
            {
                todoViewHolder.imageView.setBackgroundResource(R.drawable.cross);
            }
        }
        else
        {
            todoViewHolder.imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        return convertView;
    }

    // to filter todos
    public void filter(String searchText)
    {
        //todoArrayList.clear();

        todoArrayList = new ArrayList<>();
        if (searchText.length() == 0)
        {
            /*todoArrayList.addAll(extraTodoList);
            Log.i("filter ", "after add all");*/
            todoArrayList = extraTodoList;
        }
        else
        {
            for (int i = 0; i< extraTodoList.size() ; i++)
            {
                Todo tempTodo = extraTodoList.get(i);
                if (tempTodo.title.toLowerCase().contains(searchText.toLowerCase()))
                {
                    todoArrayList.add(tempTodo);
                    Log.i("filter ", "inside if of for loop");
                }
            }
        }

        this.notifyDataSetChanged();
        Log.i("filter function ", "end");
    }
}
