package pl.kamilkrol.cities

import android.app.AlertDialog
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.VisibilityAwareImageButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.spinner_item_layout.*

class MapFragment : Fragment(), OnMapReadyCallback {
    private var fabShown: Boolean = false
    private var polyLine : Polyline? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mMap: GoogleMap
    private var cities: MutableLiveData<List<City>> = MutableLiveData<List<City>>().apply {
        value = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.findFragmentByTag("map")?.let { mapFragment ->
            (mapFragment as SupportMapFragment).getMapAsync(this)
        } ?: run {
            val mapFragment = SupportMapFragment.newInstance(
                GoogleMapOptions()
                    .camera(CameraPosition.fromLatLngZoom(LatLng(51.109286, 17.032307), 5.0f))
                    .compassEnabled(true)
                    .zoomControlsEnabled(true)
            )
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment, "map").commit()
            mapFragment.getMapAsync(this)
        }

        FirebaseDatabase.getInstance().reference.child("cities").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.i("info", "cancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<City>()
                snapshot.children.forEach { dataSnapshot ->
                    val city = dataSnapshot.getValue(City::class.java)
                    city?.also {
                        newList.add(it)
                        Log.i("info", "city ${it.name} added")
                    } ?: Log.i("info", "city was not added")
                }
                cities.value = newList
            }
        })

        fab.setOnClickListener {
            fab.hide()
            fabShown = false
            polyLine?.points = polyLine?.points?.apply{ clear()}
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                cities.value?.forEach {
//                    if (it.name == (spinner.adapter as ArrayAdapter<String> ).getItem(position)) {
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
//                        return@forEach
//                    }
//                }
                val c = cities.value?.get(position)
                c?.run {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(this.latitude, this.longitude)))
                }
            }

        }
        spinner.adapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item_layout)
        cities.observe(this, Observer{
            val arrAdapter = (spinner.adapter as ArrayAdapter<String> )
            arrAdapter.clear()
            it?.forEach {
                arrAdapter.add(it.name)

            } ?: Log.i("info", "Nothing added to adapter")
            (spinner.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        polyLine = mMap.addPolyline(PolylineOptions().apply {
            clickable(false)
            color(R.color.colorAccent)
        })

        mMap.setOnMapLongClickListener {
            showAddCityDialog(it)
        }
        mMap.setOnMarkerClickListener {
            polyLine!!.points = polyLine!!.points.apply { add(it.position) }
            if (!fabShown && polyLine?.run { points.size > 1 } == true ) {
                fab.show()
                fabShown = true
            }
            it.showInfoWindow()
            true
        }
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        cities.observe(this, Observer {
            it?.forEach { city ->
                mMap.addMarker(MarkerOptions().position(LatLng(city.latitude, city.longitude)).title(city.name))
            }
        })
    }

    private fun showAddCityDialog(it: LatLng) {

        val builder = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.add_city_dialog_title))
            val editText = EditText(requireContext())
            setView(editText)
            setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                if (editText.text.isNotBlank()) {
                    val name = editText.text.toString()
                    addCity(City(name, it.latitude, it.longitude))
                }
            }
            setNegativeButton(getString(R.string.cancel), null)
        }
        builder.show()
    }

    private fun addCity(city: City) {
        FirebaseDatabase.getInstance().reference.child("cities").child(city.name).setValue(city)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(city.latitude, city.longitude)))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }
}
