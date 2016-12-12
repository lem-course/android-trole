package ep.trole;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

                // TODO: Implementirajte poizvedbo o prihodu avtobusev
            }
        });
    }
}
