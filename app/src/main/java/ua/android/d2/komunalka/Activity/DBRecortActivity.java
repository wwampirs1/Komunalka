package ua.android.d2.komunalka.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ua.android.d2.komunalka.Base.DBHelper;
import ua.android.d2.komunalka.Base.Dao;
import ua.android.d2.komunalka.AdditionalMetods;
import ua.android.d2.komunalka.R;
import ua.android.d2.komunalka.Tariff;


public class DBRecortActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {
  private   Spinner spTarufValue;
    private   ListView lvTarufValue;
    private ListView lvTaruf;
    private DBHelper dbHelper;
    private Tariff tarif;
    private String stringToIntent;
    private ArrayAdapter arrayAdapter;
    private Button btnAdd, btnUpdate, btnDelete;
    private boolean activityFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myLogs", "BdActivity begin onCreate");
        initializationComponents();
        Log.d("myLogs", "BdActivity begin onCreate");
    }

    //инициализация компонентов
    private void initializationComponents() {
        dbHelper = new DBHelper(this);
        Dao dao = new Dao(dbHelper.getReadableDatabase());
        Intent intent = getIntent();
        switch (intent.getAction()) {
            case "ua.android.d2.komunalka.taruf":
                setContentView(R.layout.taruf);
                lvTaruf = (ListView) findViewById(R.id.lvTaruf);
                lvTaruf.setOnItemSelectedListener(this);
                lvTaruf.setOnItemClickListener(this);
                listViewTaruff();
                activityFlag = true;
                break;
            case "ua.android.d2.komunalka.taruf_value":
                setContentView(R.layout.taruf_value);
                lvTarufValue = (ListView) findViewById(R.id.lvTarufValue);
                lvTarufValue.setOnItemSelectedListener(this);
                lvTarufValue.setOnItemClickListener(this);
                spTarufValue = (Spinner) findViewById(R.id.spTarufValue);
                spTarufValue.setOnItemSelectedListener(this);
                arrayAdapter = new ArrayAdapter(this, R.layout.list_item, dao.selectName());
                spTarufValue.setAdapter(arrayAdapter);
                listViewTariffValue();
                registerForContextMenu(lvTarufValue);
                activityFlag = false;
                break;
        }
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    //обработка spiner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (activityFlag) {
            listViewTaruff();
        } else {
            listViewTariffValue();
        }
        Log.d("myLogs", adapterView.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //обработка нажатия на  елемента списка
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            //((TextView) view).setBackgroundColor(black);
            stringToIntent = arrayAdapter.getItem(position).toString();
            if (!activityFlag) {
                List<String> list = AdditionalMetods.splitToArray(arrayAdapter.getItem(position).toString());
                List<String> list2 = new ArrayList<>();
                for (String s : list) {
                    if (AdditionalMetods.tryParseDouble(s)) list2.add(s);
                }
                tarif = new Tariff(Integer.valueOf(list2.get(0)));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
        }
    }

    //инициазация lvTaruf
    void listViewTaruff() {
        Dao dao = new Dao(dbHelper.getReadableDatabase());
        arrayAdapter = new ArrayAdapter(this, R.layout.list_item, dao.selectName());
        lvTaruf.setAdapter(arrayAdapter);
    }

    //инициазация lvTarufValue
    void listViewTariffValue() {
        Dao dao = new Dao(dbHelper.getReadableDatabase());
        arrayAdapter = new ArrayAdapter(this, R.layout.list_item, dao.selectTariffAllColumns(spTarufValue.getSelectedItem().toString()));
        lvTarufValue.setAdapter(arrayAdapter);
        //dao.close();
    }

    // обработка возврата результата после закрития активностей
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    listViewTariffValue();
                    break;
                case 2:
                    listViewTaruff();
                    Intent intent = new Intent("ua.android.d2.komunalka.add.taruf.valua");
                    intent.putExtra("stringAdapter", data.getStringExtra("insertTableTariffName"));
                    startActivityForResult(intent, 3);
                    break;
                case 3:
                    listViewTaruff();
                    break;
            }
        } else {
            Toast.makeText(this, "Неверный результат возврата", Toast.LENGTH_SHORT).show();
        }
    }

    //обработка нажатия на кнопки
    @Override
    public void onClick(View v) {
        Intent intent = null;
        Dao dao = new Dao(dbHelper.getWritableDatabase());
        switch (v.getId()) {
            case R.id.btnAdd://добавить
                addButton(intent);
                break;
            case R.id.btnUpdate://именить
                updateButton(intent);
                break;
            case R.id.btnDelete://удалить
                deleteButton(dao);
                break;
        }
        stringToIntent = null;
        tarif = null;
    }

    private void addButton(Intent intent) {
        if (activityFlag) {
            intent = new Intent("ua.android.d2.komunalka.add.taruf");
            startActivityForResult(intent, 2);

        } else {
            intent = new Intent("ua.android.d2.komunalka.add.taruf.valua");
            intent.putExtra("stringAdapter", spTarufValue.getSelectedItem().toString());
            startActivityForResult(intent, 1);
        }
    }

    private void updateButton(Intent intent) {
        if (stringToIntent != null || tarif != null) {
            if (activityFlag) {
                intent = new Intent("ua.android.d2.komunalka.update.taruf");
                intent.putExtra("stringAdapter", stringToIntent);
                startActivityForResult(intent, 3);
            } else {
                intent = new Intent("ua.android.d2.komunalka.update.taruf.valua");
                intent.putExtra("stringAdapter", stringToIntent);
                startActivityForResult(intent, 1);
            }
        } else
            Toast.makeText(this, "Виберете запись для изменения", Toast.LENGTH_SHORT).show();
    }

    private void deleteButton(Dao dao) {
        if (stringToIntent != null || tarif != null) {
            if (activityFlag) {
                dao.deleteTariffName(dao.getIdNameTariff(stringToIntent));
                listViewTaruff();
            } else {
                dao.deleteTariffValue(String.valueOf(tarif.getId()));
                listViewTariffValue();
            }
        } else
            Toast.makeText(this, "Виберете запись для удаления", Toast.LENGTH_SHORT).show();
    }


}

