/**
 * QuestionsSQLiteDatabaseHelper
 * The QuestionsSQLiteDatabaseHelper is used to provide a storage method for the programming
 * questions the user has asked the chat bot. Methods for adding questions and deleting all are
 * available.
 *
 * References:
 *  https://www.youtube.com/watch?v=GAyvtK4cWLA&index=52&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl
 *
 * @author  Edward Dunn
 * @version 1.0
 */

package abertay.ac.uk.java_bot_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class QuestionsSQLiteDatabaseHelper extends SQLiteOpenHelper {
    // Initialise constants
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SQLiteLabDB";
    private static final String QUESTIONS_TABLE_NAME = "questions";
    private static final String[] COLUMN_NAMES = {"question", "solution"};

    // Construct CREATE query string
    private static final String QUESTIONS_TABLE_CREATE =
            "CREATE TABLE " + QUESTIONS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " TEXT, " +
                    COLUMN_NAMES[1] + " TEXT);";

    QuestionsSQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the database if it doesn't exist and adds the "questions" table
        // Execute SQL query
        db.execSQL(QUESTIONS_TABLE_CREATE);
    }

    /**
     * Method used to remove old table and recall onCreate to create a new database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QUESTIONS_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Method used to add a question to the questions database
     */
    public void addQuestion(Question q){

        try {
            ContentValues row = new ContentValues();
            // The first parameter is a column name, the second is a value.
            row.put(this.COLUMN_NAMES[0], q.getQuestion());
            row.put(this.COLUMN_NAMES[1], q.getSolution());

            // Get writable database and insert the new row to the "questions" table
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(QUESTIONS_TABLE_NAME, null, row);
            db.close();

        }catch(SQLiteException e){
            e.printStackTrace();
        }catch(Exception e){
            Log.e("Error adding question from database", e.toString());
        }

    }

    /**
     * Method used to retrieve all questions in the questions database
     */
    public ArrayList<Question> getQuestions(){

        try {
            // Get the readable database
            SQLiteDatabase db = this.getReadableDatabase();

            // Get all questions by querying the database
            Cursor result = db.query(QUESTIONS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

            // Convert results to a list of question objects
            ArrayList<Question> questions = new ArrayList<Question>();

            for (int i = 0; i < result.getCount(); i++) {
                result.moveToPosition(i);
                // Create a question object
                questions.add(new Question(result.getString(0), result.getString(1)));
            }

            return questions;

        }catch(SQLiteException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            Log.e("Error getting questions from database", e.toString());
            return null;
        }

    }

    /**
     * Method used to empty database
     */
    public void emptyDatabase(){

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(QUESTIONS_TABLE_NAME, null, null);
            db.close();
            
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        catch(Exception e){
            Log.e("Error emptying database", e.toString());
        }

    }

    public int questionCount(){
        try {
            String countQuery = "SELECT  * FROM " + QUESTIONS_TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;

        }catch(SQLiteException e){
            e.printStackTrace();
        }
        catch(Exception e){
            Log.e("Error emptying database", e.toString());
        }

        // Default to zero if error found
        return 0;

    }

}
