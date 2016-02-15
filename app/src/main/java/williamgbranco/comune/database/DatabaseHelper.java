package williamgbranco.comune.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.GregorianCalendar;

import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.institution.PublicInstitutionEducacao;
import williamgbranco.comune.institution.PublicInstitutionSaude;
import williamgbranco.comune.institution.PublicInstitutionSeguranca;
import williamgbranco.comune.manager.UserManager;
import williamgbranco.comune.report.Report;
import williamgbranco.comune.survey.Survey;
import williamgbranco.comune.survey.question.CheckboxQuestion;
import williamgbranco.comune.survey.question.Question;
import williamgbranco.comune.survey.question.RadioButtonQuestion;
import williamgbranco.comune.survey.question.RatingQuestion;
import williamgbranco.comune.survey.question.YesOrNoQuestion;
import williamgbranco.comune.survey.question.answer_option.AnswerOption;
import williamgbranco.comune.survey.question.answer_option.AnswerOptionWithStatus;
import williamgbranco.comune.user.User;
import williamgbranco.comune.util.Constants;

/**
 * Created by William on 04/08/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "comune.SurveyDBHelper";

    private static final String DB_NAME = "surveys.sqlite";
    private static final int DB_VERSION = 1;

    private static final String TABLE_USER = "user_info";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_FIRST_NAME = "first_name";
    private static final String COLUMN_USER_LAST_NAME = "last_name";
    private static final String COLUMN_USER_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_DATE_BIRTH = "date_of_birth";
    private static final String COLUMN_USER_PICTURE_ID = "picture_id";
    private static final String COLUMN_USER_PICTURE_URI = "picture_uri";
    //private static final String COLUMN_USER_INFO_SENT_TO_SERVER = "info_sent_to_server";
    //private static final String COLUMN_USER_PICTURE_SENT_TO_SERVER = "picture_sent_to_server";
    //private static final String COLUMN_USER_GENDER = "gender";
    //private static final String COLUMN_USER_FB_ID = "id_fb";
    //private static final String COLUMN_USER_LOGIN_TYPE = "login_type";
    //private static final String COLUMN_USER_PICTURE = "picture";

    private static final String TABLE_PLACE = "place";
    private static final String COLUMN_PLACE_ID = "_id";
    //private static final String COLUMN_PLACE_DEPT_ID = "id_department";
    private static final String COLUMN_PLACE_LAT = "latitude";
    private static final String COLUMN_PLACE_LNG = "longitude";
    private static final String COLUMN_PLACE_TYPE = "type";
    private static final String COLUMN_PLACE_NAME = "name";
    private static final String COLUMN_PLACE_ABBREVIATED_NAME = "abbreviated_name";
    private static final String COLUMN_PLACE_RATING = "rating";
    private static final String COLUMN_PLACE_STATUS = "status";
    //private static final String COLUMN_PLACE_OPENING_TIME = "opening_time";
    //private static final String COLUMN_PLACE_CLOSING_TIME = "closing_time";
    //private static final String COLUMN_PLACE_OPEN_DAYS = "open_days";

    private static final String TABLE_REPORT = "report";
    private static final String COLUMN_REPORT_ID = "_id";
    private static final String COLUMN_REPORT_ID_USER = "id_user";
    private static final String COLUMN_REPORT_ID_PLACE = "id_place";
    private static final String COLUMN_REPORT_COMMENT = "comment";
    private static final String COLUMN_REPORT_PICTURE_URI = "picture_uri";
    private static final String COLUMN_REPORT_FOOTAGE_URI = "footage_uri";
    private static final String COLUMN_REPORT_MADE_AT = "made_at";
    private static final String COLUMN_REPORT_SERVER_ID = "id_on_server";
    private static final String COLUMN_REPORT_COMMENT_SENT_TO_SERVER = "comment_sent_to_server";
    private static final String COLUMN_REPORT_PICTURE_SENT_TO_SERVER = "picture_sent_to_server";
    private static final String COLUMN_REPORT_FOOTAGE_SENT_TO_SERVER = "footage_sent_to_server";
    //private static final String COLUMN_REPORT_PICTURE_ID = "picture_id";
    //private static final String COLUMN_REPORT_FOOTAGE_ID = "footage_id";

    private static final String TABLE_SURVEY = "survey";
    private static final String COLUMN_SURVEY_ID = "_id";
    private static final String COLUMN_SURVEY_DESCRIPTION = "descr";
    private static final String COLUMN_SURVEY_AVAILABLE_SINCE = "available_since";

    private static final String TABLE_PLACE_SURVEY = "place_surveys";
    private static final String COLUMN_PLACE_SURVEY_ID_USER = "id_user";
    private static final String COLUMN_PLACE_SURVEY_ID_PLACE = "id_place";
    private static final String COLUMN_PLACE_SURVEY_ID_SURVEY = "id_survey";
    private static final String COLUMN_PLACE_SURVEY_COMPLETED = "completed";
    private static final String COLUMN_PLACE_SURVEY_START_TIME = "start_time";
    private static final String COLUMN_PLACE_SURVEY_COMPLETION_TIME = "completion_time";

    private static final String TABLE_PLACE_WORKING_DAYS = "place_working_days";
    private static final String COLUMN_PLACE_WORKING_DAYS_ID = "_id";
    private static final String COLUMN_PLACE_WORKING_DAYS_ID_PLACE = "id_place";
    private static final String COLUMN_PLACE_WORKING_DAYS_DAY_OF_WEEK = "day_of_week";

    private static final String TABLE_SURVEY_QUESTION = "survey_question";
    private static final String COLUMN_SURVEY_QUESTION_ID_SURVEY = "id_survey";
    private static final String COLUMN_SURVEY_QUESTION_ID_QUESTION = "id_question";

    private static final String TABLE_QUESTION = "question";
    private static final String COLUMN_QUESTION_ID = "_id";
    private static final String COLUMN_QUESTION_TYPE = "type";
    private static final String COLUMN_QUESTION_DESCRIPTION = "descr";
    private static final String COLUMN_QUESTION_MANDATORY = "mandatory";

    private static final String TABLE_YES_NO_QUESTION_ANSWER = "yes_no_question_answer";
    private static final String COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION = "id_question";
    private static final String COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY = "id_survey";
    private static final String COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE = "id_place";
    private static final String COLUMN_YES_NO_QUESTION_ANSWER_ID_USER = "id_user";
    private static final String COLUMN_YES_NO_QUESTION_USER_ANSWER = "user_answer";

    private static final String TABLE_RATING_QUESTION_ANSWER = "rating_question_answer";
    private static final String COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY = "id_survey";
    private static final String COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION = "id_question";
    private static final String COLUMN_RATING_QUESTION_ANSWER_ID_PLACE = "id_place";
    private static final String COLUMN_RATING_QUESTION_ANSWER_ID_USER = "id_user";
    private static final String COLUMN_RATING_QUESTION_USER_RATING = "user_rating";

    private static final String TABLE_ANSWER_OPTION = "answer_option";
    private static final String COLUMN_ANSWER_OPTION_ID = "_id";
    private static final String COLUMN_ANSWER_OPTION_DESCRIPTION = "header";

    private static final String TABLE_MULTIPLE_CHOICE_QUESTION = "question_answer_options";
    private static final String COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION = "id_question";
    private static final String COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION = "id_answer_option";

    private static final String TABLE_CHECKBOX_QUESTION_ANSWER = "checkbox_question_answer";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_CHECKED = "checked";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION = "id_answer_option";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION = "id_question";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY = "id_survey";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE = "id_inst";
    private static final String COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER = "id_user";

    private static final String TABLE_RADIOBUTTON_QUESTION_ANSWER = "radiobutton_question_answer";
    private static final String COLUMN_RADIOBUTTON_QUESTION_ANSWER = "answer";
    private static final String COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION = "id_question";
    private static final String COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY = "id_survey";
    private static final String COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE = "id_inst";
    private static final String COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER = "id_user";


    private Context mContext;
    //private User mUser;
    private UserManager mUserManager;
    //private Integer mUserId;


    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        mUserManager = UserManager.get(mContext);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Log.i(TAG, "Creating DB");

        //Create 'user' table
        String SQLCreateTableUser = "create table if not exists " + TABLE_USER +
                " (" +
                COLUMN_USER_ID + " integer" + ", " +
                COLUMN_USER_EMAIL + " varchar(50) not null" + ", " +
                COLUMN_USER_FIRST_NAME + " varchar(100) not null" + ", " + //not null
                COLUMN_USER_LAST_NAME + " varchar(100) not null" + ", " +
                COLUMN_USER_PHONE_NUMBER + " varchar(30) not null" + ", " +
                COLUMN_USER_DATE_BIRTH + " integer not null" + ", " +
                //COLUMN_USER_LOGGED_IN + " integer not null" + ", " +
                //COLUMN_USER_GENDER + " varchar(1)" + ", " +
                //COLUMN_USER_FB_ID + " integer" + ", " +
                //COLUMN_USER_LOGIN_TYPE + " integer not null" + ", " +
                //COLUMN_USER_PICTURE + " blob" + ", " +
                COLUMN_USER_PICTURE_ID + " integer" +  ", " +
                COLUMN_USER_PICTURE_URI + " varchar(250)" + ", " +
                //COLUMN_USER_INFO_SENT_TO_SERVER + " integer not null" + ", " +
                //COLUMN_USER_PICTURE_SENT_TO_SERVER + " integer not null" +
                "primary key (" +
                COLUMN_USER_ID +  ", " +
                COLUMN_USER_EMAIL + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableUser);
        db.execSQL(SQLCreateTableUser);

        //Create 'place' table
        String SQLCreateTableInstitution = "create table if not exists " + TABLE_PLACE +
                " (" +
                COLUMN_PLACE_ID + " integer primary key" + ", " +
                //COLUMN_PLACE_DEPT_ID + " integer" + ", " + //not null
                COLUMN_PLACE_TYPE + " integer not null" + ", " +
                COLUMN_PLACE_NAME + " varchar(300) not null" + ", " +
                COLUMN_PLACE_ABBREVIATED_NAME + " varchar(25)" + ", " +
                COLUMN_PLACE_RATING + " real not null" + ", " +
                COLUMN_PLACE_STATUS + " integer not null" + ", " +
                //COLUMN_PLACE_OPENING_TIME + " integer not null" + ", " +
                //COLUMN_PLACE_CLOSING_TIME + " integer not null" + ", " +
                //COLUMN_PLACE_OPEN_DAYS + " varchar(50)" + ", " + //not null
                COLUMN_PLACE_LAT + " real not null" + ", " +
                COLUMN_PLACE_LNG + " real not null" +
                ")";
        //Log.i(TAG, SQLCreateTableInstitution);
        db.execSQL(SQLCreateTableInstitution);

        //Create 'place_working_days' table
        String SQLCreateTablePlaceWorkingDays = "create table if not exists " + TABLE_PLACE_WORKING_DAYS +
                " (" +
                COLUMN_PLACE_WORKING_DAYS_ID + " integer primary key autoincrement" + ", " +
                COLUMN_PLACE_WORKING_DAYS_ID_PLACE + " integer not null" + ", " +
                COLUMN_PLACE_WORKING_DAYS_DAY_OF_WEEK + " integer not null" +
                ")";
        //Log.i(TAG, SQLCreateTablePlaceWorkingDays);
        db.execSQL(SQLCreateTablePlaceWorkingDays);

        //Create 'report' table
        String SQLCreateTableReport = "create table if not exists " + TABLE_REPORT +
                " (" +
                COLUMN_REPORT_ID + " integer primary key autoincrement" + ", " +
                COLUMN_REPORT_ID_USER + " integer not null" +  ", " + //references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_REPORT_ID_PLACE + " integer not null" + ", " + //" integer references " + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_REPORT_COMMENT + " varchar(500) not null" + ", " +
                //COLUMN_REPORT_PICTURE_ID + " integer" + ", " +
                COLUMN_REPORT_PICTURE_URI + " varchar(250)" + ", " +
                //COLUMN_REPORT_FOOTAGE_ID + " integer" + ", " +
                COLUMN_REPORT_FOOTAGE_URI + " varchar(250)" + ", " +
                COLUMN_REPORT_MADE_AT + " integer not null" + ", " +
                COLUMN_REPORT_SERVER_ID + " integer" + ", " +
                COLUMN_REPORT_COMMENT_SENT_TO_SERVER + " integer not null" + ", " +
                COLUMN_REPORT_PICTURE_SENT_TO_SERVER + " integer not null" + ", " +
                COLUMN_REPORT_FOOTAGE_SENT_TO_SERVER + " integer not null" +
                /*"primary key (" +
                COLUMN_REPORT_ID + ", " +
                COLUMN_REPORT_ID_USER + ", " +
                COLUMN_REPORT_ID_PLACE + ")" +*/
                ")";
        //Log.i(TAG, SQLCreateTableReport);
        db.execSQL(SQLCreateTableReport);

        //Create 'survey' table
        String SQLCreateTableSurvey = "create table if not exists " + TABLE_SURVEY +
                " (" +
                COLUMN_SURVEY_ID + " integer primary key" + ", " +
                COLUMN_SURVEY_DESCRIPTION + " varchar(300) not null" + ", " +
                COLUMN_SURVEY_AVAILABLE_SINCE + " integer not null" +
                ")";
        //Log.i(TAG, SQLCreateTableSurvey);
        db.execSQL(SQLCreateTableSurvey);

        //Create 'inst_survey' table
        String SQLCreateTableInstSurvey = "create table if not exists " + TABLE_PLACE_SURVEY +
                " (" +
                COLUMN_PLACE_SURVEY_ID_USER +" integer references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_PLACE_SURVEY_ID_PLACE + " integer not null" + ", " + // references" + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_PLACE_SURVEY_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_PLACE_SURVEY_COMPLETED + " integer not null" + ", " +
                COLUMN_PLACE_SURVEY_START_TIME + " integer not null" + ", " +
                COLUMN_PLACE_SURVEY_COMPLETION_TIME + " integer" + ", " +
                "primary key (" +
                COLUMN_PLACE_SURVEY_ID_USER + ", " +
                COLUMN_PLACE_SURVEY_ID_PLACE + ", " +
                COLUMN_PLACE_SURVEY_ID_SURVEY + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableInstSurvey);
        db.execSQL(SQLCreateTableInstSurvey);

        //Create 'question' table
        String SQLCreateTableQuestion = "create table if not exists " + TABLE_QUESTION +
                " (" +
                COLUMN_QUESTION_ID + " integer primary key" + ", " +
                COLUMN_QUESTION_TYPE + " integer not null" + ", " +
                COLUMN_QUESTION_DESCRIPTION + " varchar(300) not null" + ", " +
                COLUMN_QUESTION_MANDATORY + " integer" +
                ")";
        //Log.i(TAG, SQLCreateTableQuestion);
        db.execSQL(SQLCreateTableQuestion);


        //Create 'survey_questions' table
        String SQLCreateTableSurveyQuestion = "create table if not exists " + TABLE_SURVEY_QUESTION +
                " (" +
                COLUMN_SURVEY_QUESTION_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_SURVEY_QUESTION_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                "primary key (" +
                COLUMN_SURVEY_QUESTION_ID_SURVEY + ", " +
                COLUMN_SURVEY_QUESTION_ID_QUESTION + ")" +
        ")";
        //Log.i(TAG, SQLCreateTableSurveyQuestion);
        db.execSQL(SQLCreateTableSurveyQuestion);


        //Create 'rating_question' table
        String SQLCreateTableRatingQuestionAnswer = "create table if not exists " + TABLE_RATING_QUESTION_ANSWER +
                " (" +
                COLUMN_RATING_QUESTION_ANSWER_ID_PLACE + " integer not null" + ", " + // references + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_USER + " integer references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                COLUMN_RATING_QUESTION_USER_RATING + " real" + ", " +
                "primary key (" +
                COLUMN_RATING_QUESTION_ANSWER_ID_USER + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_PLACE + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY  + ", " +
                COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableRatingQuestionAnswer);
        db.execSQL(SQLCreateTableRatingQuestionAnswer);


        //Create 'yes_no_question' table
        String SQLCreateTableYesNoQuestionAnswer = "create table if not exists " + TABLE_YES_NO_QUESTION_ANSWER +
                " (" +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE + " integer not null" + ", " + // references + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_USER + " integer references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                COLUMN_YES_NO_QUESTION_USER_ANSWER + " integer" + ", " +
                "primary key (" +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_USER +  ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE + ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY  + ", " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableYesNoQuestionAnswer);
        db.execSQL(SQLCreateTableYesNoQuestionAnswer);


        //Create 'answer_option' table
        String SQLCreateAnswerOptionTable = "create table if not exists " + TABLE_ANSWER_OPTION +
                " (" +
                COLUMN_ANSWER_OPTION_ID + " integer primary key" + ", " +
                COLUMN_ANSWER_OPTION_DESCRIPTION + " varchar(300) not null" +
                ")";
        //Log.i(TAG, SQLCreateAnswerOptionTable);
        db.execSQL(SQLCreateAnswerOptionTable);


        //Create 'multiple_choice_question' table
        String SQLCreateTableMultipleChoiceQuestion = "create table if not exists " + TABLE_MULTIPLE_CHOICE_QUESTION +
                " (" +
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION + " integer references " + TABLE_ANSWER_OPTION + "(" + COLUMN_ANSWER_OPTION_ID + ")" + ", " +
                "primary key (" +
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION + ", " +
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableMultipleChoiceQuestion);
        db.execSQL(SQLCreateTableMultipleChoiceQuestion);


        //Create 'radiobutton_question_answer' table
        String SQLCreateTableRadioButtonQuestionAnswer = "create table if not exists " + TABLE_RADIOBUTTON_QUESTION_ANSWER +
                " (" +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER + " integer references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE + " integer not null" + ", " + // references + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER + " integer references " + TABLE_ANSWER_OPTION + "(" + COLUMN_ANSWER_OPTION_ID + ")" + ", " +
                "primary key (" +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY + ", " +
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableRadioButtonQuestionAnswer);
        db.execSQL(SQLCreateTableRadioButtonQuestionAnswer);

        //Create 'checkbox_question_answer' table
        String SQLCreateTableCheckBoxQuestionAnswer = "create table if not exists " + TABLE_CHECKBOX_QUESTION_ANSWER +
                " (" +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER + " integer references " + TABLE_USER + "(" + COLUMN_USER_ID + ")" + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE + " integer not null" + ", " + // references + TABLE_PLACE + "(" + COLUMN_PLACE_ID + ")" + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY + " integer references " + TABLE_SURVEY + "(" + COLUMN_SURVEY_ID + ")" + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION + " integer references " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + ")" + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION + " integer references " + TABLE_ANSWER_OPTION + "(" + COLUMN_ANSWER_OPTION_ID + ")" + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_CHECKED + " integer" + ", " +
                "primary key (" +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION + ", " +
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION + ")" +
                ")";
        //Log.i(TAG, SQLCreateTableCheckBoxQuestionAnswer);
        db.execSQL(SQLCreateTableCheckBoxQuestionAnswer);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Log.i(TAG, "Upgrading database. Existing content will be lost. [" + oldVersion + "] -> [" + newVersion + "]");

        db.execSQL("drop table if exists " + TABLE_ANSWER_OPTION);
        db.execSQL("drop table if exists " + TABLE_CHECKBOX_QUESTION_ANSWER);
        db.execSQL("drop table if exists " + TABLE_RADIOBUTTON_QUESTION_ANSWER);
        db.execSQL("drop table if exists " + TABLE_MULTIPLE_CHOICE_QUESTION);
        db.execSQL("drop table if exists " + TABLE_RATING_QUESTION_ANSWER);
        db.execSQL("drop table if exists " + TABLE_YES_NO_QUESTION_ANSWER);
        db.execSQL("drop table if exists " + TABLE_QUESTION);
        db.execSQL("drop table if exists " + TABLE_SURVEY_QUESTION);
        db.execSQL("drop table if exists " + TABLE_PLACE_SURVEY);
        db.execSQL("drop table if exists " + TABLE_PLACE);
        db.execSQL("drop table if exists " + TABLE_SURVEY);
        db.execSQL("drop table if exists " + TABLE_REPORT);
        db.execSQL("drop table if exists " + TABLE_USER);

        onCreate(db);
    }


    /*********************** Operações de INSERT ***********************/

    private void insertYesNoQuestionAnswer(YesOrNoQuestion pQuestion, Integer pInstId)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_YES_NO_QUESTION_ANSWER_ID_USER, mUser.getUserId());
        cv.put(COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE, pInstId);
        cv.put(COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY, pQuestion.getSurvey().getId());
        cv.put(COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION, pQuestion.getId());
        cv.put(COLUMN_YES_NO_QUESTION_USER_ANSWER, pQuestion.getUserAnswer());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_YES_NO_QUESTION_ANSWER, null, cv);
        db.close();

        Log.i(TAG, "insertYesNoQuestionAnswer: " + cv.toString());
    }

    private void insertRatingQuestionAnswer(RatingQuestion pQuestion, Integer pInstId)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RATING_QUESTION_ANSWER_ID_USER, mUser.getUserId());
        cv.put(COLUMN_RATING_QUESTION_ANSWER_ID_PLACE, pInstId);
        cv.put(COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY, pQuestion.getSurvey().getId());
        cv.put(COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION, pQuestion.getId());
        cv.put(COLUMN_RATING_QUESTION_USER_RATING, pQuestion.getUserRating());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_RATING_QUESTION_ANSWER, null, cv);
        db.close();

        Log.i(TAG, "insertRatingQuestionAnswer: " + cv.toString());
    }

    private void insertAnswerOption(AnswerOption pAnswerOption)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ANSWER_OPTION_ID, pAnswerOption.getId());
        cv.put(COLUMN_ANSWER_OPTION_DESCRIPTION, pAnswerOption.getAnswerText());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ANSWER_OPTION, null, cv);
        db.close();

        //Log.i(TAG, "insertAnswerOption: " + cv.toString());
    }

    private void insertMultipleChoiceQuestion(Question pQuestion, AnswerOption pAnswerOption)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION, pQuestion.getId());
        cv.put(COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION, pAnswerOption.getId());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MULTIPLE_CHOICE_QUESTION, null, cv);
        db.close();

        //Log.i(TAG, "insertMultipleChoiceQuestion: " + cv.toString());
    }

    private void insertCheckboxQuestionAnswer(CheckboxQuestion pQuestion, Integer pInstId)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();

        for (AnswerOptionWithStatus answerOption : pQuestion.getAnswerOptions())
        {
            insertAnswerOption(answerOption);

            insertMultipleChoiceQuestion(pQuestion, answerOption);

            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER, mUser.getUserId());
            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE, pInstId);
            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY, pQuestion.getSurvey().getId());
            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION, pQuestion.getId());
            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION, answerOption.getId());
            cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_CHECKED, answerOption.isChecked() ? 1 : 0);
            SQLiteDatabase db = getWritableDatabase();
            db.insert(TABLE_CHECKBOX_QUESTION_ANSWER, null, cv);
            db.close();

            Log.i(TAG, "insertCheckboxQuestionAnswer: " + cv.toString());
        }
    }

    private void insertRadioButtonQuestionAnswer(RadioButtonQuestion pQuestion, Integer pInstId)
    {
        User mUser = mUserManager.getCurrentUser();

        for (AnswerOption answerOption : pQuestion.getAnswerOptions())
        {
            insertAnswerOption(answerOption);

            insertMultipleChoiceQuestion(pQuestion, answerOption);
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER, mUser.getUserId());
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE, pInstId);
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY, pQuestion.getSurvey().getId());
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION, pQuestion.getId());
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER, -1);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_RADIOBUTTON_QUESTION_ANSWER, null, cv);
        db.close();

        Log.i(TAG, "insertRadioButtonQuestionAnswer: " + cv.toString());
    }

    private void insertQuestion(Question pQuestion, Integer pInstId)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION_ID, pQuestion.getId());
        cv.put(COLUMN_QUESTION_TYPE, pQuestion.getType());
        cv.put(COLUMN_QUESTION_DESCRIPTION, pQuestion.getDescription());
        cv.put(COLUMN_QUESTION_MANDATORY, pQuestion.isMandatory() ? 1 : 0);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_QUESTION, null, cv);
        db.close();

        //Log.i(TAG, "insertQuestion: " + cv.toString());

        if (pQuestion instanceof YesOrNoQuestion)
        {
            insertYesNoQuestionAnswer((YesOrNoQuestion) pQuestion, pInstId);
        }
        else if (pQuestion instanceof RatingQuestion)
        {
            insertRatingQuestionAnswer((RatingQuestion) pQuestion, pInstId);
        }
        else if (pQuestion instanceof RadioButtonQuestion)
        {
            insertRadioButtonQuestionAnswer((RadioButtonQuestion) pQuestion, pInstId);
        }
        else if (pQuestion instanceof CheckboxQuestion)
        {
            insertCheckboxQuestionAnswer((CheckboxQuestion) pQuestion, pInstId);
        }
    }

    private void insertSurveyQuestion(Integer surveyID, Integer questionID)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SURVEY_QUESTION_ID_SURVEY, surveyID);
        cv.put(COLUMN_SURVEY_QUESTION_ID_QUESTION, questionID);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SURVEY_QUESTION, null, cv);
        db.close();
    }

    public void insertInstSurvey(Survey pSurvey)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLACE_SURVEY_ID_SURVEY, pSurvey.getId());
        cv.put(COLUMN_PLACE_SURVEY_ID_PLACE, pSurvey.getPublicInstitutionId());
        cv.put(COLUMN_PLACE_SURVEY_ID_USER, mUser.getUserId());
        cv.put(COLUMN_PLACE_SURVEY_START_TIME, pSurvey.getStartTime().getTimeInMillis());
        cv.put(COLUMN_PLACE_SURVEY_COMPLETED, 0);
        Log.i(TAG, "insertInstSurvey: " + cv.toString());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PLACE_SURVEY, null, cv);
        db.close();
    }

    public void insertSurvey(Survey pSurvey)
    {
        //Log.i(TAG, "Inserting survey " + pSurvey.getId() + " in DB.");

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SURVEY_ID, pSurvey.getId());
        cv.put(COLUMN_SURVEY_DESCRIPTION, pSurvey.getDescription());
        cv.put(COLUMN_SURVEY_AVAILABLE_SINCE, pSurvey.getAvailableSince().getTimeInMillis());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SURVEY, null, cv);
        db.close();
    }

    public void insertSurveyForInstitution(Survey pSurvey)
    {
        insertSurvey(pSurvey);

        insertInstSurvey(pSurvey);

        for (Question question : pSurvey.getQuestions())
        {
            insertQuestion(question, pSurvey.getPublicInstitutionId());
            insertSurveyQuestion(pSurvey.getId(), question.getId());
        }

        //Log.i(TAG, "Survey " + pSurvey.getId() + " inserted for institution " + pSurvey.getPublicInstitutionId());
    }

    public long insertReport(Report pReport)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REPORT_ID_USER, pReport.getUserId());
        cv.put(COLUMN_REPORT_ID_PLACE, pReport.getPublicInstitutionId());
        cv.put(COLUMN_REPORT_COMMENT, pReport.getComment());
        //cv.put(COLUMN_REPORT_PICTURE_ID, pReport.getPictureId());

        if (pReport.getPictureUri() != null)
            cv.put(COLUMN_REPORT_PICTURE_URI, pReport.getPictureUri().toString());

        //cv.put(COLUMN_REPORT_FOOTAGE_ID, pReport.getFootageId());

        if (pReport.getFootageUri() != null)
            cv.put(COLUMN_REPORT_FOOTAGE_URI, pReport.getFootageUri().toString());

        cv.put(COLUMN_REPORT_MADE_AT, pReport.madeAt().getTimeInMillis());
        cv.put(COLUMN_REPORT_SERVER_ID, -1);
        cv.put(COLUMN_REPORT_COMMENT_SENT_TO_SERVER, (pReport.isCommentSentToServer())?1:0);
        cv.put(COLUMN_REPORT_PICTURE_SENT_TO_SERVER, (pReport.isPictureSentToServer())?1:0);
        cv.put(COLUMN_REPORT_FOOTAGE_SENT_TO_SERVER, (pReport.isFootageSentToServer()) ? 1 : 0);

        Log.i(TAG, "insertReport: " + cv.toString());
        SQLiteDatabase db = getWritableDatabase();
        long reportId = db.insert(TABLE_REPORT, null, cv);
        db.close();

        return reportId;
    }

    public long insertUser(User pUser)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID, pUser.getUserId());
        cv.put(COLUMN_USER_EMAIL, pUser.getEmail());
        cv.put(COLUMN_USER_FIRST_NAME, pUser.getFirstName());
        cv.put(COLUMN_USER_LAST_NAME, pUser.getLastName());
        cv.put(COLUMN_USER_PHONE_NUMBER, pUser.getPhoneNumber());
        cv.put(COLUMN_USER_DATE_BIRTH, pUser.getDateOfBirth().getTimeInMillis());
        //cv.put(COLUMN_USER_GENDER, pUser.getGender());
        //cv.put(COLUMN_USER_FB_ID, pUser.getFbUserId());
        //cv.put(COLUMN_USER_LOGIN_TYPE, pUser.getLoginType());
        //cv.put(COLUMN_USER_LOGGED_IN, 1);
        //cv.put(COLUMN_USER_PICTURE, pUser.getPicture());
        //if (pUser.getPictureId() != -1)
        cv.put(COLUMN_USER_PICTURE_ID, pUser.getPictureId());
        if (pUser.getPictureUri() != null)
            cv.put(COLUMN_USER_PICTURE_URI, pUser.getPictureUri().toString());
        //cv.put(COLUMN_USER_INFO_SENT_TO_SERVER, (pUser.isInfoSentToServer())?1:0);
        //cv.put(COLUMN_USER_PICTURE_SENT_TO_SERVER, (pUser.isPictureSentToServer())?1:0);

        Log.i(TAG, "insertUser: " + cv.toString());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_USER, null, cv);
        db.close();

        return id;
    }

    public long insertInstitution(PublicInstitution pInstitution)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLACE_ID, pInstitution.getId());
        cv.put(COLUMN_PLACE_NAME, pInstitution.getNome());
        cv.put(COLUMN_PLACE_ABBREVIATED_NAME, pInstitution.getNomeAbreviado());
        cv.put(COLUMN_PLACE_TYPE, pInstitution.getTipo());
        cv.put(COLUMN_PLACE_RATING, pInstitution.getNotaMedia());
        cv.put(COLUMN_PLACE_STATUS, pInstitution.getStatus());
        //cv.put(COLUMN_PLACE_DEPT_ID, pInstitution.getmDepartmentId());
        cv.put(COLUMN_PLACE_LAT, pInstitution.getLatLng().latitude);
        cv.put(COLUMN_PLACE_LNG, pInstitution.getLatLng().longitude);
        //cv.put(COLUMN_PLACE_OPENING_TIME, pInstitution.getOpeningTimeOfDay());
        //cv.put(COLUMN_PLACE_CLOSING_TIME, pInstitution.getClosingTimeOfDay());
        //cv.put(COLUMN_PLACE_OPEN_DAYS, pInstitution.getWorkingDays());

        Log.i(TAG, "insertInstitution: " + cv.toString());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_PLACE, null, cv);
        db.close();

        return id;
    }


    /*********************** Operações de SELECT ***********************/

    //Busca todas as pesquisas do banco
    /*public SurveyCursor querySurveys()
    {
        //select * from TABLE_SURVEY
        Cursor wrapped = getReadableDatabase().query(TABLE_SURVEY, null, null, null, null, null, null);
        return new SurveyCursor(wrapped);
    }*/

    public UserCursor queryUserById(Integer userID)
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_USER,
                null, //Todas as colunas
                COLUMN_USER_ID + " = ?", //Procura por um ID
                new String[]{String.valueOf(userID)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new UserCursor(wrapped);
    }

    public PublicInstitutionCursor queryInstitutionById(Integer instId)
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE,
                null, //Todas as colunas
                COLUMN_PLACE_ID + " = ?", //Procura por um ID
                new String[]{String.valueOf(instId)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new PublicInstitutionCursor(wrapped, mContext);
    }

    public InstitutionSurveyCursor querySurveysForInstitutionForAnyUser(Integer instId)
    {
        Cursor wrapper = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_PLACE + " = ?",
                new String[]{String.valueOf(instId)},
                null,
                null,
                null);

        return new InstitutionSurveyCursor(wrapper);
    }

    public InstitutionSurveyCursor querySurveysForUser(Integer userId)
    {
        Cursor wrapper = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_USER + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null);

        return new InstitutionSurveyCursor(wrapper);
    }

    public ReportCursor queryReportsForInstitutionForAnyUser(Integer instId)
    {
        Cursor wrapper = getReadableDatabase().query(TABLE_REPORT,
                null,
                COLUMN_REPORT_ID_PLACE + " = ?",
                new String[]{String.valueOf(instId)},
                null,
                null,
                null);

        return new ReportCursor(wrapper, mContext);
    }

    public ReportCursor queryReportsForUser(Integer userId)
    {
        Cursor wrapper = getReadableDatabase().query(TABLE_REPORT,
                null,
                COLUMN_REPORT_ID_USER + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null);

        return new ReportCursor(wrapper, mContext);
    }

    //public UserCursor queryUserByLoginInfo(String email,String password)
    public UserCursor queryUserByLoginInfo(String email)
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_USER,
                null, //Todas as colunas
                COLUMN_USER_EMAIL + " = ?", //+ " AND " +
                        //COLUMN_USER_PASSWORD + " = ?", //Procura por um ID
                new String[]{String.valueOf(email)}, //, String.valueOf(password) //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new UserCursor(wrapped);
    }

    public InstitutionSurveyCursor queryUsesForSurvey(Integer surveyId)
    {
        Cursor wrapper = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?",
                new String[]{String.valueOf(surveyId)},
                null,
                null,
                null);

        return new InstitutionSurveyCursor(wrapper);
    }

    public SurveyCursor querySurveyById(Integer surveyID)
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_SURVEY,
                null, //Todas as colunas
                COLUMN_SURVEY_ID + " = ?", //Procura por um ID
                new String[]{String.valueOf(surveyID)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new SurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryInstSurveyById(Integer userId, Integer instID, Integer surveyID)
    {
        //User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null, //Todas as colunas
                COLUMN_PLACE_SURVEY_ID_USER + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_ID_PLACE + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?", //Procura por um ID
                new String[]{String.valueOf(userId), String.valueOf(instID), String.valueOf(surveyID)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor querySurveysForInstitution(Integer instId)
    {
        /*GregorianCalendar limite = new GregorianCalendar();
        limite.add(Calendar.DATE, 0 - Constants.SURVEY_MAX_DAYS_AVAILABLE);

        long limiteInMillis = limite.getTimeInMillis();*/

        User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_USER + " = ? AND " +
                        COLUMN_PLACE_SURVEY_ID_PLACE + " = ?", // + " AND " +
                        //COLUMN_PLACE_SURVEY_COMPLETED + " = ?" + " AND " +
                        //COLUMN_PLACE_SURVEY_START_TIME + " >= ?", //Procura por um ID
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instId)}, //, String.valueOf(0), String.valueOf(limiteInMillis) //com esse valor
                null, //group by
                null, //having
                COLUMN_PLACE_SURVEY_START_TIME + " asc"); //order by start time (pesquisas mais antigas primeiro)
        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryInstitutionsWithIncompleteSurveysForCurrentUser()
    {
        GregorianCalendar limite = new GregorianCalendar();
        limite.add(Calendar.DATE, 0 - Constants.SURVEY_MAX_DAYS_AVAILABLE);
        long limiteInMillis = limite.getTimeInMillis();

        User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(
                true, //distinct
                TABLE_PLACE_SURVEY,
                new String[]{COLUMN_PLACE_SURVEY_ID_PLACE},
                COLUMN_PLACE_SURVEY_ID_USER + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_COMPLETED + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_START_TIME + " >= ?", //Procura por um ID
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(0), String.valueOf(limiteInMillis)}, //com esses valores
                COLUMN_PLACE_SURVEY_ID_PLACE, //group by
                null, //having
                COLUMN_PLACE_SURVEY_START_TIME + " asc", //order by start time (pesquisas mais antigas primeiro)
                null);

        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryCompleteSurveys()
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_COMPLETED + " = ?",
                new String[]{String.valueOf(1)}, //com esse valor
                null, //group by
                null, //having
                null); //order by
        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryIncompleteSurveysForUser(int userId)
    {
        //User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_USER + " = ?" + " AND " +
                COLUMN_PLACE_SURVEY_COMPLETED + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(0)}, //com esse valor
                null, //group by
                null, //having
                null); //order by
        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryExpiredSurveys()
    {
        GregorianCalendar limite = new GregorianCalendar();
        limite.add(Calendar.DATE, 0 - Constants.SURVEY_MAX_DAYS_AVAILABLE);

        long limiteInMillis = limite.getTimeInMillis();

        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_START_TIME + " < ?",
                new String[]{String.valueOf(limiteInMillis)}, //com esse valor
                null, //group by
                null, //having
                null); //order by
        return new InstitutionSurveyCursor(wrapped);
    }

    public InstitutionSurveyCursor queryExpiringSoonSurveys(int userId)
    {
        //User mUser = mUserManager.getCurrentUser();

        GregorianCalendar expiration = new GregorianCalendar();
        expiration.add(Calendar.DATE, 0 - Constants.SURVEY_MAX_DAYS_AVAILABLE);
        long expirationInMillis = expiration.getTimeInMillis();

        GregorianCalendar expirationSoon = (GregorianCalendar) expiration.clone();
        expirationSoon.add(Calendar.HOUR, Constants.SURVEY_ALERT_FEW_HOURS_REMAINING);
        long expirationSoonInMillis = expirationSoon.getTimeInMillis();

        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE_SURVEY,
                null,
                COLUMN_PLACE_SURVEY_ID_USER + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_START_TIME + " <= ?" + " AND " +
                        COLUMN_PLACE_SURVEY_START_TIME + " > ?",
                new String[]{String.valueOf(userId), String.valueOf(expirationSoonInMillis), String.valueOf(expirationInMillis)}, //com esse valor
                null, //group by
                null, //having
                null); //order by
        return new InstitutionSurveyCursor(wrapped);
    }

    public YesOrNoAnswerCursor queryUserAnswerForYesOrNoQuestion(Integer userId, Integer instID, Integer surveyID, Integer questionID)
    {
        //select COLUMN_YES_NO_QUESTION_USER_ANSWER from TABLE_YES_NO_QUESTION where COLUMN_YES_NO_QUESTION_ID = questionID
        //
        //User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_YES_NO_QUESTION_ANSWER,
                new String[]{COLUMN_YES_NO_QUESTION_USER_ANSWER},
                COLUMN_YES_NO_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)},
                null,
                null,
                null,
                "1");
        return new YesOrNoAnswerCursor(wrapped);
    }

    public RatingAnswerCursor queryUserRatingForRatingQuestion(Integer userId, Integer instID, Integer surveyID, Integer questionID)
    {
        //User mUser = mUserManager.getCurrentUser();

        //select COLUMN_RATING_QUESTION_USER_RATING from TABLE_RATING_QUESTION where COLUMN_RATING_QUESTION_ID = questionID
        Cursor wrapped = getReadableDatabase().query(TABLE_RATING_QUESTION_ANSWER,
                new String[]{COLUMN_RATING_QUESTION_USER_RATING},
                COLUMN_RATING_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)},
                null,
                null,
                null,
                "1");

        return new RatingAnswerCursor(wrapped);
    }

    public QuestionCursor queryQuestionById(Integer questionID)
    {
        //select * from TABLE_QUESTION where COLUMN_QUESTION_ID = questionID
        Cursor wrapped = getReadableDatabase().query(TABLE_QUESTION,
                null, //Todas as colunas
                COLUMN_QUESTION_ID + " = ?", //Procura por um ID
                new String[]{String.valueOf(questionID)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new QuestionCursor(wrapped, mContext);
    }

    public SurveyQuestionCursor queryQuestionsIdsForSurvey(Integer surveyId)
    {
        //select COLUMN_SURVEY_QUESTION_ID_QUESTION from TABLE_SURVEY_QUESTION where COLUMN_SURVEY_QUESTION_ID_SURVEY = surveyID
        Cursor wrapped = getReadableDatabase().query(TABLE_SURVEY_QUESTION,
                new String[]{COLUMN_SURVEY_QUESTION_ID_QUESTION}, //Somente os IDs das quest�es
                COLUMN_SURVEY_QUESTION_ID_SURVEY + " = ?", //Procura por um ID
                new String[]{String.valueOf(surveyId)}, //com esse valor
                null, //group by
                null, //having
                null); //order by

        return new SurveyQuestionCursor(wrapped);
    }

    public SurveyQuestionCursor queryUsesForQuestions(Integer questionId)
    {
        Cursor wrapped = getReadableDatabase().query(TABLE_SURVEY_QUESTION,
                new String[]{COLUMN_SURVEY_QUESTION_ID_SURVEY}, //Somente os IDs das pesquisas
                COLUMN_SURVEY_QUESTION_ID_QUESTION + " = ?", //Procura por um ID
                new String[]{String.valueOf(questionId)}, //com esse valor
                null, //group by
                null, //having
                null); //order by

        return new SurveyQuestionCursor(wrapped);
    }

    public AnswerOptionCursor queryAnswerOptionById(Integer answerOptionID)
    {
        //Log.i(TAG, "queryAnswerOptionById: answer_option id = " + answerOptionID);
        //select * from TABLE_QUESTION where COLUMN_QUESTION_ID = questionID
        Cursor wrapped = getReadableDatabase().query(TABLE_ANSWER_OPTION,
                null, //Todas as colunas
                COLUMN_ANSWER_OPTION_ID + " = ?", //Procura por um ID
                new String[]{String.valueOf(answerOptionID)}, //com esse valor
                null, //group by
                null, //having
                null, //order by
                "1"); //limita a 1 linha
        return new AnswerOptionCursor(wrapped, mContext);
    }

    public RadioButtonQuestionAnswerCursor queryRadioButtonQuestionAnswerById(Integer userId, Integer instID, Integer surveyID, Integer questionID)
    {
        //Log.i(TAG, "queryRadioButtonQuestionAnswerById: question id = " + questionID);

        //User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_RADIOBUTTON_QUESTION_ANSWER,
                null, //Todas as colunas
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION + " = ?", //Procura por um ID
                new String[]{String.valueOf(userId), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)},
                null, //group by
                null, //having
                null, //order by
                "1"); //limit
        return new RadioButtonQuestionAnswerCursor(wrapped);
    }

    public CheckboxQuestionAnswerCursor queryCheckboxQuestionAnswerById(Integer userId, Integer instID, Integer surveyID, Integer questionID, Integer answerOptionId)
    {
        //Log.i(TAG, "queryCheckboxQuestionAnswerById: question id = " + questionID);
        //User mUser = mUserManager.getCurrentUser();

        Cursor wrapped = getReadableDatabase().query(TABLE_CHECKBOX_QUESTION_ANSWER,
                null, //Todas as colunas
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION + " = ?", //Procura por um ID
                new String[]{String.valueOf(userId), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID), String.valueOf(answerOptionId)},
                null, //group by
                null, //having
                null, //order by
                "1"); //limit
        return new CheckboxQuestionAnswerCursor(wrapped);
    }

    public MultipleChoiceQuestionCursor queryMultipleChoiceQuestion(Integer questionID)
    {
        //Log.i(TAG, "queryMultipleChoiceQuestion: question id = " + questionID);

        //select * from TABLE_MULTIPLE_CHOICE_QUESTION where COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION = questionID
        Cursor wrapped = getReadableDatabase().query(TABLE_MULTIPLE_CHOICE_QUESTION,
                null, //Todas as colunas
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION + " = ?", //Procura por um ID
                new String[]{String.valueOf(questionID)},
                null, //group by
                null, //having
                null); //order by
        return new MultipleChoiceQuestionCursor(wrapped);
    }

    public MultipleChoiceQuestionCursor queryUsesForAnswerOption(Integer answerId)
    {
        //Log.i(TAG, "getUsesForAnswerOption: answer id = " + answerId);

        //select * from TABLE_MULTIPLE_CHOICE_QUESTION where COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION = questionID
        Cursor wrapped = getReadableDatabase().query(TABLE_MULTIPLE_CHOICE_QUESTION,
                null, //Todas as colunas
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION + " = ?", //Procura por um ID
                new String[]{String.valueOf(answerId)},
                null, //group by
                null, //having
                null); //order by
        return new MultipleChoiceQuestionCursor(wrapped);
    }


    public ReportCursor queryReportsMade()
    {
        // select * from TABLE_REPORT
        Cursor wrapped = getReadableDatabase().query(TABLE_REPORT, null, null, null, null, null, null);
        return new ReportCursor(wrapped, mContext);
    }


    /*********************** Operações de UPDATE ***********************/
    public int updateUserPhotoId(User user)
    {
        int result = -1;

        if ((user != null) && (user.getPictureId() != -1))
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USER_PICTURE_ID, user.getPictureId());

            result = db.update(
                    TABLE_USER,
                    cv,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())}
            );
            db.close();
        }
        return result;
    }

    public int updateUserPhotoUri(User user)
    {
        int result = -1;

        if ((user != null) && (user.getPictureUri() != null))
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USER_PICTURE_URI, user.getPictureUri().toString());

            result = db.update(
                    TABLE_USER,
                    cv,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())}
            );
            db.close();
        }
        return result;
    }

    /*public int updateUserPhotoServerStatus(User user)
    {
        int result = -1;

        if (user != null)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USER_PICTURE_SENT_TO_SERVER, (user.isPictureSentToServer())?1:0);

            result = db.update(
                    TABLE_USER,
                    cv,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())}
            );
            db.close();
        }
        return result;
    }

    public int updateUserInfoServerStatus(User user)
    {
        int result = -1;

        if (user != null)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_USER_INFO_SENT_TO_SERVER, (user.isInfoSentToServer())?1:0);

            result = db.update(
                    TABLE_USER,
                    cv,
                    COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())}
            );
            db.close();
        }
        return result;
    }*/

    public int updateReportCommentServerStatus(Report report)
    {
        int result = -1;

        if (report != null)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_REPORT_SERVER_ID, report.getIdOnServer());
            cv.put(COLUMN_REPORT_COMMENT_SENT_TO_SERVER, (report.isCommentSentToServer())?1:0);

            result = db.update(
                    TABLE_REPORT,
                    cv,
                    COLUMN_REPORT_ID + " = ?",
                    new String[]{String.valueOf(report.getId())}
            );
            db.close();
        }

        return result;
    }

    public int updateReportPictureServerStatus(Report report)
    {
        int result = -1;

        if (report != null)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_REPORT_PICTURE_SENT_TO_SERVER, (report.isPictureSentToServer())?1:0);

            result = db.update(
                    TABLE_REPORT,
                    cv,
                    COLUMN_REPORT_ID + " = ?",
                    new String[]{String.valueOf(report.getId())}
            );
            db.close();
        }
        return result;
    }

    public int updateReportFootageServerStatus(Report report)
    {
        int result = -1;

        if (report != null)
        {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_REPORT_FOOTAGE_SENT_TO_SERVER, (report.isFootageSentToServer())?1:0);

            result = db.update(
                    TABLE_REPORT,
                    cv,
                    COLUMN_REPORT_ID + " = ?",
                    new String[]{String.valueOf(report.getId())}
            );
            db.close();
        }
        return result;
    }


    public int updateSurveyCompleteness(Integer instID, Integer surveyID, boolean completed)
    {
        User mUser = mUserManager.getCurrentUser();
        long now = new GregorianCalendar().getTimeInMillis();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLACE_SURVEY_COMPLETED, completed);
        cv.put(COLUMN_PLACE_SURVEY_COMPLETION_TIME, now);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_PLACE_SURVEY,
                cv,
                COLUMN_PLACE_SURVEY_ID_USER + " = ? AND " +
                        COLUMN_PLACE_SURVEY_ID_PLACE + " = ? AND " +
                        COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID)}
        );
        db.close();

        //Log.i(TAG, "updateSurveyCompleteness: result " + result);

        return result;
    }

    public int updateSurveyExpiration(Integer instID, Integer surveyID, Long newStartTimeInMillis)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLACE_SURVEY_START_TIME, newStartTimeInMillis);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_PLACE_SURVEY,
                cv,
                COLUMN_PLACE_SURVEY_ID_USER + " = ? AND " +
                        COLUMN_PLACE_SURVEY_ID_PLACE + " = ? AND " +
                        COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID)}
        );
        db.close();

        Log.i(TAG, "updateSurveyExpiration: result " + result);

        return result;
    }

    public int updateUserAnswerForYesOrNoQuestion(Integer instID, Integer surveyID, Integer questionID, String newAnswer)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_YES_NO_QUESTION_USER_ANSWER, newAnswer);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_YES_NO_QUESTION_ANSWER,
                cv,
                COLUMN_YES_NO_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY + " = ? AND " +
                        COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)}
        );
        db.close();

        //Log.i(TAG, "updateUserAnswerForYesOrNoQuestion : cv = " + cv.toString() + " : result = " + result);
        ////Log.i(TAG, "updateUserAnswerForYesOrNoQuestion : instID = " + instID + ", surveyID = " + surveyID + ", questionID = " + questionID + ", newAnswer = " + newAnswer);

        return result;
    }

    public int updateUserRatingForRatingQuestion(Integer instID, Integer surveyID, Integer questionID, Float newRating)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RATING_QUESTION_USER_RATING, newRating);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_RATING_QUESTION_ANSWER,
                cv,
                COLUMN_RATING_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY + " = ? AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)}
        );
        db.close();

        //Log.i(TAG, "updateUserRatingForRatingQuestion: cv = " + cv.toString() + " : result = " + result);

        return result;
    }

    public int updateCheckStatusForCheckboxQuestion(Integer instID, Integer surveyID, Integer questionID, Integer answerOptionID, boolean newStatus)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CHECKBOX_QUESTION_ANSWER_CHECKED, newStatus);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_CHECKBOX_QUESTION_ANSWER,
                cv,
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_ANSWER_OPTION + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID), String.valueOf(answerOptionID)}
        );
        db.close();

        //Log.i(TAG, "updateCheckStatusForCheckboxQuestion: cv = " + cv.toString() + " : result = " + result);
        //Log.i(TAG, "updateCheckStatusForCheckboxQuestion : instID = " + instID + ", surveyID = " + surveyID + ", questionID = " + questionID + ", answerOptionID = " + answerOptionID + ", newStatus = " + newStatus);

        return result;
    }

    public int updateUserAnswerForRadioButtonQuestion(Integer instID, Integer surveyID, Integer questionID, Integer answerOptionID)
    {
        User mUser = mUserManager.getCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RADIOBUTTON_QUESTION_ANSWER, answerOptionID);

        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(
                TABLE_RADIOBUTTON_QUESTION_ANSWER,
                cv,
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY + " = ? AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(mUser.getUserId()), String.valueOf(instID), String.valueOf(surveyID), String.valueOf(questionID)}
        );
        db.close();

        //Log.i(TAG, "updateUserAnswerForRadioButtonQuestion: cv = " + cv.toString() + " : result = " + result);
        //Log.i(TAG, "updateUserAnswerForRadioButtonQuestion : instID = " + instID + ", surveyID = " + surveyID + ", questionID = " + questionID + ", answerOptionID = " + answerOptionID);

        return result;
    }


    /*********************** Operações de DELETE ***********************/

    public int deleteUserById(Integer userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_USER,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        db.close();

        Log.i(TAG, "deleteUserById : userId = " + userId + ", RESULT: " + result);

        return result;
    }

    public int deleteInstitutionById(Integer instId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_PLACE,
                        COLUMN_PLACE_ID + " = ?",
                new String[]{String.valueOf(instId)}
        );
        db.close();

        Log.i(TAG, "deleteInstitutionById : instId = " + instId + ", RESULT: " + result);

        return result;
    }

    public int deleteReportById(Integer reportId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_REPORT,
                        COLUMN_REPORT_ID + " = ?",
                new String[]{String.valueOf(reportId)}
        );
        db.close();

        Log.i(TAG, "deleteReportById :  reportId = " + reportId + ", RESULT: " + result);

        return result;
    }

    public int deleteReportForUser(Integer userId, Integer instId, Integer reportId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_REPORT,
                COLUMN_REPORT_ID_USER + " = ?" + " AND " +
                        COLUMN_REPORT_ID_PLACE + " = ?" + " AND " +
                        COLUMN_REPORT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(reportId)}
        );
        db.close();

        Log.i(TAG, "deleteReportForUser : userId = " + userId + ", instId = " + instId + ", reportId = " + reportId + ", RESULT: " + result);

        return result;
    }

    public int deleteInstSurveyForUser(Integer userId, Integer instId, Integer surveyId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_PLACE_SURVEY,
                COLUMN_PLACE_SURVEY_ID_USER + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_ID_PLACE + " = ?" + " AND " +
                        COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(surveyId)}
        );
        db.close();

        Log.i(TAG, "deleteInstSurveyForUser : userId = " + userId + ", instId = " + instId + ", surveyId = " + surveyId + ", RESULT: " + result);

        return result;
    }

    public int deleteSurveyById(Integer surveyId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_SURVEY,
                COLUMN_SURVEY_ID + " = ?",
                new String[]{String.valueOf(surveyId)}
        );
        db.close();

        Log.i(TAG, "deleteSurveyById : surveyId = " + surveyId + ", RESULT: " + result);

        return result;
    }

    public int deleteSurveyQuestionsEntryBySurveyId(Integer surveyId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_PLACE_SURVEY,
                COLUMN_PLACE_SURVEY_ID_SURVEY + " = ?",
                new String[]{String.valueOf(surveyId)}
        );
        db.close();

        Log.i(TAG, "deleteSurveyQuestionsEntryBySurveyId : surveyId = " + surveyId + ", RESULT: " + result);

        return result;
    }

    public int deleteQuestionById(Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_QUESTION,
                COLUMN_QUESTION_ID + " = ?",
                new String[]{String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteQuestionById : questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    public int deleteMultiChoiceQuestionEntryById(Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_MULTIPLE_CHOICE_QUESTION,
                COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION + " = ?",
                new String[]{String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteMultiChoiceQuestionEntryById : questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    public int deleteAnswerOptionById(Integer answerOptionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_ANSWER_OPTION,
                COLUMN_ANSWER_OPTION_ID + " = ?",
                new String[]{String.valueOf(answerOptionId)}
        );
        db.close();

        Log.i(TAG, "deleteAnswerOptionById : answerOptionId = " + answerOptionId + ", RESULT: " + result);

        return result;
    }

    public int deleteUserAnswerForYesOrNoQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_YES_NO_QUESTION_ANSWER,
                COLUMN_YES_NO_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                COLUMN_YES_NO_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(surveyId), String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteUserAnswerForYESorNOQuestion : userId = " + userId + ", instId = " + instId + ", surveyId = " + surveyId + ", questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    public int deleteUserAnswerForRatingQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_RATING_QUESTION_ANSWER,
                COLUMN_RATING_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_RATING_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(surveyId), String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteUserAnswerForRATINGQuestion : userId = " + userId + ", instId = " + instId + ", surveyId = " + surveyId + ", questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    public int deleteUserAnswerForRadioButtonQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_RADIOBUTTON_QUESTION_ANSWER,
                COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_RADIOBUTTON_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(surveyId), String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteUserAnswerForRADIOBUTTONQuestion : userId = " + userId + ", instId = " + instId + ", surveyId = " + surveyId + ", questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    public int deleteUserAnswersForCheckBoxQuestion(Integer userId, Integer instId, Integer surveyId, Integer questionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(
                TABLE_CHECKBOX_QUESTION_ANSWER,
                COLUMN_CHECKBOX_QUESTION_ANSWER_ID_USER + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_PLACE + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_SURVEY + " = ?" + " AND " +
                        COLUMN_CHECKBOX_QUESTION_ANSWER_ID_QUESTION + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(instId), String.valueOf(surveyId), String.valueOf(questionId)}
        );
        db.close();

        Log.i(TAG, "deleteUserAnswersForCHECKBOXQuestion : userId = " + userId + ", instId = " + instId + ", surveyId = " + surveyId + ", questionId = " + questionId + ", RESULT: " + result);

        return result;
    }

    /*********************** CursorWrappers Customizados ***********************/

    public static class UserCursor extends CursorWrapper
    {
        public UserCursor(Cursor cursor)
        {
            super(cursor);
        }

        public User getUser()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            int userId = getInt(getColumnIndex(COLUMN_USER_ID));
            User user = new User(userId);

            String firstName = getString(getColumnIndex(COLUMN_USER_FIRST_NAME));
            user.setFirstName(firstName);

            String lastName = getString(getColumnIndex(COLUMN_USER_LAST_NAME));
            user.setLastName(lastName);

            Long bdayMillis = getLong(getColumnIndex(COLUMN_USER_DATE_BIRTH));
            GregorianCalendar dateOfBirth = new GregorianCalendar();
            dateOfBirth.setTimeInMillis(bdayMillis);
            user.setDateOfBirth(dateOfBirth);

            /*String gender = getString(getColumnIndex(COLUMN_USER_GENDER));
            user.setGender(gender);*/

            String email = getString(getColumnIndex(COLUMN_USER_EMAIL));
            user.setEmail(email);

            String phoneNumber = getString(getColumnIndex(COLUMN_USER_PHONE_NUMBER));
            user.setPhoneNumber(phoneNumber);

            /*int loginType = getInt(getColumnIndex(COLUMN_USER_FB_ID));
            user.setLoginType(loginType);*/

            int pictureId = getInt(getColumnIndex(COLUMN_USER_PICTURE_ID));
            user.setPictureId(pictureId);

            String pictureUriText = getString(getColumnIndex(COLUMN_USER_PICTURE_URI));
            if (pictureUriText != null) {
                Uri pictureUri = Uri.parse(pictureUriText);
                user.setPictureUri(pictureUri);
            }

            return user;
        }
    }

    public static class PublicInstitutionCursor extends CursorWrapper
    {
        Context mContext;

        public PublicInstitutionCursor(Cursor cursor, Context pContext)
        {
            super(cursor);
            mContext = pContext;
        }

        public PublicInstitution getInstitution()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            PublicInstitution institution = null;

            int id = getInt(getColumnIndex(COLUMN_PLACE_ID));
            int type = getInt(getColumnIndex(COLUMN_PLACE_TYPE));

            if (type == PublicInstitution.TIPO_EDUCACAO)
            {
                institution = new PublicInstitutionEducacao(id, mContext);
            }
            else if (type == PublicInstitution.TIPO_SAUDE)
            {
                institution = new PublicInstitutionSaude(id, mContext);
            }
            else if (type == PublicInstitution.TIPO_SEGURANCA)
            {
                institution = new PublicInstitutionSeguranca(id, mContext);
            }

            if (institution != null)
            {
                String name = getString(getColumnIndex(COLUMN_PLACE_NAME));
                institution.setNome(name);

                String abbrev = getString(getColumnIndex(COLUMN_PLACE_ABBREVIATED_NAME));
                institution.setNomeAbreviado(abbrev);

                Double rating = getDouble(getColumnIndex(COLUMN_PLACE_RATING));
                institution.setNotaMedia(rating);

                int status = getInt(getColumnIndex(COLUMN_PLACE_STATUS));
                institution.setStatus(status);

                /*int deptId = getInt(getColumnIndex(COLUMN_PLACE_DEPT_ID));
                institution.setDepartmentId(deptId);*/

                Double latitude = getDouble(getColumnIndex(COLUMN_PLACE_LAT));
                Double longitude = getDouble(getColumnIndex(COLUMN_PLACE_LNG));
                institution.setLatLng(new LatLng(latitude, longitude));

                /*int open = getInt(getColumnIndex(COLUMN_PLACE_OPENING_TIME));
                institution.setHoraAbertura(open);

                int close = getInt(getColumnIndex(COLUMN_PLACE_CLOSING_TIME));
                institution.setHoraFechamento(close);*/

                /*String days = getString(getColumnIndex(COLUMN_PLACE_OPEN_DAYS));
                institution.setWorkingDays(days);*/
            }

            return institution;
        }
    }

    public static class ReportCursor extends CursorWrapper
    {
        private Context mContext;

        public ReportCursor(Cursor cursor, Context pContext) {
            super(cursor);
            mContext = pContext;
        }

        public Report getReport()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Report report = new Report(mContext);

            int id = getInt(getColumnIndex(COLUMN_REPORT_ID));
            report.setId(id);

            int userId = getInt(getColumnIndex(COLUMN_REPORT_ID_USER));
            report.setUserId(userId);

            int instId = getInt(getColumnIndex(COLUMN_REPORT_ID_PLACE));
            report.setPublicInstitutionId(instId);

            String comment = getString(getColumnIndex(COLUMN_REPORT_COMMENT));
            report.setComment(comment);

            /*int pictureId = getInt(getColumnIndex(COLUMN_REPORT_PICTURE_ID));
            report.setPictureId(pictureId);

            int footageId = getInt(getColumnIndex(COLUMN_REPORT_FOOTAGE_ID));
            report.setFootageId(footageId);*/

            String pictureUri = getString(getColumnIndex(COLUMN_REPORT_PICTURE_URI));
            if ((pictureUri != null) && (pictureUri.length() > 0))
                report.setPictureUri(Uri.parse(pictureUri));

            String videoUri = getString(getColumnIndex(COLUMN_REPORT_FOOTAGE_URI));
            if ((videoUri != null) && (videoUri.length() > 0))
                report.setFootageUri(Uri.parse(videoUri));

            long madeAtInMillis = getLong(getColumnIndex(COLUMN_REPORT_MADE_AT));
            GregorianCalendar madeAt = new GregorianCalendar();
            madeAt.setTimeInMillis(madeAtInMillis);
            report.setMadeAt(madeAt);

            int idOnServer = getInt(getColumnIndex(COLUMN_REPORT_SERVER_ID));
            report.setIdOnServer(idOnServer);

            boolean commentSentToServer = (getInt(getColumnIndex(COLUMN_REPORT_COMMENT_SENT_TO_SERVER)) == 1)?true:false;
            report.setCommentSentToServer(commentSentToServer);

            boolean pictureSentToServer = (getInt(getColumnIndex(COLUMN_REPORT_PICTURE_SENT_TO_SERVER)) == 1)?true:false;
            report.setPictureSentToServer(pictureSentToServer);

            boolean footageSentToServer = (getInt(getColumnIndex(COLUMN_REPORT_FOOTAGE_SENT_TO_SERVER)) == 1)?true:false;
            report.setFootageSentToServer(footageSentToServer);

            return report;
        }
    }

    public static class SurveyCursor extends CursorWrapper
    {
        public SurveyCursor(Cursor cursor)
        {
            super(cursor);
        }

        public Survey getSurvey()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Survey survey = new Survey();

            int surveyId = getInt(getColumnIndex(COLUMN_SURVEY_ID));
            survey.setId(surveyId);

            String description = getString(getColumnIndex(COLUMN_SURVEY_DESCRIPTION));
            survey.setDescription(description);

            long availableSinceInMillis = getLong(getColumnIndex(COLUMN_SURVEY_AVAILABLE_SINCE));
            GregorianCalendar availableSince = new GregorianCalendar();
            availableSince.setTimeInMillis(availableSinceInMillis);
            survey.setAvailableSince(availableSince);

            return survey;
        }
    }

    public static class InstitutionSurveyCursor extends CursorWrapper
    {
        public InstitutionSurveyCursor(Cursor cursor)
        {
            super(cursor);
        }

        public boolean isCompleted()
        {
            if (isBeforeFirst() || isAfterLast())
                return false;

            int is_completed = getInt(getColumnIndex(COLUMN_PLACE_SURVEY_COMPLETED));

            if (is_completed == 1)
                return true;

            return false;
        }

        public GregorianCalendar getStartTime() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            long startDateInMillis = getLong(getColumnIndex(COLUMN_PLACE_SURVEY_START_TIME));
            GregorianCalendar startDate = new GregorianCalendar();
            startDate.setTimeInMillis(startDateInMillis);

            return startDate;
        }

        public boolean isExpiringSoon()
        {
            if (isBeforeFirst() || isAfterLast())
                return false;

            /*long startDateInMillis = getLong(getColumnIndex(COLUMN_PLACE_SURVEY_START_TIME));
            GregorianCalendar startTime = new GregorianCalendar();
            startTime.setTimeInMillis(startDateInMillis);*/
            GregorianCalendar startTime = getStartTime();

            if (startTime != null)
            {
                GregorianCalendar limitDate = (GregorianCalendar) startTime.clone();
                limitDate.add(Calendar.DATE, Constants.SURVEY_MAX_DAYS_AVAILABLE);

                GregorianCalendar now = new GregorianCalendar();

                int remainingTimeInHours = (int) (limitDate.getTime().getTime() - now.getTime().getTime())/(1000 * 60 * 60);

                if (remainingTimeInHours <= Constants.SURVEY_ALERT_FEW_HOURS_REMAINING)
                    return true;
            }

            return false;
        }

        public GregorianCalendar getCompleteTime() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            long completeTimeInMillis = getLong(getColumnIndex(COLUMN_PLACE_SURVEY_COMPLETION_TIME));
            GregorianCalendar completeTime = new GregorianCalendar();
            completeTime.setTimeInMillis(completeTimeInMillis);

            return completeTime;
        }

        public Integer getSurveyId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer surveyId = getInt(getColumnIndex(COLUMN_PLACE_SURVEY_ID_SURVEY));

            return surveyId;
        }

        public Integer getInstitutionId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer instId = getInt(getColumnIndex(COLUMN_PLACE_SURVEY_ID_PLACE));

            return instId;
        }

        public Integer getUserId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer userId = getInt(getColumnIndex(COLUMN_PLACE_SURVEY_ID_USER));

            return userId;
        }
    }

    public static class QuestionCursor extends CursorWrapper
    {
        private Context mContext;
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public QuestionCursor(Cursor cursor, Context pContext)
        {
            super(cursor);
            mContext = pContext;
        }

        public Question getQuestion()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Question question = null;

            int questionId = getInt(getColumnIndex(COLUMN_QUESTION_ID));
            int type = getInt(getColumnIndex(COLUMN_QUESTION_TYPE));
            String description = getString(getColumnIndex(COLUMN_QUESTION_DESCRIPTION));
            boolean mandatory = false;
            int aux_mandatory = getInt(getColumnIndex(COLUMN_QUESTION_MANDATORY));
            if (aux_mandatory == 1)
                mandatory = true;

            switch (type)
            {
                case (Question.QUESTION_TYPE_YES_OR_NO):
                    question = new YesOrNoQuestion(mContext);
                    break;

                case (Question.QUESTION_TYPE_RATING):
                    question = new RatingQuestion(mContext);
                    break;

                case (Question.QUESTION_TYPE_RADIOBUTTON):
                    question = new RadioButtonQuestion(mContext);
                    break;

                case (Question.QUESTION_TYPE_CHECKBOX):
                    question = new CheckboxQuestion(mContext);
                    break;

                default:
                    //Log.i(TAG, "Tipo de questao desconhecido = " + type);
                    return null;
            }

            question.setId(questionId);
            question.setDescription(description);
            question.setIsMandatory(mandatory);

            return question;
        }
    }

    public static class YesOrNoAnswerCursor extends CursorWrapper
    {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public YesOrNoAnswerCursor(Cursor cursor)
        {
            super(cursor);

        }

        public String getUserAnswer()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            String userAnswer = getString(getColumnIndex(COLUMN_YES_NO_QUESTION_USER_ANSWER));

            return userAnswer;
        }
    }

    public static class RatingAnswerCursor extends CursorWrapper
    {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public RatingAnswerCursor(Cursor cursor)
        {
            super(cursor);

        }

        public Float getUserRating()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Float userRating = getFloat(getColumnIndex(COLUMN_RATING_QUESTION_USER_RATING));

            return userRating;
        }
    }

    public static class SurveyQuestionCursor extends CursorWrapper
    {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public SurveyQuestionCursor(Cursor cursor)
        {
            super(cursor);

        }

        public Integer getSurveyId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer id = getInt(getColumnIndex(COLUMN_SURVEY_QUESTION_ID_SURVEY));

            return id;
        }

        public Integer getQuestionId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer id = getInt(getColumnIndex(COLUMN_SURVEY_QUESTION_ID_QUESTION));

            return id;
        }
    }

    public static class MultipleChoiceQuestionCursor extends CursorWrapper
    {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public MultipleChoiceQuestionCursor(Cursor cursor)
        {
            super(cursor);
        }

        public Integer getQuestionId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer id = getInt(getColumnIndex(COLUMN_MULTIPLE_CHOICE_QUESTION_ID_QUESTION));

            return id;
        }

        public Integer getAnswerOptionId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer id = getInt(getColumnIndex(COLUMN_MULTIPLE_CHOICE_QUESTION_ID_ANSWER_OPTION));

            return id;
        }
    }

    public static class CheckboxQuestionAnswerCursor extends CursorWrapper
    {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public CheckboxQuestionAnswerCursor(Cursor cursor)
        {
            super(cursor);
        }

        public boolean isChecked()
        {
            if (isBeforeFirst() || isAfterLast())
                return false;

            Integer is_checked = getInt(getColumnIndex(COLUMN_CHECKBOX_QUESTION_ANSWER_CHECKED));

            if (is_checked == 1)
                return true;

            return false;
        }
    }

    public static class RadioButtonQuestionAnswerCursor extends CursorWrapper
    {
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public RadioButtonQuestionAnswerCursor(Cursor cursor)
        {
            super(cursor);
        }

        public Integer getAnswerId()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            Integer answer_id = getInt(getColumnIndex(COLUMN_RADIOBUTTON_QUESTION_ANSWER));

            if (answer_id.intValue() == -1)
                return null;

            return answer_id;
        }
    }

    public static class AnswerOptionCursor extends CursorWrapper
    {
        private Context mContext;
        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public AnswerOptionCursor(Cursor cursor, Context pContext)
        {
            super(cursor);
            mContext = pContext;
        }

        public AnswerOption getAnswerOption()
        {
            if (isBeforeFirst() || isAfterLast())
                return null;

            AnswerOption answerOption = new AnswerOption(mContext);

            int id = getInt(getColumnIndex(COLUMN_ANSWER_OPTION_ID));
            String description = getString(getColumnIndex(COLUMN_ANSWER_OPTION_DESCRIPTION));

            answerOption.setId(id);
            answerOption.setAnswerText(description);

            return answerOption;
        }
    }
}
