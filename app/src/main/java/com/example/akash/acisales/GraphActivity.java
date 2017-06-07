package com.example.akash.acisales;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;





public class GraphActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<HashMap<String, String>> salesList;

    private XYPlot plot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_xy_plot_example);

        plot = (XYPlot) findViewById(R.id.plot);

        final String[] domainLabels = {"AAAA","qqqq", "Consumer", "ACI SALT", "Pharma", "Paint", "Ffffsfsdf", "xxxx", "zzzz", "cccc", "vvvv"};
        Number[] series1Numbers = {1, 4,6, 2, 8, 4, 16, 8, 32, 16, 64};
        Number[] series2Numbers = {5, 2, 9.6, 10, 5, 20, 10, 40, 20, 80, 40};

        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);

        LineAndPointFormatter series2Format =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_2);

        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        salesList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(GraphActivity.this,"Data is downloading",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://dashboard.acigroup.info/leveldashboard/salesdata/";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray sales = jsonObj.getJSONArray("salesdata");


                    for (int i = 0; i < sales.length(); i++) {
                        JSONObject c = sales.getJSONObject(i);
                        String businessname = c.getString("businessname");
                        String curr = c.getString("curr");
                        String SLSCurLMSD = c.getString("SLSCurLMSD");
                        String SLSCurSPLY = c.getString("SLSCurSPLY");
                        String ctotal = c.getString("ctotal");
                        String SLSCumm = c.getString("SLSCumm");

                        if(curr=="null") {

                            curr = "0.00";
                        }

                        if(SLSCurLMSD=="null") {

                            SLSCurLMSD = "0.00";
                        }

                        if(SLSCurSPLY=="null") {

                            SLSCurSPLY = "0.00";
                        }

                        if(ctotal=="null") {

                            ctotal = "0.00";
                        }

                        if(SLSCumm=="null") {

                            SLSCumm = "0.00";
                        }

                        DecimalFormat dfLMSD = new DecimalFormat("#.00");
                        float valuecurr = Float.parseFloat(curr);
                        float valueLMSD = Float.parseFloat(SLSCurLMSD);
                        float valueSLSCurSPLY = Float.parseFloat(SLSCurSPLY);
                        float valuectotal = Float.parseFloat(ctotal);
                        float valueSLSCumm = Float.parseFloat(SLSCumm);

                        curr = dfLMSD.format(valuecurr);
                        SLSCurLMSD  = dfLMSD.format(valueLMSD);
                        SLSCurSPLY = dfLMSD.format(valueSLSCurSPLY);
                        ctotal = dfLMSD.format(valuectotal);
                        SLSCumm = dfLMSD.format(valueSLSCumm);

                        HashMap<String, String> SalesData = new HashMap<>();

                        // adding each child node to HashMap key => value
                        SalesData.put("businessname", businessname);
                        SalesData.put("curr",   curr);
                        SalesData.put("SLSCurLMSD",   SLSCurLMSD);
                        SalesData.put("SLSCurSPLY", SLSCurSPLY);
                        SalesData.put("ctotal",ctotal);
                        SalesData.put("SLSCumm",SLSCumm);

                        salesList.add(SalesData);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            /*
            ListAdapter adapter = new SimpleAdapter(GraphActivity.this, salesList,
                    R.layout.list_item2, new String[]{ "businessname","curr"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);
            */
        }
    }
}
