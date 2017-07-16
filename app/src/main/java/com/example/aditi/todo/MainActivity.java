package com.example.aditi.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT_TODO = 2;
    ListView listView;
    ArrayList<Todo> todoList;
    TodoArrayAdapter todoArrayAdapter;
    public final static int NEW_TODO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        todoList = new ArrayList<>();
        todoArrayAdapter = new TodoArrayAdapter(this, todoList);
        listView.setAdapter(todoArrayAdapter);

        // setting item long click listener for deleting it
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Todo todo = todoList.get(position);
                        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                        SQLiteDatabase database = todoOpenHelper.getReadableDatabase();
                        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
                        while (cursor.moveToNext())
                        {
                            if(todo.id == cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID)))
                            {
                                database.execSQL("delete from "+TodoOpenHelper.TODO_TABLE_NAME+" where " + TodoOpenHelper.TODO_ID +
                                        " = "+ todo.id);
                                todoList.remove(position);
                                break;
                            }
                        }
                        todoArrayAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
                return true;
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, TodoDetailActivity.class);
                intent.putExtra(IntentConstants.ID, todoList.get(position).id);
                Log.i("id in main", ""+todoList.get(position).id);
                intent.putExtra(IntentConstants.POSITION, ""+position);
                startActivityForResult(intent, EDIT_TODO);
            }
        });

        // toolbar fetched from layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // search view fetched from layout
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                todoArrayAdapter.filter(newText);
                return false;
            }
        });


        updateTodoList();
    }

    // update todo list
    private void updateTodoList() {

        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
        SQLiteDatabase database = todoOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
        int titleIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE);
        int categoryIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY);
        int descriptionIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION);
        int idIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_ID);
        int dateIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DATE);
        int deadlineIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE);
        while(cursor.moveToNext())
        {
            String title = cursor.getString(titleIndex);
            String category = cursor.getString(categoryIndex);
            String description = cursor.getString(descriptionIndex);
            int _id = cursor.getInt(idIndex);
            long date = cursor.getLong(dateIndex);
            long deadline = cursor.getLong(deadlineIndex);
            Todo todo = new Todo(title, category, description, _id, date, deadline);
            todoList.add(todo);
        }

        todoArrayAdapter.notifyDataSetChanged();
    }

    // creating menu to add an sort todos by title, category, deadline
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.add)
        {
            Intent i = new Intent(this, TodoDetailActivity.class);
            startActivityForResult(i, NEW_TODO);
        }
        else if (id == R.id.sortByTitle)
        {
            Collections.sort(todoList, Todo.byTitle);
            todoArrayAdapter.notifyDataSetChanged();
        }
        else if (id == R.id.sortByCategory)
        {
            Collections.sort(todoList, Todo.byCategory);
            todoArrayAdapter.notifyDataSetChanged();
        }
        else if (id  == R.id.sortByDeadline)
        {
            Collections.sort(todoList, Todo.byDeadline);
            todoArrayAdapter.notifyDataSetChanged();
        }
        return true;
    }

    // updating the todoList when a todo is added in another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_TODO)
        {
            if(resultCode == RESULT_OK)
            {

                TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                SQLiteDatabase database = todoOpenHelper.getReadableDatabase();
                Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
                cursor.moveToLast();
                String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE));
                String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION));
                int _id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                long deadline = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE));
                Todo todo = new Todo(title, category, description, _id, date, deadline);
                todoList.add(todo);
                todoArrayAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == EDIT_TODO)
        {
            if(resultCode == RESULT_OK)
            {
                int id = data.getIntExtra(IntentConstants.ID, -1);
                if (id != -1)
                {
                    TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(MainActivity.this);
                    SQLiteDatabase database = todoOpenHelper.getReadableDatabase();
                    Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ID + " = " + id,
                            null, null, null,null);
                    /*Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
                    while (cursor.moveToNext())
                    {
                        if (cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID)) == id)
                        {
                            break;
                        }
                    }*/
                    cursor.moveToNext();
                    String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE));
                    String category = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY));
                    String description = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION));
                    int _id = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID));
                    long date = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
                    long deadline = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE));
                    Todo todo = new Todo(title, category, description, _id, date, deadline);
                    todoList.set(Integer.parseInt(data.getStringExtra(IntentConstants.POSITION)), todo);
                    todoArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    }


}
