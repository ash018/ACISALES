package com.example.akash.acisales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
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
        lv.setBackgroundColor ( Color.WHITE );



        if (isConnected(getApplicationContext())) {
            new GetSales().execute();


        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please Connect Internet or Wi-Fi!!!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Toast.makeText(this, "Add is Selected", Toast.LENGTH_LONG).show();
                return (true);

            case R.id.reset:
                Toast.makeText(this, "Reset is Selected", Toast.LENGTH_LONG).show();
                return (true);
            case R.id.about:

                Intent in = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(in);
                return (true);
            case R.id.exit:
                finish();
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }

    private class GetSales extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Data is downloading", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = "http://dashboard.acigroup.info/leveldashboard/salesdata/";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


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

                        if (curr == "null") {

                            curr = "0.00";
                        }

                        if (SLSCurLMSD == "null") {

                            SLSCurLMSD = "0.00";
                        }

                        if (SLSCurSPLY == "null") {

                            SLSCurSPLY = "0.00";
                        }

                        if (ctotal == "null") {

                            ctotal = "0.00";
                        }

                        if (SLSCumm == "null") {

                            SLSCumm = "0.00";
                        }

                        DecimalFormat dfLMSD = new DecimalFormat("#.00");
                        float valuecurr = Float.parseFloat(curr);
                        float valueLMSD = Float.parseFloat(SLSCurLMSD);
                        float valueSLSCurSPLY = Float.parseFloat(SLSCurSPLY);
                        float valuectotal = Float.parseFloat(ctotal);
                        float valueSLSCumm = Float.parseFloat(SLSCumm);

                        curr = dfLMSD.format(valuecurr);
                        SLSCurLMSD = dfLMSD.format(valueLMSD);
                        SLSCurSPLY = dfLMSD.format(valueSLSCurSPLY);
                        ctotal = dfLMSD.format(valuectotal);
                        SLSCumm = dfLMSD.format(valueSLSCumm);

                        HashMap<String, String> SalesData = new HashMap<>();


                        SalesData.put("businessname", businessname);
                        SalesData.put("curr", curr);
                        SalesData.put("SLSCurLMSD", SLSCurLMSD);
                        SalesData.put("SLSCurSPLY", SLSCurSPLY);
                        SalesData.put("ctotal", ctotal);
                        SalesData.put("SLSCumm", SLSCumm);

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

        @SuppressWarnings("ResourceAsColor")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            asn.setText("Sales Update Up to " + result);




            setView();



        }

        public void setView(){

            /*
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, salesList,
                    R.layout.list_item, new String[]{"businessname", "curr", "SLSCurLMSD", "SLSCurSPLY", "ctotal", "SLSCumm"},
                    new int[]{R.id.businessname, R.id.curr, R.id.SLSCurLMSD, R.id.SLSCurSPLY, R.id.ctotal, R.id.SLSCumm});

            //TextView tv = (TextView) findViewById ( R.id.ctotal );
            //tv.setTextColor ( R.color.green );
            lv.setAdapter (adapter);
*/


            CustomAdapter customAdapter = new CustomAdapter ( MainActivity.this,salesList,R.layout.list_item,new String[]{"businessname", "curr", "SLSCurLMSD", "SLSCurSPLY", "ctotal", "SLSCumm"},
                    new int []{R.id.businessname, R.id.curr, R.id.SLSCurLMSD, R.id.SLSCurSPLY, R.id.ctotal, R.id.SLSCumm} );

            lv.setAdapter ( customAdapter );




/*
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    TextView sply = (TextView) view.findViewById( R.id.SLSCurSPLY);
                    String splyStr = sply.getText().toString ();
                    float splyFloat = Float.parseFloat ( splyStr );
                    if(splyFloat > 1.00) {
                        sply.setBackgroundResource (R.color.green);
                    }
                    else {
                        sply.setBackgroundResource (R.color.red);
                    }

                    TextView lmsd = (TextView) view.findViewById( R.id.SLSCurLMSD);
                    String lmsdStr = lmsd.getText().toString ();
                    float lmsdFolat = Float.parseFloat ( lmsdStr );
                    if(lmsdFolat > 1.00) {
                        lmsd.setBackgroundResource (R.color.green);
                    }
                    else {
                        lmsd.setBackgroundResource (R.color.red);
                    }
                }
            });
            */
        }



    }


    public Boolean isConnected(Context context) {
        Boolean status = false;

        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInf = conMgr.getAllNetworkInfo();

        for (NetworkInfo inf : netInf) {
            if (inf != null) {
                if (inf.getTypeName().contains("WIFI")
                        || inf.getTypeName().toUpperCase().contains("MOBILE")) {
                    if (inf.getState() == NetworkInfo.State.CONNECTED) {
                        status = true;
                        break;
                    }
                }
            }
        }

        return status;

    }

}