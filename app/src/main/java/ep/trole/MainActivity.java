package ep.trole;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;


/**
 * Ta rešitev uporablja zunanje knjižnice.
 * <p>
 * Knjižnico Retrofit2 se uporabi za podajanje zahtevkov HTTP.
 * URL http://square.github.io/retrofit
 * <p>
 * Strežnikov odgovor (v formatu JSON) se avtomatično razčleni in pretvori v Javanske objekte
 * s pomočjo knjižnic GSON ter converter-gson.
 */
public class MainActivity extends AppCompatActivity implements Callback<MainActivity.TrolaResponse> {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private EditText station;
    private EditText line;
    private TextView arrivals;
    private Button searchButton;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.trola.si/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final TrolaService service = retrofit.create(TrolaService.class);

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
                final String station = MainActivity.this.station.getText().toString().trim();
                final String line = MainActivity.this.line.getText().toString().trim();

                if (!station.isEmpty() && !line.isEmpty()) {
                    final Call<TrolaResponse> rides = service.getRides(station, line);
                    rides.enqueue(MainActivity.this);
                } else if (!station.isEmpty()) {
                    final Call<TrolaResponse> rides = service.getRides(station);
                    rides.enqueue(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onResponse(Call<TrolaResponse> call, Response<TrolaResponse> response) {
        final List<Station> stations = response.body().stations;

        if (stations == null || stations.isEmpty()) {
            arrivals.setText(R.string.no_hits);
            return;
        }

        final StringBuilder sb = new StringBuilder();
        for (Station s : stations) {
            sb.append(s.name).append(" (").append(s.number).append("):\n");
            for (Bus b : s.buses) {
                if (!b.arrivals.isEmpty()) {
                    sb.append("  ").append(b.number).append("-")
                            .append(b.direction).append(": ")
                            .append(b.arrivals).append("\n");
                }
            }
        }
        arrivals.setText(sb.toString());
    }

    @Override
    public void onFailure(Call<TrolaResponse> call, Throwable t) {
        Log.wtf(TAG, "Failure: " + t.getMessage(), t);
    }

    interface TrolaService {
        @Headers("accept: application/json")
        @GET("{station}/")
        Call<TrolaResponse> getRides(@Path("station") String station);

        @Headers("accept: application/json")
        @GET("{station}/{line}/")
        Call<TrolaResponse> getRides(@Path("station") String station, @Path("line") String line);
    }

    static class TrolaResponse {
        List<Station> stations;
    }

    static class Station {
        String number, name;
        List<Bus> buses;
    }

    static class Bus {
        String direction, number;
        List<Integer> arrivals;
    }
}
