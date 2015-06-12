package ua.android.d2.komunalka.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.android.d2.komunalka.Base.DBHelper;
import ua.android.d2.komunalka.Base.Dao;
import ua.android.d2.komunalka.AdditionalMetods;
import ua.android.d2.komunalka.R;
import ua.android.d2.komunalka.Tariff;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private DBHelper dbHelper;
    private Button btnAction;
    private Button btnClear;
    private TextView tvPrevision;
    private TextView tvActual;
    private TextView tvRezult;
    private EditText etPrevision;
    private EditText etActual;
    private Spinner spListCommunal;
    private  String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myLogs", "begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        currency=sp.getString("currency","грн");
        spListCommunal.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, new Dao(new DBHelper(this).getReadableDatabase()).selectName()));
    }

    @Override
    public void onClick(View view) {
        Log.d("myLogs", "Click begin");
        switch (view.getId()) {
            case R.id.btnAction:
                calculation();
                break;
            case R.id.btnClear:
                clear();
                break;
        }
        Log.d("myLogs", "Click end");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_taruf:
                startActivity(new Intent("ua.android.d2.komunalka.taruf"));//добавления новых тарифов
                break;
            case R.id.open_taruf_value:
                startActivity(new Intent("ua.android.d2.komunalka.taruf_value"));//добавления значений тарифов
                break;
            case R.id.instruction:
                startActivity(new Intent(this, InstructionActivity.class));// открить инструкции
                break;
            case R.id.exit:
                finish();//закрыть програму
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //инициализация компонентов
    private void initializationComponent() {
        Log.d("myLogs", "MainActivity begin onCreate");
        tvRezult = (TextView) findViewById(R.id.tvResult);
        btnAction = (Button) findViewById(R.id.btnAction);
        btnAction.setOnClickListener(this);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        tvPrevision = (TextView) findViewById(R.id.tvPrevision);
        tvActual = (TextView) findViewById(R.id.tvActual);
        etPrevision = (EditText) findViewById(R.id.etPrevision);
        etActual = (EditText) findViewById(R.id.etActual);
        spListCommunal = (Spinner) findViewById(R.id.sp);
        spListCommunal.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, new Dao(new DBHelper(this).getReadableDatabase()).selectName()));
        loadData();
        Log.d("myLogs", "MainActivity end onCreate");
    }

    // проверка на корректные данные
    private boolean check() {
        try {
            if (!etPrevision.getText().toString().equals("") && !etActual.getText().toString().equals("") && Double.parseDouble(etPrevision.getText().toString()) >= 0 && Double.parseDouble(etActual.getText().toString()) >= 0 && Double.parseDouble(etPrevision.getText().toString()) < Double.parseDouble(etActual.getText().toString())) {
                return true;
            } else {
                Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
            Toast.makeText(this, "Введите корректные данные", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // сохранения состояния полей
    private void saveData() {
        SharedPreferences sPre = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPre.edit();
        ed.putString("etPrevision", etPrevision.getText().toString());
        ed.putString("etActual", etActual.getText().toString());
        ed.putString("tvRezult", tvRezult.getText().toString());
        ed.commit();
    }

    // загрузка  состояния полей
    private void loadData() {
        SharedPreferences sPre = getPreferences(MODE_PRIVATE);
        etPrevision.setText(sPre.getString("etPrevision", ""));
        etActual.setText(sPre.getString("etActual", ""));
        tvRezult.setText(sPre.getString("tvRezult", ""));
    }

    // очистить поля
    private void clear() {
        etPrevision.setText("");
        etActual.setText("");
        tvRezult.setText("");
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }
    //расчёт результатов
    private void calculation() {
        try {
            if (check()) {
                Tariff t = new Dao(new DBHelper(this).getReadableDatabase()).selectTariff(spListCommunal.getSelectedItem().toString());
                if (t != null) {
                    double rezult = AdditionalMetods.format(Double.parseDouble(etActual.getText().toString()) - Double.parseDouble(etPrevision.getText().toString()), 2);
                    double bufRezult = 0.0;
                    StringBuilder st = new StringBuilder();
                    if (t.getValue().size() > 1) {
                        double mas[] = new double[t.getValue().size()];
                        double[] value = new double[t.getValue().size()];
                        List<Double> list = new ArrayList<>();
                        int j = 0;
                        for (Map.Entry<Double, Double> d : t.getValue().entrySet()) {
                            mas[j] = d.getKey();
                            value[j] = d.getValue();
                            j++;
                        }
                        for (int i = 0; i < mas.length; i++) {
                            try {
                                bufRezult = AdditionalMetods.format(rezult - mas[i + 1], 2);
                                if (bufRezult < 0) {
                                    list.add(rezult);
                                    break;
                                } else if (bufRezult > 0) {
                                    list.add(mas[i + 1]);
                                    rezult = bufRezult;
                                }
                            } catch (Exception e) {
                                list.add(rezult);
                            }
                        }
                        bufRezult = 0;
                        for (int i = 0; i < list.size(); i++) {
                            bufRezult += list.get(i) * value[i];
                            st.append(list.get(i)).append("*").append(value[i]);
                            if (i < list.size() - 1) st.append("+");
                        }
                    } else {
                        bufRezult = rezult * t.getOneRows();
                        st.append(rezult).append("*").append(t.getOneRows());
                    }
                    st.append("=").append(AdditionalMetods.format(bufRezult, 2)).append(" ").append(currency);
                    tvRezult.setText(st);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLogs", e.toString());
        }
    }
}
