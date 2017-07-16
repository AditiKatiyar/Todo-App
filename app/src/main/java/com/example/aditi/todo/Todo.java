package com.example.aditi.todo;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Aditi on 6/28/2017.
 */

public class Todo implements Serializable {
    int id;
    String title;
    String category;
    String description;
    long date;
    long deadline;
    boolean done;
    boolean deadlinePassed;

    public Todo(String title, String category, String description, int id, long date, long deadline) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.id = id;
        this.date = date;
        this.deadline = deadline;
    }

    public static Comparator<Todo> byTitle = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            return o1.title.compareTo(o2.title);
        }
    };

    public static Comparator<Todo> byCategory = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            return o1.category.compareTo(o2.category);
        }
    };

    public static Comparator<Todo> byDeadline = new Comparator<Todo>() {
        @Override
        public int compare(Todo o1, Todo o2) {
            return (int) (o1.deadline - o2.deadline);
        }
    };
}
