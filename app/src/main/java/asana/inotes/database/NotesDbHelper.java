package asana.inotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import asana.inotes.model.NotesModel;

public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 1;

    public class NotesColumns implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_DATE = "date";
    }

    private final String CREATE_NOTES_TABLE = "CREATE TABLE " +
            NotesColumns.TABLE_NAME + " (" +
            NotesColumns.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            NotesColumns.COLUMN_TITLE + " TEXT, " +
            NotesColumns.COLUMN_CONTENT + " TEXT, " +
            NotesColumns.COLUMN_LABEL + " TEXT, " +
            NotesColumns.COLUMN_DATE + " TEXT)";

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesColumns.TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String title, String content, String label) {
        //Getting current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Last modified :' yyyy / MM / dd 'at' HH:mm");

        //put data into ContentValues
        ContentValues values = new ContentValues();
        values.put(NotesColumns.COLUMN_TITLE, title);
        values.put(NotesColumns.COLUMN_CONTENT, content);
        values.put(NotesColumns.COLUMN_LABEL, label);
        values.put(NotesColumns.COLUMN_DATE, dateFormat.format(calendar.getTime()));
        long id = getWritableDatabase().insert(NotesColumns.TABLE_NAME, null, values);

        // if -1, an error occurred so data won't be inserted.
        return id != -1;
    }

    public List<NotesModel> getInsertedData() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + NotesColumns.TABLE_NAME, null);
        List<NotesModel> resultList = new ArrayList<>();

        // Convert cursor to List
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                long noteID = Long.parseLong(cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_ID)));
                String noteTitle = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_TITLE));
                String noteContent = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_CONTENT));
                String noteLabel = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_LABEL));
                String noteDate = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_DATE));
                NotesModel model = new NotesModel(noteID, noteTitle, noteContent, noteLabel, noteDate);
                resultList.add(model);
            }
        }
        return resultList;
    }

    public String getColumnById(long id, String selectionColumn) {
        if (!isNotesColumn(selectionColumn)) {
            return "Wrong column, please use NotesColumns class's strings";
        }

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selectionColumns = new String[] { selectionColumn };
        String selection = NotesColumns.COLUMN_ID.concat(" = ").concat("?");
        String[] selectionArgs = new String[] { String.valueOf(id) };
        qb.setTables(NotesColumns.TABLE_NAME);
        Cursor cursor = qb.query(db, selectionColumns, selection, selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0)
            return "No note inserted with this id ";

        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(selectionColumn));
    }

    private boolean isNotesColumn(String selectionColumn) {
        // Check if the column is a part of Notes table.
        return selectionColumn.equals(NotesColumns.COLUMN_ID) || selectionColumn.equals(NotesColumns.COLUMN_TITLE)
                || selectionColumn.equals(NotesColumns.COLUMN_CONTENT) || selectionColumn.equals(NotesColumns.COLUMN_DATE)
                || selectionColumn.equals(NotesColumns.COLUMN_LABEL);
    }

    public void deleteItem(long id) {
        String whereClause = NotesColumns.COLUMN_ID + " = " + id;
        getWritableDatabase().delete(NotesColumns.TABLE_NAME, whereClause, null);
    }

    public List<NotesModel> searchData(String toSearch) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Columns to select in the select statement.
        String[] selectedColumns = new String[] { NotesColumns.COLUMN_ID, NotesColumns.COLUMN_TITLE, NotesColumns.COLUMN_CONTENT
                , NotesColumns.COLUMN_LABEL, NotesColumns.COLUMN_DATE };
        // Select statement.
        String selection = NotesColumns.COLUMN_TITLE.concat(" LIKE ").concat("?")
                .concat(" OR ").concat(NotesColumns.COLUMN_CONTENT).concat(" LIKE").concat("?")
                .concat(" OR ").concat(NotesColumns.COLUMN_LABEL).concat(" LIKE").concat("?");
        // Specify the desired records, which is like %toSearch%
        String[] selectionArgs = new String[] { "%"+toSearch+"%", "%"+toSearch+"%", "%"+toSearch+"%"};
        qb.setTables(NotesColumns.TABLE_NAME);
        Cursor cursor =  qb.query(db, selectedColumns, selection, selectionArgs, null, null, null);
        List<NotesModel> resultList = new ArrayList<>();

        // convert cursor to List.
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                long noteID = Long.parseLong(cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_ID)));
                String noteTitle = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_TITLE));
                String noteContent = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_CONTENT));
                String noteLabel = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_LABEL));
                String noteDate = cursor.getString(cursor.getColumnIndex(NotesColumns.COLUMN_DATE));
                NotesModel model = new NotesModel(noteID, noteTitle, noteContent, noteLabel, noteDate);
                resultList.add(model);
            }
        }
        return resultList;
    }
}
