package com.example.roseanna.massinfoquery;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    LinearLayout ll;
    TableLayout tl;
    Button increase, decrease, limit;
    String url;
    SQLiteDatabase sampleDB = null;
    String tableName = "MassPopulation";
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll          = (LinearLayout) findViewById(R.id.ll);
        increase    = (Button) findViewById(R.id.popIncrease);
        decrease    = (Button) findViewById(R.id.popDecrease);
        limit       = (Button) findViewById(R.id.popLimit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        url         = "https://malegislature.gov/District/CensusData";

        increase.setOnClickListener(this);
        decrease.setOnClickListener(this);
        limit.setOnClickListener(this);

        createDatabase();
        startView();
    }


    public void createDatabase(){
        try{
            sampleDB = openOrCreateDatabase("NAME", MODE_PRIVATE, null);
            createTable();
        }catch(SQLiteException se) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }
    }
    private void createTable() {
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName +
                " (City VARCHAR, " +
                "  PopulationOLD int ," +
                "  PopulationNEW int ," +
                "  Change FLOAT );");
    }

    private void insertIntoDB(String city, int pop1, int pop2, float change){
        ContentValues contentValues = new ContentValues();
        contentValues.put("City", city);
        contentValues.put("PopulationOLD", pop1);
        contentValues.put("PopulationNEW", pop2);
        contentValues.put("Change", change);
        sampleDB.insert(tableName, null, contentValues);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.popDecrease:
                descClick();
                break;
            case R.id.popIncrease:
                incrClick();
                break;
            case R.id.popLimit:
                limitClick();
                break;
        }
    }

    public void onResume(){
        super.onResume();
        this.deleteDatabase("NAME");
        createDatabase();
    }

    public void descClick(){
        String q = "Select * from " + tableName + " order by Change desc;";
        generateTop(q);
    }
    public void incrClick(){
        String q = "Select * from " + tableName + " order by Change;";
        generateTop(q);
    }
    public void limitClick(){
        String q = "Select * from " + tableName + " where PopulationNEW < 5000;";
        generateTable(q);
    }

    public void generateTop(String query){
        cursor = sampleDB.rawQuery(query, null);
        tl = (TableLayout) findViewById(R.id.table);
        tl.removeAllViews();

        if(cursor != null) {
            cursor.moveToFirst();
            TableRow title          = new TableRow(this);
            TextView cityTitle      = new TextView(this);
            TextView pop2000Title   = new TextView(this);
            TextView pop2010Title   = new TextView(this);
            TextView percent        = new TextView(this);

            cityTitle.setText("City Name ");
            pop2000Title.setText("2000     ");
            pop2010Title.setText("2010     ");
            percent.setText("Change    ");
            title.addView(cityTitle);
            title.addView(pop2000Title);
            title.addView(pop2010Title);
            title.addView(percent);
            tl.addView(title);
            if (cursor.moveToNext()){
                TableRow tr         = new TableRow(this);
                String city         = cursor.getString(cursor.getColumnIndex("City"));
                int pop2000         = cursor.getInt(cursor.getColumnIndex("PopulationOLD"));
                int pop2010         = cursor.getInt(cursor.getColumnIndex("PopulationNEW"));
                float change        = cursor.getFloat(cursor.getColumnIndex("Change"));
                TextView cityTV     = new TextView(this);
                TextView pop        = new TextView(this);
                TextView pop2       = new TextView(this);
                TextView changeTV   = new TextView(this);

                cityTV.setText(city);
                pop.setText(String.valueOf(pop2000));
                pop2.setText(String.valueOf(pop2010));
                changeTV.setText(String.format("%.2f",change) + "%");
                tr.addView(cityTV);
                tr.addView(pop);
                tr.addView(pop2);
                tr.addView(changeTV);
                tl.addView(tr);
            }
            cursor.close();
        }
    }
    public void generateTable(String query){
        cursor = sampleDB.rawQuery(query, null);
        tl = (TableLayout) findViewById(R.id.table);
        tl.removeAllViews();

        if(cursor != null) {
            cursor.moveToFirst();
            TableRow title          = new TableRow(this);
            TextView cityTitle      = new TextView(this);
            TextView pop2000Title   = new TextView(this);
            TextView pop2010Title   = new TextView(this);
            TextView percent        = new TextView(this);

            cityTitle.setText("City Name ");
            pop2000Title.setText("2000     ");
            pop2010Title.setText("2010     ");
            percent.setText("Change    ");
            title.addView(cityTitle);
            title.addView(pop2000Title);
            title.addView(pop2010Title);
            title.addView(percent);
            tl.addView(title);
            while (cursor.moveToNext()){
                TableRow tr         = new TableRow(this);
                String city         = cursor.getString(cursor.getColumnIndex("City"));
                int pop2000         = cursor.getInt(cursor.getColumnIndex("PopulationOLD"));
                int pop2010         = cursor.getInt(cursor.getColumnIndex("PopulationNEW"));
                float change        = cursor.getFloat(cursor.getColumnIndex("Change"));
                TextView cityTV     = new TextView(this);
                TextView pop        = new TextView(this);
                TextView pop2       = new TextView(this);
                TextView changeTV   = new TextView(this);

                cityTV.setText(city);
                pop.setText(String.valueOf(pop2000));
                pop2.setText(String.valueOf(pop2010));
                changeTV.setText(String.format("%.2f",change) + "%");
                tr.addView(cityTV);
                tr.addView(pop);
                tr.addView(pop2);
                tr.addView(changeTV);
                tl.addView(tr);
            }
            cursor.close();
        }
    }

    public void startView(){
        GetData getData = new GetData();
        getData.execute(url);
    }

    public class GetData extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        String regex, regex2;
        Pattern pattern, pattern2;
        Matcher m, m2;

        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            increase.setVisibility(View.GONE);
            decrease.setVisibility(View.GONE);
            limit.setVisibility(View.GONE);
            regex = "(<td scope=\"row\">)(\\w{2,})(</td>)";
            regex2 = "( <td class=\"number\">)(.*)(</td>)";
            if (pattern == null)
                pattern = Pattern.compile(regex);
            if (pattern2 == null)
                pattern2 = Pattern.compile(regex2);
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(reader);

                String line = br.readLine();
                ArrayList<Integer> temp = new ArrayList();
                while ((line = br.readLine()) != null) {
                    m = pattern.matcher(line);
                    if (m.find()) {
                        String newName = m.group(2);
                        int i = 0;
                        while (i < 2){
                            line = br.readLine();
                            m2 = pattern2.matcher(line);
                            if (m2.find()){
                                String pop = m2.group(2);
                                if (pop.contains(","))
                                    pop = pop.replace(",","");
                                int popInt = Integer.valueOf(pop);
                                temp.add(popInt);
                                i++;
                            }
                        }
                        if (temp.size() == 2) {
                            float change = (((float)temp.get(1) - (float)temp.get(0))/(float)temp.get(0)) * 100;
                            insertIntoDB(newName, temp.get(0), temp.get(1), change);
                            temp.clear();
                        }
                        else{
                            Log.i("temp size not 3", "wtf");
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return "done";
        }

        public void onPostExecute(String done){
            progressBar.setVisibility(View.GONE);
            increase.setVisibility(View.VISIBLE);
            decrease.setVisibility(View.VISIBLE);
            limit.setVisibility(View.VISIBLE);

            Log.i("post", "done");
        }

    }


}



