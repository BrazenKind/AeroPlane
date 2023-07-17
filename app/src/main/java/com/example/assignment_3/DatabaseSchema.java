package com.example.assignment_3;

import android.provider.BaseColumns;

public class DatabaseSchema {
    public DatabaseSchema() {
    }

    public static final class TableDB implements BaseColumns {
        public static final String TABLE_NAME = "Passwords";
        public static final String COLUMN_USERNAME = "Username";
        public static final String COLUMN_PASSWORD = "Password";
        public static final String COLUMN_SERVICE = "Service";
        public static final String COLUMN_EMAIL = "Email";
    }
}
