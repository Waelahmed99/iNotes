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

import asana.inotes.model.ChecklistsModel;
import asana.inotes.model.ListsModel;
import asana.inotes.model.NotesModel;

public class ChecklistsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Checklists.db";
    public static final int DATABASE_VERSION = 1;

    public class ChecklistsColumns implements BaseColumns {
        public static final String TABLE_NAME = "checklists";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
    }

    public class ListsColumns implements BaseColumns {
        public static final String TABLE_NAME = "lists";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CHECKLIST_ID = "checklist_id";
        public static final String COLUMN_IS_CHECKED = "is_checked";
    }

    private final String CREATE_TABLE_CHECKLIST = "CREATE TABLE " +
            ChecklistsColumns.TABLE_NAME + " (" +
            ChecklistsColumns.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            ChecklistsColumns.COLUMN_TITLE + " TEXT, " +
            ChecklistsColumns.COLUMN_DATE + " TEXT)";

    private final String CREATE_TABLE_LISTS = "CREATE TABLE " +
            ListsColumns.TABLE_NAME + " (" +
            ListsColumns.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            ListsColumns.COLUMN_CONTENT + " TEXT, " +
            ListsColumns.COLUMN_CHECKLIST_ID + " INTEGER, " +
            ListsColumns.COLUMN_IS_CHECKED + " TEXT, " +
            "FOREIGN KEY (" + ListsColumns.COLUMN_CHECKLIST_ID + ")" +
            "REFERENCES " + ChecklistsColumns.TABLE_NAME + " (" +
            ChecklistsColumns.COLUMN_ID + "))";


    public ChecklistsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHECKLIST);
        db.execSQL(CREATE_TABLE_LISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChecklistsColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ListsColumns.TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String title, List<ListsModel> list) {
        // Getting current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Last modified :' yyyy / MM / dd 'at' HH:mm");

        // Put data into ContentValues
        ContentValues values = new ContentValues();
        values.put(ChecklistsColumns.COLUMN_TITLE, title);
        values.put(ChecklistsColumns.COLUMN_DATE, dateFormat.format(calendar.getTime()));
        long id = getWritableDatabase().insert(ChecklistsColumns.TABLE_NAME, null, values);

        // Add list items with the same id of checklist.
        for (int i = 0; i < list.size(); i++) {
            ContentValues listValues = new ContentValues();
            ListsModel model = list.get(i);
            listValues.put(ListsColumns.COLUMN_CONTENT, model.getContent());
            listValues.put(ListsColumns.COLUMN_IS_CHECKED, model.isChecked());
            listValues.put(ListsColumns.COLUMN_CHECKLIST_ID, id);
            getWritableDatabase().insert(ListsColumns.TABLE_NAME, null, listValues);
        }
    }

    public List<ChecklistsModel> getInsertedData() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + ChecklistsColumns.TABLE_NAME, null);
        List<ChecklistsModel> resultList = new ArrayList<>();

        // Convert cursor to List
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Long checklistID = Long.parseLong(cursor.getString(cursor.getColumnIndex(ChecklistsColumns.COLUMN_ID)));
                String noteTitle = cursor.getString(cursor.getColumnIndex(ChecklistsColumns.COLUMN_TITLE));
                String noteDate = cursor.getString(cursor.getColumnIndex(NotesDbHelper.NotesColumns.COLUMN_DATE));
                List<ListsModel> listsModels = findListByID(checklistID);

                resultList.add(new ChecklistsModel(checklistID, noteTitle, listsModels, noteDate));
            }
        }
        return resultList;
    }

    public List<ListsModel> findListByID(long id) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // Select statement.
        String selection = ListsColumns.COLUMN_CHECKLIST_ID.concat(" = ").concat("?");
        // Selected columns.
        String[] selectionColumns = new String[] { ListsColumns.COLUMN_ID, ListsColumns.COLUMN_CONTENT,
                ListsColumns.COLUMN_IS_CHECKED, ListsColumns.COLUMN_CHECKLIST_ID};
        // Table to perform query in.
        qb.setTables(ListsColumns.TABLE_NAME);
        Cursor cursor = qb.query(db, selectionColumns, selection, new String[] { String.valueOf(id) }, null, null, null);

        // Convert cursor to List.
        List<ListsModel> resultList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(ListsColumns.COLUMN_CONTENT));
            String isChecked = cursor.getString(cursor.getColumnIndex(ListsColumns.COLUMN_IS_CHECKED));
            resultList.add(new ListsModel(content, isChecked));
        }
        return resultList;
    }

    public void deleteItem(long id) {
        getWritableDatabase().delete(ChecklistsColumns.TABLE_NAME, ChecklistsColumns.COLUMN_ID + " = " + id, null);
        getWritableDatabase().delete(ListsColumns.TABLE_NAME, ListsColumns.COLUMN_CHECKLIST_ID + " = " + id, null);
    }

    public List<ChecklistsModel> searchData(String toSearch) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // Select statement
        String selection = ChecklistsColumns.COLUMN_TITLE.concat(" LIKE ").concat("?");
        // Selected columns.
        String[] selectionColumns =  new String[] { ChecklistsColumns.COLUMN_ID,
                ChecklistsColumns.COLUMN_TITLE, ChecklistsColumns.COLUMN_DATE };
        // Table to perform query in.
        qb.setTables(ChecklistsColumns.TABLE_NAME);
        Cursor cursor = qb.query(db, selectionColumns, selection, new String[] {"%"+toSearch+"%"}, null, null, null);

        // Convert cursor to List.
        List<ChecklistsModel> resultList = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Long checklistID = Long.parseLong(cursor.getString(cursor.getColumnIndex(ChecklistsColumns.COLUMN_ID)));
                String noteTitle = cursor.getString(cursor.getColumnIndex(ChecklistsColumns.COLUMN_TITLE));
                String noteDate = cursor.getString(cursor.getColumnIndex(NotesDbHelper.NotesColumns.COLUMN_DATE));
                List<ListsModel> listsModels = findListByID(checklistID);

                resultList.add(new ChecklistsModel(checklistID, noteTitle, listsModels, noteDate));
            }
        }
        return resultList;
    }
}
