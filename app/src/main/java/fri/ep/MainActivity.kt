package fri.ep

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val ADDRESS_BOTH = "https://www.trola.si/%s/%s/"
        const val ADDRESS_SINGLE = "https://www.trola.si/%s/"
        val TAG: String = MainActivity::class.java.canonicalName!!
    }

    var task: LookUpTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_btn.setOnClickListener {
            val currentStation = station.text.toString().trim()
            val currentLine = line.text.toString().trim()
            arrivals.text = "$currentStation : $currentLine"
            Log.i(TAG, "Poizvedba za linijo $currentLine in postajo $currentStation")

            task = LookUpTask(arrivals)

            if (currentLine.isEmpty()) {
                task?.execute(currentStation)
            } else {
                task?.execute(currentStation, currentLine)
            }
        }
    }

    override fun onStop() {
        task?.cancel(true)
        task = null

        super.onStop()
    }

    class LookUpTask(private val arrivals: TextView) : AsyncTask<String, Unit, JSONObject>() {

        override fun doInBackground(vararg params: String): JSONObject? {
            if (params.isEmpty() || params.size > 2)
                throw IllegalArgumentException("Metoda potrebuje 1 ali 2 parametra")

            try {
                val url = if (params.size == 1)
                    URL(String.format(ADDRESS_SINGLE, params[0]))
                else
                    URL(String.format(ADDRESS_BOTH, params[0], params[1]))

                val conn = (url.openConnection() as HttpURLConnection).apply {
                    doInput = true
                    requestMethod = "GET"
                    setRequestProperty("accept", "application/json")
                }

                val scanner = Scanner(conn.inputStream).useDelimiter("\\A")
                val content = if (scanner.hasNext()) scanner.next() else ""

                return JSONObject(content)
            } catch (e: Exception) {
                Log.w(TAG, "Exception: ${e.localizedMessage}")
                return null
            }
        }

        override fun onPostExecute(jsonObject: JSONObject?) {
            arrivals.text = jsonObject?.toString(2)
        }
    }
}
