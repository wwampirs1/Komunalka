package ua.android.d2.komunalka.Base;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ua.android.d2.komunalka.MyMethods;
import ua.android.d2.komunalka.Tariff;

/**
 * Created by Julia on 15.05.2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME_TARIFF = "name_taruf";
    public static final String TCNT_ID = "id";
    public static final String TCNT_NAME = "name";
    public static final String TCNT_TARUF_ID = "taruf_id";

    public static final String TABLE_NAME_TARIFF_VALUE = "taruf";
    public static final String TCTF_ID = "t_id";
    public static final String TCTF_TARIFF_ID = "t_taruf_id";
    public static final String TCTF_NUMBER = "t_number";
    public static final String TCTF_VALUE = "t_value";

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "communalka", null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("myLogs", "--- onCreate database ---");
        //createAndInsert(db);
        createTable(db);
        insertTable(db);
        Log.d("myLogs", "end onCreate database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
// создания таблиц
    private void createTable(SQLiteDatabase db)
    {
        try {
        Log.d("myLogs", "create table name_taruf");

        db.execSQL(new StringBuilder("CREATE TABLE ").append(TABLE_NAME_TARIFF).append(" ( ")
                .append(TCNT_ID).append(" integer primary key autoincrement, ")
                .append(TCNT_NAME).append(" text, ")
                .append(TCNT_TARUF_ID).append(" integer ")
                .append("); ")
                .toString());
        Log.d("myLogs", "create table taruf");

        db.execSQL(new StringBuilder("CREATE TABLE ").append(TABLE_NAME_TARIFF_VALUE).append(" (")
                .append(TCTF_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(TCTF_TARIFF_ID).append(" INTEGER, ")
                .append(TCTF_NUMBER).append(" TEXT, ")
                .append(TCTF_VALUE).append(" TEXT")
                .append(" ); ")
                .toString());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
        }
    }
// заполнения таблиц
    private void insertTable (SQLiteDatabase db)
    {
        try {
            int j = 1;
            List<Tariff> listTariff = new ArrayList<>();
            TreeMap<Double, Double> value;
            Log.d("myLogs", "insert begin");
            value = new TreeMap<Double, Double>();
            value.put(0.0, 12.3);
            listTariff.add(new Tariff("Гарячая вода", j++, value));
            value = new TreeMap<Double, Double>();
            value.put(0.0, 3.30);
            listTariff.add(new Tariff("Холодная вода", j++, value));

            value = new TreeMap<Double, Double>();
            value.put(0.0, 0.363);
            value.put(100.0, 0.6303);
            value.put(600.0, 1.477);
            listTariff.add(new Tariff("Електрика", j++, value));

            value = new TreeMap<Double, Double>();
            value.put(0.0, 7.20);
            listTariff.add(new Tariff("Газ", j++, value));
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            try {
                db.beginTransaction();
                for (Tariff t : listTariff) {
                    ContentValues cv = new ContentValues();
                    cv.put(TCNT_NAME, t.getName());
                    cv.put(TCNT_TARUF_ID, t.getTarufId());
                    // INSERTg
                    long insert = db.insert(TABLE_NAME_TARIFF, null, cv);
                }
                // COMMIT
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            //виводим в лог
            MyMethods.logCursor(db.query(TABLE_NAME_TARIFF, null, null, null, null, null, null));
            try {
                db.beginTransaction();
                for (Tariff t : listTariff) {
                    for (Map.Entry entry : t.getValue().entrySet()) {
                        ContentValues cv = new ContentValues();
                        cv.put(TCTF_TARIFF_ID, t.getTarufId());
                        cv.put(TCTF_NUMBER, String.valueOf(entry.getKey()));
                        cv.put(TCTF_VALUE, String.valueOf(entry.getValue()));
                        // INSERT
                        db.insert(TABLE_NAME_TARIFF_VALUE, null, cv);
                    }
                }
                // COMMIT
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            Log.d("myLogs", "end insert tables");
            MyMethods.logCursor(db.query(TABLE_NAME_TARIFF_VALUE, null, null, null, null, null, null));
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
        }
    }

}
