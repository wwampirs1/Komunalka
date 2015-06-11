package ua.android.d2.komunalka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Julia on 17.05.2015.
 */
public class Tariff {

    private int id;
    private String name;
    private int tarufId;
    private Map<Double, Double> value;

    public Tariff() {

    }

    public Tariff(int id, int tarufId, Map<Double, Double> value) {
        this.tarufId = tarufId;
        this.value = value;
        this.id = id;
    }

    public Tariff(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTarufId() {
        return tarufId;
    }

    public void setTarufId(int tarufId) {
        this.tarufId = tarufId;
    }

    public Map<Double, Double> getValue() {
        return value;
    }

    public double getOneRows() {
        double rezult = 0;
        try {
            if (value.size() == 1) {
                for (double d : value.values()) rezult = d;
            }
            return rezult;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setValue(Map<Double, Double> value) {
        this.value = value;
    }

    public Tariff(String name, int tarufId, Map<Double, Double> value) {
        this.name = name;
        this.tarufId = tarufId;
        this.value = value;
    }

    public Tariff(int id, String name, int tarufId, Map<Double, Double> value) {
        this.id = id;
        this.name = name;
        this.tarufId = tarufId;
        this.value = value;
    }
}
