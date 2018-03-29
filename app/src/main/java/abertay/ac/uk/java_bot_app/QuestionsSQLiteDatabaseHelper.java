package abertay.ac.uk.java_bot_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Edward Dunn on 23/03/2018.
 */

public class QuestionsSQLiteDatabaseHelper extends SQLiteOpenHelper {
    /**
     * References:
     *  https://www.youtube.com/watch?v=GAyvtK4cWLA&index=52&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl
     */

    /* Initialise constants. */
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SQLiteLabDB";
    private static final String QUESTIONS_TABLE_NAME = "questions";
    private static final String[] COLUMN_NAMES = {"question", "solution"};
    /* Construct CREATE query string. */
    private static final String QUESTIONS_TABLE_CREATE =
            "CREATE TABLE " + QUESTIONS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " TEXT, " +
                    COLUMN_NAMES[1] + " TEXT);";

    QuestionsSQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the database if it doesn't exist and adds the "contacts" table.
        /* Execute SQL query. */
        db.execSQL(QUESTIONS_TABLE_CREATE);
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS_TABLE_NAME);
        onCreate(db);
        //db.close();
    }

    public void addQuestion(Question q){
        /* Pack contact details in ContentValues object for database insertion. */
        ContentValues row = new ContentValues();
        row.put(this.COLUMN_NAMES[0], q.getQuestion());
        row.put(this.COLUMN_NAMES[1], q.getSolution());
        // The first parameter is a column name, the second is a value.

        /* Get writable database and insert the new row to the "contacts" table. */
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(QUESTIONS_TABLE_NAME, null, row);
        db.close();
    }

    public ArrayList<Question> getQuestions(){

         /* Get the readable database. */
        SQLiteDatabase db = this.getReadableDatabase();

        /* Get all contacts by querying the database. */
        Cursor result = db.query(QUESTIONS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

        /* Convert results to a list of Contact objects. */
        ArrayList<Question> questions = new ArrayList<Question>();

        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);
            /* Create a Contact object with using data from name, email, phone columns. Add it to list. */
            questions.add(new Question(result.getString(0), result.getString(1)));
        }

        //db.close();
        return questions;
    }



    public void emptyDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE * FROM " + QUESTIONS_TABLE_NAME);
        db.close();
    }


    /*
    SQLiteDatabase sqLiteDatabase;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "questions.db";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "_productname";

    public QuestionsSQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_QUESTIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(sqLiteDatabase);
    }

    // Add a new row to the database
    public void addQuestion(Question question){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, question.get_productname());
        //SQLiteDatabase db = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_QUESTIONS, null, values);
        sqLiteDatabase.close();
    }

    // Delete from questions from database
    public void deleteQuestion(String productName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_QUESTIONS + " WHERE " + COLUMN_PRODUCT_NAME + "=\"" + productName + "\";" );
    }

    // Print out the database as a string
    public String databaseToString(){
        String dbString = "";
        //SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE 1";

        // Cursor point to a location in results
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        // Move to first row
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("_productname")) != null){
                dbString += c.getString(c.getColumnIndex("_productname"));
                dbString += "\n";
            }
        }
        sqLiteDatabase.close();
        return dbString;
    }
    */
}
