package ua.android.d2.komunalka.Base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ua.android.d2.komunalka.AdditionalMetods;
import ua.android.d2.komunalka.Tariff;

/**
 * Created by Julia on 15.05.2015.
 */
public class Dao {
    private SQLiteDatabase db;
    private final String LOG = "myLogs";

    public Dao(SQLiteDatabase db) {
        this.db = db;
    }

    public void close() {
        db.close();
    }

    public Tariff selectTariff(String s) {
        Tariff t = new Tariff();
        Cursor c = null;
        try {
            c = db.query(DBHelper.TABLE_NAME_TARIFF + " as n inner join " + DBHelper.TABLE_NAME_TARIFF_VALUE + " t on n." + DBHelper.TCNT_TARUF_ID + "=t." + DBHelper.TCTF_TARIFF_ID,
                    new String[]{"n." + DBHelper.TCNT_NAME + " as name", "n." + DBHelper.TCNT_TARUF_ID + " as taruf_id", "t." + DBHelper.TCTF_NUMBER + " as t_number", "t." + DBHelper.TCTF_VALUE + " as t_value"},
                    "n." + DBHelper.TCNT_NAME + " = ?", new String[]{s}, null, null, null);
            Map<Double, Double> map = new TreeMap<>();
            while (c.moveToNext()) {
                t.setName(c.getString(0));
                t.setTarufId(c.getInt(1));
                map.put(Double.valueOf(c.getString(2)), Double.valueOf(c.getString(3)));
            }
            t.setValue(map);
            AdditionalMetods.logCursor(c);
            c.close();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
            return null;
        }
    }

    // выбираем все тарифи
    public List<String> selectName() {
        List<String> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(DBHelper.TABLE_NAME_TARIFF, new String[]{DBHelper.TCNT_NAME}, null, null, DBHelper.TCNT_NAME, null, DBHelper.TCNT_NAME);
            while (c.moveToNext()) {
                list.add(c.getString(c.getColumnIndex(DBHelper.TCNT_NAME)));
            }
            AdditionalMetods.logCursor(c);
            c.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
            return null;
        }
    }

    // удаляет тариф по id
    public void deleteTariffValue(String s) {
        try {
            db.beginTransaction();
            int delCount = db.delete(DBHelper.TABLE_NAME_TARIFF_VALUE, DBHelper.TCTF_ID + " = " + s, null);
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    // удаляет названия тарифа по taruf_id
    public void deleteTariffName(String s) {
        try {
            db.beginTransaction();
            int delCount = db.delete(DBHelper.TABLE_NAME_TARIFF, DBHelper.TCNT_TARUF_ID + " = " + s, null);
            deleteTariffValueIdTariff(s);
            Log.d(LOG, String.valueOf(delCount));
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    // удаляет значения тарифов  по taruf_id
    public void deleteTariffValueIdTariff(String s) {
        try {
            db.beginTransaction();
            int delCount = db.delete(DBHelper.TABLE_NAME_TARIFF_VALUE, DBHelper.TCTF_TARIFF_ID + " = " + s, null);
            Log.d(LOG, String.valueOf(delCount));
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    // по таблице и из какого поля получить макс. значение
    public String maxId(String nameTable, String nameColumn) {
        Cursor c = null;
        String max = null;
        try {
            c = db.query(nameTable, new String[]{"max(" + nameColumn + ") as max"}, null, null, null, null, null, null);
            if (c.moveToFirst()) {
                max = c.getString(c.getColumnIndex("max"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
            return null;
        } finally {
            c.close();
            return max;
        }
    }

    // выбираем id  по названию тарифа
    public String getIdNameTariff(String name) {
        Cursor c = null;
        String id = null;
        try {
            c = db.query(DBHelper.TABLE_NAME_TARIFF, new String[]{DBHelper.TCNT_TARUF_ID}, "name = ?", new String[]{name}, null, null, null);
            if (c.moveToFirst()) {
                do {
                    id = c.getString(c.getColumnIndex(DBHelper.TCNT_TARUF_ID));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    //запись в таблицу taruf
    public void insertTariff(int id, String number, String value) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(DBHelper.TCTF_TARIFF_ID, id);
            cv.put(DBHelper.TCTF_NUMBER, number);
            cv.put(DBHelper.TCTF_VALUE, value);
            db.beginTransaction();
            long insert = db.insert(DBHelper.TABLE_NAME_TARIFF_VALUE, null, cv);
            Log.d(LOG, String.valueOf(insert));
            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    //запись в таблицу name_taruf
    public void insertTariffName(String name) {
        ContentValues cv = new ContentValues();
        try {
            cv.put(DBHelper.TCNT_NAME, name);
            cv.put(DBHelper.TCNT_TARUF_ID, Integer.parseInt(maxId(DBHelper.TABLE_NAME_TARIFF_VALUE, DBHelper.TCTF_TARIFF_ID)) + 1);
            db.beginTransaction();
            long insert = db.insert(DBHelper.TABLE_NAME_TARIFF, null, cv);
            Log.d(LOG, String.valueOf(insert));
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    public List<String> selectTariffAllColumns(String s) {
        Log.d(LOG, "Metods selectTariffAllColumns");
        List<String> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(DBHelper.TABLE_NAME_TARIFF + " as n inner join " + DBHelper.TABLE_NAME_TARIFF_VALUE + " t on n." + DBHelper.TCNT_TARUF_ID + "=t." + DBHelper.TCTF_TARIFF_ID,
                    new String[]{"t." + DBHelper.TCTF_ID + " as id", "t." + DBHelper.TCTF_TARIFF_ID + " as taruf_id", "t." + DBHelper.TCTF_NUMBER, "t." + DBHelper.TCTF_VALUE},
                    "n." + DBHelper.TCNT_NAME + " = ?",
                    new String[]{s}, null, null, null);
            if (c.moveToFirst()) {
                do {
                    StringBuffer st = new StringBuffer();
                    st.append("id = ").append(c.getInt(0)).append("\n")
                            .append("id_tariffs = ").append(c.getInt(1)).append("\n")
                            .append("diapazon = ").append(c.getString(2)).append("\n")
                            .append("value = ").append(c.getString(3)).append("\n");
                    list.add(String.valueOf(st));
                } while (c.moveToNext());
            }
            c.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
            return list;
        } finally {
            c.close();

        }

    }

    // изменяет поля  таблиц taruf
    public void updateTariff(String diapason, String value, String id) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.TCTF_NUMBER, diapason);
            cv.put(DBHelper.TCTF_VALUE, value);
            db.beginTransaction();
            int update = db.update(DBHelper.TABLE_NAME_TARIFF_VALUE, cv, DBHelper.TCTF_ID + " = ?", new String[]{id});
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(LOG, String.valueOf(update));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }

    // изменяет поля  таблиц name_taruf
    public void updateTariffName(String oldName, String newName) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.TCNT_NAME, newName);
            db.beginTransaction();
            int update = db.update(DBHelper.TABLE_NAME_TARIFF, cv, DBHelper.TCNT_NAME + " = ?", new String[]{oldName});
            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(LOG, String.valueOf(update));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG, e.toString());
        }
    }


}
