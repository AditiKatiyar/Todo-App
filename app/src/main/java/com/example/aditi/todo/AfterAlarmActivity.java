package com.example.aditi.todo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AfterAlarmActivity extends AppCompatActivity {

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_alarm);

        Intent intent = getIntent();
        final Todo mTodo = (Todo) intent.getSerializableExtra(IntentConstants.OBJECT);

        TextView title = (TextView) findViewById(R.id.title_text);
        TextView category = (TextView) findViewById(R.id.category_text);
        TextView description = (TextView) findViewById(R.id.description_text);

        title.setText(mTodo.title);
        category.setText(mTodo.category);
        description.setText(mTodo.description);

        TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(AfterAlarmActivity.this);
        database = todoOpenHelper.getWritableDatabase();

        final ContentValues cv = new ContentValues();
        cv.put(TodoOpenHelper.TODO_TITLE, mTodo.title);
        cv.put(TodoOpenHelper.TODO_CATEGORY, mTodo.category);
        cv.put(TodoOpenHelper.TODO_DESCRIPTION, mTodo.description);
        cv.put(TodoOpenHelper.TODO_DATE, mTodo.date);
        cv.put(TodoOpenHelper.TODO_DEADLINE, mTodo.deadline);

        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.put(TodoOpenHelper.TODO_DEADLINE_PASSED, 1);
                cv.put(TodoOpenHelper.TODO_DONE, 1);
                goToMainActivity(cv, mTodo.id);
            }
        });

        Button notDoneButton = (Button) findViewById(R.id.not_done);
        notDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv.put(TodoOpenHelper.TODO_DEADLINE_PASSED, 1);
                cv.put(TodoOpenHelper.TODO_DONE, 0);
                goToMainActivity(cv, mTodo.id);
            }
        });
    }

    private void goToMainActivity(ContentValues cv, int Id)
    {
        database.update(TodoOpenHelper.TODO_TABLE_NAME, cv, TodoOpenHelper.TODO_ID + " = " + Id, null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
