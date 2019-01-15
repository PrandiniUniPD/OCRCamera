package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import java.text.DecimalFormat;
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
 * @author Leonardo Pratesi                                          //LINES CALCULATED 200(statcalculatoractivity) + 150(statisticmanager) + 50(blurobject) + 150(blurgalleryactivity) + 100(blurobjectadapter) + 50(galleryactivity) + 50(resultactivity)
 */                                                                  // 200+150+50+150+100+50+50 = 750
public class StatCalculatorActivity extends AppCompatActivity {


    private HorizontalBarChart mChart;

    /**
     * map that contains the ingredients with the counter
     */
    private HashMap<String, Integer> statmap = new HashMap<>();

    /**
     * see documentation for Android chart view :
     * https://github.com/PhilJay/MPAndroidChart
     * License Apache 2.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatisticManager manager = new StatisticManager(getApplicationContext());
        //load map
        statmap = manager.loadMap();
        Log.i("HASHFINAL", statmap.toString());
        setContentView(R.layout.activity_stat_calculator);
        //objectview of the BarChart
        mChart = (HorizontalBarChart) findViewById(R.id.chart1);

        //set graph
        setData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticManager manager = new StatisticManager(getApplicationContext());
        statmap = manager.loadMap();

    }

    /**
     * Menu Inflater
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.clear_graph, menu);
        return true;
    }

    /**
     * Handling click events on the menu
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        StatisticManager manager = new StatisticManager(getApplicationContext());
        switch (item.getItemId()) {
            case R.id.statsDelete:
                manager.resetStats();
                finish();
                Toast.makeText(this, "statsCleared", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sort:
                statmap = manager.sortMap(statmap);
                setContentView(R.layout.activity_stat_calculator);
                mChart = (HorizontalBarChart) findViewById(R.id.chart1);
                setData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * method to populate the chart
     * @author Leonardo Pratesi
     */
    private void setData() {

        ArrayList<BarEntry> yVals = new ArrayList<>(); //values
        ArrayList<String> xVals = new ArrayList<>();   //keys

        int columnNumber = 0;
        for (Map.Entry<String, Integer> entry : statmap.entrySet()) {
                    yVals.add(new BarEntry(columnNumber, entry.getValue()));
                    xVals.add(trimStringIfTooLong(entry.getKey()));
                    columnNumber++;

        }

        Log.e("xVals",xVals.toString());
        String[] xValsToString = new String[xVals.size()];
        xValsToString = xVals.toArray(xValsToString);

        // if you want to show a description change this
        mChart.getDescription().setEnabled(false);
        BarDataSet set1;
        set1 = new BarDataSet(yVals, "");
        set1.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(set1);

       //barchart plotted
        mChart.setData(data);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        //set the number of names shown (always maximum here)
        xAxis.setLabelCount(statmap.size(),false);

        //show all the ingredients name
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xValsToString));

        //see barChart column values
        mChart.getAxisRight().setDrawLabels(false);
    }

    /**
     * Method to cut the names of the ingredients that are too long
     * @param inciname name of the ingredient that need to be trimmed
     * @return trimmed string
     */
    // TODO trim for better understanding
    private String trimStringIfTooLong(String inciname) {

            if (inciname != null && inciname.length() > 10)
                inciname = inciname.substring(0, 10)+ "..";
            return inciname;

    }
}

/**
 * Class to get the name of every bar in the graph
 */
class MyXAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[(int)value];
    }
}

