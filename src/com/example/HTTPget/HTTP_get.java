package com.example.HTTPget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.net.URLEncoder;

import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HTTP_get extends Activity {
    /**
     * Called when the activity is first created.
     */

    // JSON Node names
    private static final String TAG_fields = "fields";
    private static final String TAG_timestamp = "timestamp";
    private static final String TAG_sensor = "sensor";
    private static final String TAG_value = "value";
    private static final String TAG_node = "Android";

    // contacts JSONArray
    JSONArray node = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    // object creation statement
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final String limit = sharedPrefs.getString(SettingsActivity.KEY_LIMIT, "");
        final String project = sharedPrefs.getString(SettingsActivity.KEY_PROJECTID, "200");
        final String sensor = sharedPrefs.getString(SettingsActivity.KEY_SENSORID, "413");
        final String api = sharedPrefs.getString(SettingsActivity.KEY_API_KEY, "please set!");

        TextView content = (TextView) findViewById(R.id.content);
        final Button SendMe = (Button) findViewById(R.id.SendMe);

        // method invocation statement
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        content.setText("Setari : "+limit+" "+project+" "+sensor+" "+api);



//GRAPH -begin ------------------------------------------------------------------------------
//graficele se pot afisa si direct intr-un php aflat pe telefon :)
//http://moinur-rahman.blogspot.ro/2012/04/how-to-plot-charts-and-graphs-for.html
//libraria si exemplele din implement curenta se gasesc aici:
// http://android-graphview.org/#doc_styles

//        int num = 150;
//        GraphViewData[] data = new GraphViewData[num];
//        double v=0;
//        for (int i=0; i<num; i++) {
//            v += 0.2;
//            data[i] = new GraphViewData(i, Math.sin(v));
//        }
//
//        GraphView graphView = new BarGraphView(
//                this // context
//                , "Job Status Graph" // heading
//        );
//        // add data
//        graphView.addSeries(new GraphViewSeries(data));
//        // set view port, start=2, size=40
//        graphView.setViewPort(2, 40);
//        graphView.setScrollable(true);
//        // optional - activate scaling / zooming
//        graphView.setScalable(true);
//
//        //graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today", "tomorrow"});
//        //graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
//
//        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//        layout.addView(graphView);

//GRAPH -end ------------------------------------------------------------------------------

        SendMe.setOnClickListener(
          new Button.OnClickListener(){
                public void onClick(View v)
                {
                //TODO aici era un encoding la sfarsitul functiei, de vazut ce face
                String limitvalue    = URLEncoder.encode(limit);
                String projectvalue    = URLEncoder.encode(project);
                String sensorvalue    = URLEncoder.encode(sensor);
                String apivalue    = URLEncoder.encode(api);

                //TODO de implementat si &format=simple JSON array ???
                // Server Request URL
                final String serverURL = "http://devicehub.net/io/project/"+projectvalue+"/sensor/"+sensorvalue+"/?apiKey="+apivalue+"&limit="+limitvalue;
                Log.d("URL ul este: ", serverURL);

                // Create Object and call AsyncTask execute Method
                new GetJSON().execute(serverURL);
                }
          }
        );

    }


    //http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
    //http://androidexample.com/JSON_Parsing_-_Android_Example/index.php?view=article_discription&aid=71&aaid=95
    //http://www.learn2crack.com/2013/10/android-json-parsing-url-example.html

//JSON PARSER - start -------------------------------------------------------------------------------
private class GetJSON extends AsyncTask<String, Void, Void> {
    private ProgressDialog pDialog = new ProgressDialog(HTTP_get.this);
    TextView uiUpdate = (TextView) findViewById(R.id.content);
    private String Error = null;
    private String timestamp;
    private String sensor;
    private String value;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(String... urls) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(urls[0], ServiceHandler.GET);
        String jsonResp = "{\"Android\" :" + jsonStr + "}";

        Log.d("Response: ", "> " + jsonResp);

        if (jsonStr != null) {
            try {
                // JSON Object from all HTTP response
                JSONObject jsonObj = new JSONObject(jsonResp);

                // Getting JSON Main Array node defined by the added "Android" prefix
                node = jsonObj.getJSONArray(TAG_node);

//                Log.d("JsonObj: ", "> " + jsonObj);

                // looping through all samples which were output
                for (int i = 0; i < node.length(); i++) {
                    JSONObject child = node.getJSONObject(i);
                    //pk = child.getString(TAG_pk);
                    //model = child.getString(TAG_model);
                    //Get the JSON object "field"
                    JSONObject fields = child.getJSONObject(TAG_fields);
                    //Get the sensor timestamp and value
                    timestamp = fields.getString(TAG_timestamp);
                    sensor = fields.getString(TAG_sensor);
                    value = fields.getString(TAG_value);
                    Log.d("value: ", "> " + value);
//TODO valorile parsate trebuies puse intr-un array si trimise catre grafic
                }
            } catch (JSONException e) {
                Log.d("ceva e naspa!!!", "> ");
               e.printStackTrace();
                Error = "naspa";
                cancel(true);
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            pDialog.dismiss();

            if (Error != null) {
                uiUpdate.setText("Output : "+Error);
            } else {
                Log.d("JsonObj: ", "> "+timestamp+" "+sensor+" "+value);
                uiUpdate.setText("Output : "+timestamp+" "+sensor+" "+value);
            }
        }
}
//JSON PARSER - end -------------------------------------------------------------------------------



//    // Class with extends AsyncTask class
//    private class LongOperation  extends AsyncTask<String, Void, Void> {
//
//        private final HttpClient Client = new DefaultHttpClient();
//        private String Content;
//        private String Error = null;
//        private ProgressDialog Dialog = new ProgressDialog(HTTP_get.this);
//
//        TextView uiUpdate = (TextView) findViewById(R.id.content);
//
//
//        protected void onPreExecute() {
//            // NOTE: You can call UI Element here.
//
//            //UI Element
//            uiUpdate.setText("Output : ");
//            Dialog.setMessage("Downloading source..");
//            Dialog.show();
//        }
//
//        // Call after onPreExecute method
//        protected Void doInBackground(String... urls) {
//
//            try {
//
//                // Call long running operations here (perform background computation)
//                // NOTE: Don't call UI Element here.
//
//                // Server url call by GET method
//                HttpGet httpget = new HttpGet(urls[0]);
//                ResponseHandler<String> responseHandler = new BasicResponseHandler();
//                Content = Client.execute(httpget, responseHandler);
//
//            } catch (ClientProtocolException e) {
//                Error = e.getMessage();
//                cancel(true);
//            } catch (IOException e) {
//                Error = e.getMessage();
//                cancel(true);
//            }
//
//            return null;
//        }
//
//        protected void onPostExecute(Void unused) {
//            // NOTE: You can call UI Element here.
//
//            // Close progress dialog
//            Dialog.dismiss();
//
//            if (Error != null) {
//
//                uiUpdate.setText("Output : "+Error);
//
//            } else {
//
//                uiUpdate.setText("Output : "+Content);
//
//            }
//        }
//
//    }

//OPTIONS menu-----------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_refresh:
                 return false;
        }
        return false;
    }
}
//OPTIONS menu-------------------------------------------------------------------------------------
