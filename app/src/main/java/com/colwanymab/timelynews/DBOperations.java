package com.colwanymab.timelynews;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DBOperations extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "timely_news.db";


    private static final String QUERY_POSTS = "CREATE TABLE "+ DBContract.Recyclers.TABLE_NAME+"("+
            DBContract.Recyclers.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Recyclers.UID + " TEXT, " +
            DBContract.Recyclers.NAME + " TEXT, " +
            DBContract.Recyclers.CITY + " TEXT, " +
            DBContract.Recyclers.NUMBER + " TEXT, " +
            DBContract.Recyclers.TYPE + " TEXT, " +
            DBContract.Recyclers.LINK + " TEXT, " +
            DBContract.Recyclers.EMAIL + " TEXT, " +
            DBContract.Recyclers.ADDRESS + " TEXT, " +
            DBContract.Recyclers.LAT + " TEXT, " +
            DBContract.Recyclers.LONG +" TEXT);";

    private static final String QUERY_EVENTS = "CREATE TABLE "+ DBContract.Events.TABLE_NAME+"("+
            DBContract.Events.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Events.KEY + " TEXT, " +
            DBContract.Events.NAME + " TEXT, " +
            DBContract.Events.DATE + " TEXT, " +
            DBContract.Events.VENUE + " TEXT, " +
            DBContract.Events.LINK + " TEXT, " +
            DBContract.Events.START_DATE + " TEXT, " +
            DBContract.Events.UID + " TEXT, " +
            DBContract.Events.LAT + " TEXT, " +
            DBContract.Events.LONG +" TEXT);";


    private static final String QUERY_PRES = "CREATE TABLE "+ DBContract.ConfirmationMessage.TABLE_NAME+"("+
            DBContract.ConfirmationMessage.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.ConfirmationMessage.UID + " TEXT, " +
            DBContract.ConfirmationMessage.KEY + " TEXT, " +
            DBContract.ConfirmationMessage.MESSAGE + " TEXT, " +
            DBContract.ConfirmationMessage.PHONE + " TEXT, " +
            DBContract.ConfirmationMessage.WHATS_APP + " TEXT, " +
            DBContract.ConfirmationMessage.IMAGE_LINK_2 + " TEXT, " +
            DBContract.ConfirmationMessage.LIKE + " TEXT, " +
            DBContract.ConfirmationMessage.LIKE_NUMBER + " TEXT, " +
            DBContract.ConfirmationMessage.COMMENT_NUMBER + " TEXT, " +
            DBContract.ConfirmationMessage.TIMESTAMP +" TEXT);";


    private static final String QUERY_NEWS = "CREATE TABLE "+ DBContract.News.TABLE_NAME+"("+
            DBContract.News.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.News.KEY + " TEXT, " +
            DBContract.News.WRITER_UID + " TEXT, " +
            DBContract.News.EDITOR_UID + " TEXT, " +
            DBContract.News.HEADLINE + " TEXT, " +
            DBContract.News.STORY + " TEXT, " +
            DBContract.News.READ + " TEXT, " +
            DBContract.News.TIMESTAMP + " TEXT, " +
            DBContract.News.LINK + " TEXT, " +
            DBContract.News.TAG + " TEXT, " +
            DBContract.News.FREE + " TEXT, " +
            DBContract.News.READ_TIME +" TEXT);";


    private static final String QUERY_MESSAGES = "CREATE TABLE "+ DBContract.Messages.TABLE_NAME+"("+
            DBContract.Messages.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.Messages.UID + " TEXT, " +
            DBContract.Messages.KEY + " TEXT, " +
            DBContract.Messages.CHAT_ROOM + " TEXT, " +
            DBContract.Messages.LINK + " TEXT, " +
            DBContract.Messages.MESSAGE + " TEXT, " +
            DBContract.Messages.ISTEXT + " TEXT, " +
            DBContract.Messages.TIMESTAMP +" TEXT);";


    private static final String QUERY_STRUCTURES = "CREATE TABLE "+ DBContract.WasteCollection.TABLE_NAME + "("+
            DBContract.WasteCollection.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.WasteCollection.PHONE + " TEXT, " +
            DBContract.WasteCollection.KEY + " TEXT, " +
            DBContract.WasteCollection.PRICE + " TEXT, " +
            DBContract.WasteCollection.LAT + " TEXT, " +
            DBContract.WasteCollection.LINK + " TEXT, " +
            DBContract.WasteCollection.LONG +" TEXT);";

    Context context;
    DBOperations(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_POSTS);
        db.execSQL(QUERY_NEWS);
        db.execSQL(QUERY_MESSAGES);
        db.execSQL(QUERY_PRES);
        db.execSQL(QUERY_STRUCTURES);
        db.execSQL(QUERY_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Recyclers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.News.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Messages.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ConfirmationMessage.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.WasteCollection.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Events.TABLE_NAME);

        onCreate(db);
    }

    Cursor getRecyclers(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {
                DBContract.Recyclers.ID,
                DBContract.Recyclers.UID,
                DBContract.Recyclers.NAME,
                DBContract.Recyclers.CITY,
                DBContract.Recyclers.NUMBER,
                DBContract.Recyclers.TYPE,
                DBContract.Recyclers.LINK,
                DBContract.Recyclers.EMAIL,
                DBContract.Recyclers.ADDRESS,
                DBContract.Recyclers.LAT,
                DBContract.Recyclers.LONG
        };

        cursor = db.query(
                true,
                DBContract.Recyclers.TABLE_NAME, projections,
                null,
                null,
                DBContract.Recyclers.NAME,
                null,
                DBContract.Recyclers.LONG + " DESC",
                null
        );

        return cursor;
    }

    List<String> getRecyclerKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.Recyclers.UID};
        cursor = db.query(true, DBContract.Recyclers.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.Recyclers.UID)));
        }
        cursor.close();

        return listKeys;
    }




    Cursor getEvents(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {

                DBContract.Events.KEY,
                DBContract.Events.NAME,
                DBContract.Events.DATE,
                DBContract.Events.VENUE,
                DBContract.Events.LINK,
                DBContract.Events.START_DATE,
                DBContract.Events.UID,
                DBContract.Events.LAT,
                DBContract.Events.LONG
        };

        cursor = db.query(
                true,
                DBContract.Events.TABLE_NAME, projections,
                null,
                null,
                DBContract.Events.KEY,
                null,
                DBContract.Events.ID + " DESC",
                null
        );

        return cursor;
    }

    List<String> getEventKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.Events.KEY};
        cursor = db.query(true, DBContract.Events.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.Events.KEY)));
        }
        cursor.close();

        return listKeys;
    }



    Cursor getWaste(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {
                DBContract.WasteCollection.ID,
                DBContract.WasteCollection.PHONE,
                DBContract.WasteCollection.KEY,
                DBContract.WasteCollection.PRICE,
                DBContract.WasteCollection.LONG,
                DBContract.WasteCollection.LAT,
                DBContract.WasteCollection.LINK
        };

        cursor = db.query(
                true,
                DBContract.WasteCollection.TABLE_NAME, projections,
                null,
                null,
                DBContract.WasteCollection.KEY,
                null,
                DBContract.WasteCollection.ID + " ASC",
                null
        );

        return cursor;
    }

    int getMembers(SQLiteDatabase db, String province) {
        Cursor cursor;
        String[] projections = {
                DBContract.WasteCollection.ID
        };

        cursor = db.query(
                true,
                DBContract.WasteCollection.TABLE_NAME, projections,
                DBContract.WasteCollection.LAT + "='" + province +"'",
                null,
                DBContract.WasteCollection.KEY,
                null,
                DBContract.WasteCollection.ID + " ASC",
                null
        );
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    List<String> getWasteKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.WasteCollection.KEY};
        cursor = db.query(true, DBContract.WasteCollection.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.WasteCollection.KEY)));
        }
        cursor.close();

        return listKeys;
    }




    Cursor getNews(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {
                DBContract.News.ID,
                DBContract.News.KEY,
                DBContract.News.WRITER_UID,
                DBContract.News.EDITOR_UID,
                DBContract.News.HEADLINE,
                DBContract.News.STORY,
                DBContract.News.READ,
                DBContract.News.READ_TIME,
                DBContract.News.TIMESTAMP,
                DBContract.News.LINK,
                DBContract.News.TAG,
                DBContract.News.FREE
        };
        cursor = db.query(
                true,
                DBContract.News.TABLE_NAME,
                projections,
                null,
                null,
                DBContract.News.KEY,
                null,
                DBContract.News.TIMESTAMP + " DESC",
                null
        );

        return cursor;
    }

    List<String> getNewsKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.News.KEY};
        cursor = db.query(true, DBContract.News.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.News.KEY)));
        }
        cursor.close();

        return listKeys;
    }



    Cursor getConfirmationMessage(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {
                DBContract.ConfirmationMessage.ID,
                DBContract.ConfirmationMessage.UID,
                DBContract.ConfirmationMessage.KEY,
                DBContract.ConfirmationMessage.MESSAGE,
                DBContract.ConfirmationMessage.PHONE,
                DBContract.ConfirmationMessage.WHATS_APP,
                DBContract.ConfirmationMessage.IMAGE_LINK_2,
                DBContract.ConfirmationMessage.LIKE,
                DBContract.ConfirmationMessage.LIKE_NUMBER,
                DBContract.ConfirmationMessage.COMMENT_NUMBER,
                DBContract.ConfirmationMessage.TIMESTAMP
        };

        cursor = db.query(
                true,
                DBContract.ConfirmationMessage.TABLE_NAME, projections,
                null,
                null,
                DBContract.ConfirmationMessage.KEY,
                null,
                DBContract.ConfirmationMessage.TIMESTAMP + " DESC",
                null
        );

        return cursor;
    }

    List<String> getConfirmationKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.ConfirmationMessage.MESSAGE};
        cursor = db.query(true, DBContract.ConfirmationMessage.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.ConfirmationMessage.MESSAGE)));
        }
        cursor.close();

        return listKeys;
    }




    Cursor getInbox(SQLiteDatabase db, String userUid) {
        Cursor cursor;
        String[] projections = {
                DBContract.Messages.ID,
                DBContract.Messages.UID,
                DBContract.Messages.KEY,
                DBContract.Messages.LINK,
                DBContract.Messages.CHAT_ROOM,
                DBContract.Messages.MESSAGE,
                DBContract.Messages.ISTEXT,
                DBContract.Messages.TIMESTAMP
        };
        cursor = db.query(
                true,
                DBContract.Messages.TABLE_NAME,
                projections,
                DBContract.Messages.UID + "='" + userUid +"'",
                null,
                DBContract.Messages.KEY,
                null,
                DBContract.Messages.TIMESTAMP + " ASC",
                null
        );

        return cursor;
    }

    Cursor getInboxDetailed(SQLiteDatabase db, String senderUid) {
        Cursor cursor;
        String[] projections = {
                DBContract.Messages.ID,
                DBContract.Messages.UID,
                DBContract.Messages.KEY,
                DBContract.Messages.LINK,
                DBContract.Messages.CHAT_ROOM,
                DBContract.Messages.MESSAGE,
                DBContract.Messages.ISTEXT,
                DBContract.Messages.TIMESTAMP
        };
        cursor = db.query(
                true,
                DBContract.Messages.TABLE_NAME,
                projections,
                DBContract.Messages.UID + "='" + senderUid +"'",
                null,
                DBContract.Messages.KEY,
                null,
                DBContract.Messages.TIMESTAMP + " ASC",
                null
        );

        return cursor;
    }

    Cursor getMessages(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {
                DBContract.Messages.ID,
                DBContract.Messages.UID,
                DBContract.Messages.KEY,
                DBContract.Messages.LINK,
                DBContract.Messages.CHAT_ROOM,
                DBContract.Messages.MESSAGE,
                DBContract.Messages.ISTEXT,
                DBContract.Messages.TIMESTAMP
        };
        cursor = db.query(
                true,
                DBContract.Messages.TABLE_NAME,
                projections,
                null,
                null,
                DBContract.Messages.UID,
                null,
                DBContract.Messages.TIMESTAMP + " DESC",
                null
        );

        return cursor;
    }

    List<String> getMessagesKeys(SQLiteDatabase db) {
        Cursor cursor;

        String[] arrayMax = new String[]{""};
        List<String> listKeys = new LinkedList<>(Arrays.asList(arrayMax));
        String[] projections = {DBContract.Messages.KEY};
        cursor = db.query(true, DBContract.Messages.TABLE_NAME, projections, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            listKeys.add(cursor.getString(cursor.getColumnIndex(DBContract.Messages.KEY)));
        }
        cursor.close();

        return listKeys;
    }

}
