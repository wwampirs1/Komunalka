package ua.android.d2.komunalka.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ua.android.d2.komunalka.Base.DBHelper;
import ua.android.d2.komunalka.Base.Dao;
import ua.android.d2.komunalka.MyMethods;
import ua.android.d2.komunalka.R;
import ua.android.d2.komunalka.Tariff;


public class WorkBD extends ActionBarActivity implements View.OnClickListener {

    EditText etDiapason;
    EditText etValue;
    EditText etNameTariff;
    Button btnAddUpdate;
    Tariff tarif;
    DBHelper dbHelper;
    boolean update;
    boolean tariff;
    String intentValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializationComponents();
    }
    //инициализация компонентов
    private void initializationComponents() {
        Intent intent = getIntent();
        dbHelper = new DBHelper(this);
        intentValue = intent.getStringExtra("stringAdapter");
        if (intent.getAction().equals("ua.android.d2.komunalka.add.taruf.valua") || intent.getAction().equals("ua.android.d2.komunalka.update.taruf.valua")) {
            setContentView(R.layout.activity_work_bd);
            etValue = (EditText) findViewById(R.id.etValue);
            etDiapason = (EditText) findViewById(R.id.etDiapason);
            switch (intent.getAction()) {
                //добавление
                case "ua.android.d2.komunalka.add.taruf.valua":
                    update = false;
                    tariff = false;
                    break;
                //изменение
                case "ua.android.d2.komunalka.update.taruf.valua":
                    List<String> list = MyMethods.array(intentValue);
                    List<String> list2 = new ArrayList<>();
                    for (String parametrs : list) {
                        if (MyMethods.checkString(parametrs)) list2.add(parametrs);
                    }
                    Map<Double, Double> map = new TreeMap<>();
                    map.put(Double.valueOf(list2.get(2)), Double.valueOf(list2.get(3)));
                    tarif = new Tariff(Integer.valueOf(list2.get(0)), Integer.valueOf(list2.get(1)), map);
                    update = true;
                    tariff = false;
                    etValue.setText(list2.get(3));
                    etDiapason.setText(list2.get(2));
                    break;
            }
        } else {
            setContentView(R.layout.add_update_tariff);
            etNameTariff = (EditText) findViewById(R.id.etNameTariff);
            switch (intent.getAction()) {
                case "ua.android.d2.komunalka.add.taruf":
                    tariff = true;
                    update = false;
                    break;
                case "ua.android.d2.komunalka.update.taruf":
                    tariff = true;
                    update = true;
                    etNameTariff.setText(intentValue);
                    break;
            }
        }

        btnAddUpdate = (Button) findViewById(R.id.btnAddUpdate);
        btnAddUpdate.setOnClickListener(this);
    }

    private boolean check() {
        double diapazon = Double.parseDouble(etDiapason.getText().toString());
        double value = Double.parseDouble(etValue.getText().toString());
        if (diapazon >= 0 && value >0) return true;
        else {
            Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
//обработка нажатия на кнопку
    @Override
    public void onClick(View view) {
        Dao dao = new Dao(dbHelper.getWritableDatabase());
        Intent intent = new Intent();
        try {
            if (update) {
                if (tariff) {
                    String s = etNameTariff.getText().toString();
                    if (!s.equals("")) {
                        dao.updateTariffName(intentValue, etNameTariff.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else
                        Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
                } else {
                    if (check())
                        dao.updateTariff(String.valueOf(etDiapason.getText()), String.valueOf(etValue.getText()), String.valueOf(tarif.getId()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                if (tariff) {
                    if (!etNameTariff.getText().toString().equals("")) {
                        dao.insertTariffName(etNameTariff.getText().toString());
                        intent.putExtra("insertTableTariffName", etNameTariff.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();

                    } else
                        Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (check()) {
                            dao.insertTariff(Integer.parseInt(dao.getIdNameTariff(intentValue)), etDiapason.getText().toString(), etValue.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
        }

    }
}
