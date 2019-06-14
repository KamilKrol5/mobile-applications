package pl.kamilkrol.cities


import com.google.android.gms.maps.model.LatLng

data class City(
    var name: String = "no name",
    var latitude :Double = 0.0,
    var longitude: Double = 0.0
    )