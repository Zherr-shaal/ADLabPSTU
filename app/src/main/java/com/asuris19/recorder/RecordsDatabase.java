package com.asuris19.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.asuris19.recorder.models.RecordModel;

public class RecordsDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class RecordingTable implements BaseColumns {
        public static final String TABLE_NAME = "recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordingTable.TABLE_NAME + " (" +
                    RecordingTable._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    RecordingTable.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    RecordingTable.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    RecordingTable.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA_SEP +
                    RecordingTable.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + RecordingTable.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public RecordsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @SuppressLint("Range")
    public RecordModel getItemAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                RecordingTable._ID,
                RecordingTable.COLUMN_NAME_RECORDING_NAME,
                RecordingTable.COLUMN_NAME_RECORDING_FILE_PATH,
                RecordingTable.COLUMN_NAME_RECORDING_LENGTH,
                RecordingTable.COLUMN_NAME_TIME_ADDED
        };
        Cursor c = db.query(RecordingTable.TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            RecordModel item = new RecordModel();
            item.setId(c.getInt(c.getColumnIndex(RecordingTable._ID)));
            item.setName(c.getString(c.getColumnIndex(RecordingTable.COLUMN_NAME_RECORDING_NAME)));
            item.setFilePath(c.getString(c.getColumnIndex(RecordingTable.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.setLength(c.getInt(c.getColumnIndex(RecordingTable.COLUMN_NAME_RECORDING_LENGTH)));
            item.setTime(c.getLong(c.getColumnIndex(RecordingTable.COLUMN_NAME_TIME_ADDED)));
            c.close();
            return item;
        }
        return null;
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {RecordingTable._ID};
        Cursor c = db.query(RecordingTable.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }
}
