package ep.trole;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static final String ADDRESS_BOTH = "https://www.trola.si/%s/%s/";
    public static final String ADDRESS_SINGLE = "https://www.trola.si/%s/";

    private static final String TAG = MainActivity.class.getCanonicalName();

    private EditText station;
    private EditText line;
    private TextView arrivals;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        station = (EditText) findViewById(R.id.station);
        line = (EditText) findViewById(R.id.line);
        arrivals = (TextView) findViewById(R.id.arrivals);
        searchButton = (Button) findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentStation = station.getText().toString().trim();
                final String currentLine = line.getText().toString().trim();
                arrivals.setText(currentStation + " : " + currentLine);
                Log.i(TAG, "Poizvedba za linijo " + currentLine + " in postajo " + currentStation);

                final LookUpTask task = new LookUpTask(arrivals);

                if (currentLine.isEmpty()) {
                    task.execute(currentStation);
                } else {
                    task.execute(currentStation, currentLine);
                }
            }
        });
    }

    private static class LookUpTask extends AsyncTask<String, Void, JSONObject> {

        private final TextView arrivals;

        private LookUpTask(TextView arrivals) {
            this.arrivals = arrivals;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            if (params.length == 0 || params.length > 2)
                throw new IllegalArgumentException("Metoda potrebuje 1 ali 2 parametra");

            InputStream inStream = null;
            Scanner scanner = null;

            try {
                final URL url = params.length == 1 ?
                        new URL(String.format(ADDRESS_SINGLE, params[0])) :
                        new URL(String.format(ADDRESS_BOTH, params[0], params[1]));

                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("accept", "application/json");

                inStream = conn.getInputStream();
                scanner = new Scanner(inStream).useDelimiter("\\A");
                final String content = scanner.hasNext() ?
                        scanner.next() :
                        "";

                return new JSONObject(content);
            } catch (IOException | JSONException e) {
                Log.w(TAG, "Exception: " + e.getLocalizedMessage());
                return null;
            } finally {
                if (scanner != null) scanner.close();
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    arrivals.setText(jsonObject.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
