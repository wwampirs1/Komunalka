package ua.android.d2.komunalka.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ua.android.d2.komunalka.R;

public class InstructionActivity extends ActionBarActivity {
    ExpandableListView elvMain;
    ListView lv;

    //инициализация
    private void inicialization() {
        elvMain = (ExpandableListView) findViewById(R.id.lvInstruction);
        // коллекция для групп
        ArrayList<Map<String, String>> groupData;
        // коллекция для элементов одной группы
       ArrayList<Map<String, String>> childDataItem;
        // общая коллекция для коллекций элементов
        ArrayList<ArrayList<Map<String, String>>> childData;
        // в итоге получится childData = ArrayList<childDataItem>
        // список аттрибутов группы или элемента
        Map<String, String> m;
        groupData = new ArrayList<Map<String, String>>();
        for (String group : getResources().getStringArray(R.array.groupe_elements)) {
            // заполняем список аттрибутов для каждой группы
            m = new HashMap<String, String>();
            m.put("groupName", group); // имя компании
            groupData.add(m);
        }
        // создаем коллекцию для коллекций элементов
        childData = new ArrayList<ArrayList<Map<String, String>>>();
        // создаем коллекцию элементов для первой группы
        childDataItem = new ArrayList<Map<String, String>>();
        // заполняем список аттрибутов для каждого элемента
        for (String field : getResources().getStringArray(R.array.field)) {
            m = new HashMap<String, String>();
            m.put("field", field); // название телефона
            childDataItem.add(m);
        }
        // добавляем в коллекцию коллекций
        childData.add(childDataItem);

        // создаем коллекцию элементов для второй группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (String buttons : getResources().getStringArray(R.array.buttons)) {
            m = new HashMap<String, String>();
            m.put("field", buttons);
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        // создаем коллекцию элементов для третьей группы
        childDataItem = new ArrayList<Map<String, String>>();
        for (String menu : getResources().getStringArray(R.array.menu)) {
            m = new HashMap<String, String>();
            m.put("field", menu);
            childDataItem.add(m);
        }
        childData.add(childDataItem);

        elvMain.setAdapter(new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] {"groupName"},
                new int[] {android.R.id.text1},
                childData,
                android.R.layout.simple_list_item_1,
                new String[] {"field"}, // список аттрибутов элементов для чтения
                new int[] {android.R.id.text1}));// список ID view-элементов, в которые будет помещены аттрибуты элементов
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        inicialization();
    }


}
