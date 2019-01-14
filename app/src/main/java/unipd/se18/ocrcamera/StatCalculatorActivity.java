package unipd.se18.ocrcamera;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import unipd.se18.ocrcamera.inci.Ingredient;

/**
 * Class to show the statistics about ingredients
 * @author Leonardo Pratesi
 */
public class StatCalculatorActivity extends AppCompatActivity {

    HorizontalBarChart mChart;

    /**
     * map that contains the ingredients with the counter
     */
    HashMap<String, Integer> statmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatisticManager manager = new StatisticManager(getApplicationContext());
        statmap = manager.loadMap();
        Log.i("HASHFINAL", statmap.toString());
        setContentView(R.layout.activity_stat_calculator);
        mChart = (HorizontalBarChart) findViewById(R.id.chart1);


        setData(12, 50);

    }

    /**
     * method to populate the chart
     *
     * @param count
     * @param range
     * @author Leonardo Pratesi
     */
    private void setData(int count, int range) {

        ArrayList<BarEntry> yVals = new ArrayList<>();
        float barWidth = 20f;
        float spaceForBar = 1f;
        int i = 0;
        for (Map.Entry<String, Integer> entry : statmap.entrySet()) {
            if (i <= 12){
                float val = (float) (entry.getValue());
                yVals.add(new BarEntry(i * spaceForBar, entry.getValue()));
                i++;
            }
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals, "Data Set1");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);

        final ArrayList<String> xVals = new ArrayList<>();

        Iterator it = statmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            xVals.add((String)pair.getKey());
        }
        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xVals));



        BarData data = new BarData(set1);

        mChart.setData(data);

    }


}