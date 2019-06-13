package pl.kamilkrol.cities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        loadMainFragment()
    }

    private fun loadMainFragment() {
        if (supportFragmentManager.findFragmentByTag("map") == null) {
            supportFragmentManager.beginTransaction().replace(R.id.content, MapFragment(), "map").commit()
        }
    }


}
