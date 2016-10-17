package aviadapps.flip;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    Button btnDaily;
    TextView textView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        btnDaily = (Button)findViewById(R.id.btnDaily);
        btnDaily.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnDaily:
                getQuotes getQuotes = new getQuotes();
                getQuotes.execute();
            break;
        }
    }

        public class getQuotes extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
        protected String doInBackground(Void... params) {
            String apiID = "", urlAddress;
            Uri.Builder builder = new Uri.Builder();
            /*
            builder.scheme("http");
            builder.authority("quotes.rest");
            builder.appendPath("quote.json");
            builder.appendQueryParameter("api_key", apiID);
            urlAddress = builder.build().toString();
            */
            urlAddress = "https://favqs.com/api/qotd";
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            String dailyQuotes = "", check = "";
            HttpURLConnection urlConnection = null;
            JSONObject jsonObject = null;

            try {
                URL url = new URL(urlAddress);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.e(LOG_TAG, "URL Connected: " + urlAddress);

                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) return null;

                dailyQuotes = buffer.toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(dailyQuotes);
                    JSONObject json = jsonObject1.getJSONObject("quote");
                    /*JSONArray jsonArray = json.getJSONArray("tags");
                    dailyQuotes = "Quote kind: ";
                    for(int i = 0; i < jsonArray.length(); i++) {
                        if(i + 1 >= jsonArray.length())
                            dailyQuotes += jsonArray.get(i);
                        else dailyQuotes += jsonArray.get(i) + ", ";
                    }
                    */
                    dailyQuotes = json.getString("body");
                    dailyQuotes += "\n\n";
                    dailyQuotes += "~" + json.getString("author");
                    Log.e(LOG_TAG, "JSON check: " + dailyQuotes);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
            catch(IOException e) {
                Log.e(LOG_TAG, "Connection failed");
                return null;
            }

            finally {
                if(urlConnection != null) urlConnection.disconnect();

                if(reader != null) {
                    try {
                        reader.close();
                    } catch(final IOException e) {
                        Log.e(LOG_TAG, "Error: " + e);
                    }
                }
            }
            return dailyQuotes;
        }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.GONE);
                if(result != null) {
                    Log.e(LOG_TAG, result);
                    textView.setText(result);
                }
                else textView.setText("Error occured");
            }
        }


}
