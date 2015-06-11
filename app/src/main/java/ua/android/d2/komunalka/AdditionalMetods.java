package ua.android.d2.komunalka;



import android.database.Cursor;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class AdditionalMetods {
    private static String LOG= "myLogs";;

    public static double format(double num, int col) {
        return new BigDecimal(num).setScale(col, RoundingMode.HALF_UP).doubleValue();
    }
//разбитие на подсторку і возвращает список строк
    public static List<String> array(String s) {
        List<String> list = new ArrayList<>();
        for (String mas : s.split("\n")){
            Collections.addAll(list, mas.split(" "));
        }
        return list;
    }
// проверка на число, если да то true, если exeption false
    public static boolean tryParseDouble(String string) {
        try {
           Double.parseDouble(string);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }
    // вывод информации из курсора в log
    public static void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + " ");
                    }
                    Log.d(LOG, str);
                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG, "Cursor is null");
    }
}
