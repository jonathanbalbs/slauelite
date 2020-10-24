package com.example.slauelite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public final class UserContract {
    //table name
    public  static final String TABLE_NAME = "users";

    //column names
    public  static final String ID = "id";
    public  static final String FULL_NAME = "name";
    public  static final String REG_NUMBER = "reg_number";
    public  static final String PHONE = "phone";
    public  static final String EMAIL = "email";
    public  static final String PASSWORD = "password";
    public  static final String FACULTY = "faculty";
    public  static final String COURSE = "course";
    public  static final String YEAR = "year";

    //sql query strings
    public  static final String SQL_CREATE_USER = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER PRIMARY KEY," + FULL_NAME + " VARCHAR(64), " + REG_NUMBER +
            " VARCHAR(32), " + PHONE + " VARCHAR(16), " + EMAIL + " VARCHAR(128), " + PASSWORD +
            " VARCHAR(255), " + FACULTY + " VARCHAR(8), " + COURSE + " VARCHAR(64), " + YEAR + " VARCHAR(8))";

    public  static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    //getters
    public String getId() {
        return ID;
    }
    public String getFullName() {
        return FULL_NAME;
    }
    public String getRegNumber() {
        return REG_NUMBER;
    }
    public String getPhone() {
        return PHONE;
    }
    public String getEmail() {
        return EMAIL;
    }
    public String getPassword() {
        return PASSWORD;
    }
    public String getFaculty() {
        return FACULTY;
    }
    public String getCourse() {
        return COURSE;
    }
    public String getYear() {
        return YEAR;
    }
    public  String getTableName() {
        return TABLE_NAME;
    }

    public String getSqlCreateUser() {
        return SQL_CREATE_USER;
    }
    public String getSqlDeleteTable() {
        return SQL_DELETE_TABLE;
    }
}
