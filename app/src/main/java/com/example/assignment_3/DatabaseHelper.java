package com.example.assignment_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/*public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "CREATE TABLE " + DatabaseSchema.TableDB.TABLE_NAME + " (" +
            DatabaseSchema.TableDB._ID + " INTEGER PRIMARY KEY, " +
            DatabaseSchema.TableDB.COLUMN_USERNAME + " TEXT, " +
            DatabaseSchema.TableDB.COLUMN_PASSWORD + " TEXT, " +
            DatabaseSchema.TableDB.COLUMN_SERVICE + " TEXT, " +
            DatabaseSchema.TableDB.COLUMN_EMAIL + " TEXT)";

    private static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseSchema.TableDB.TABLE_NAME;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Passwords.db";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //public DBHelper(context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}*/
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "passwords.db";
    private static final int DATABASE_VERSION = 1;

    // Define the table and column names
    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SERVICE = "service";
    private static final String COLUMN_EMAIL = "email";
    //DBHelper dbHelper = new DBHelper(this);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the passwords table
        String createTableQuery = "CREATE TABLE " + TABLE_PASSWORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_SERVICE + " TEXT, "
                + COLUMN_EMAIL + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public void updateTable(SQLiteDatabase db, String username, String password, String email, String service) {
        //DatabaseHelper DatabaseHelper = new DatabaseHelper(this);
        //SQLiteDatabase db = DatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(DatabaseSchema.TableDB.COLUMN_SERVICE, "Spotify");

        String selection = DatabaseSchema.TableDB.COLUMN_SERVICE + " LIKE ?";
        String[] selectArgs = {service};

        int count = db.update(DatabaseSchema.TableDB.TABLE_NAME, values, selection, selectArgs);
        //Log.i("COUNT", new Integer(count).toString());
    }

    public void addTableEntry(SQLiteDatabase db, String username, String password, String email, String service) {
        //DatabaseHelper DatabaseHelper = new DatabaseHelper(this);
        //SQLiteDatabase db = DatabaseHelper.getWritableDatabase(); //getWriteable vs getReadable
        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.TableDB.COLUMN_USERNAME, username);
        values.put(DatabaseSchema.TableDB.COLUMN_PASSWORD, password);
        values.put(DatabaseSchema.TableDB.COLUMN_EMAIL, email);
        values.put(DatabaseSchema.TableDB.COLUMN_SERVICE, service);
        long rowID = db.insert(DatabaseSchema.TableDB.TABLE_NAME, null, values);
        //Log.i("ROWID", new Long(rowID).toString());
        //current = rowID;
    }

    public void readTableEntries(SQLiteDatabase db) {
        //DatabaseHelper DatabaseHelper = new DatabaseHelper(this);
        //SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        String[] projection = {         //projection is mandatory
                BaseColumns._ID,
                DatabaseSchema.TableDB.COLUMN_USERNAME,
                DatabaseSchema.TableDB.COLUMN_PASSWORD,
                DatabaseSchema.TableDB.COLUMN_SERVICE,
                DatabaseSchema.TableDB.COLUMN_EMAIL
        };
        //String selection = DatabaseSchema.TableDB.COLUMN_SERVICE + " = ?"; //optional
        //String[] selectArgs = {"Spotify"}; //optional

        String sortOrder = DatabaseSchema.TableDB.COLUMN_SERVICE + " DESC"; // or ASC (optional)

        //Cursor cursor = db.query(DatabaseSchema.TableDB.TABLE_NAME, projection, selection, selectArgs, null, null, sortOrder);
        Cursor cursor = db.query(DatabaseSchema.TableDB.TABLE_NAME, projection, null, null, null, null, sortOrder);
        while (cursor.moveToNext()) {
            String[] names = cursor.getColumnNames();
            for (int i = 0; i < names.length; i++) {
                int index = cursor.getColumnIndex(names[i]);
                String value = cursor.getString(index);
                Log.i("Values", value);
            }
        }
        cursor.close();
    }
}