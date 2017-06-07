package com.example.akash.acisales;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;





public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private TextView asn;
    public String ason;
    ArrayList<HashMap<String, String>> salesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salesList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetSales().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.add:
            Toast.makeText(this,"Add is Selected", Toast.LENGTH_LONG).show();
            return(true);

        case R.id.reset:
            Toast.makeText(this,"Reset is Selected", Toast.LENGTH_LONG).show();
            return(true);
        case R.id.about:
            //Toast.makeText(this, "About is Selected", Toast.LENGTH_LONG).show();
            Intent in = new Intent(MainActivity.this,GraphActivity.class);
            startActivity(in);
            return(true);
        case R.id.exit:
            finish();
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

    private class GetSales extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Data is downloading",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... arg0) {
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
                    asn = (TextView) findViewById(R.id.ason);
                    ason = jsonObj.getString("ason");

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

            return ason;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            asn.setText("Sales Update Up to "+result);

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, salesList,
                    R.layout.list_item, new String[]{ "businessname","curr","SLSCurLMSD","SLSCurSPLY","ctotal","SLSCumm"},
                    new int[]{R.id.businessname,R.id.curr,R.id.SLSCurLMSD,R.id.SLSCurSPLY,R.id.ctotal,R.id.SLSCumm});
            lv.setAdapter(adapter);
        }
    }
}