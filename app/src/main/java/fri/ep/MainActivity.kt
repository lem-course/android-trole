package fri.ep

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val ADDRESS_BOTH = "https://www.trola.si/%s/%s/"
        const val ADDRESS_SINGLE = "https://www.trola.si/%s/"
        val TAG: String = MainActivity::class.java.canonicalName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_btn.setOnClickListener {
            val currentStation = station.text.toString().trim()
            val currentLine = line.text.toString().trim()
            arrivals.text = "$currentStation : $currentLine"
            Log.i(TAG, "Poizvedba za linijo $currentLine in postajo $currentStation")

            // TODO
        }
    }
}
