package pixela16.space.speedometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvCurrentSpeed: TextView
    private lateinit var tvUnit: TextView
    private lateinit var tvSpeedLabel: TextView
    private lateinit var tvOdometer: TextView
    private lateinit var btnStartStop: Button
    private lateinit var btnSettings: ImageButton
    private lateinit var btnStats: ImageButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var prefs: SharedPreferences

    private var isMeasuring = false
    private var maxSpeed = 0.0
    private var totalSpeed = 0.0
    private var measureCount = 0

    private var lastLocation: Location? = null
    private var odometerMeters = 0f
    private var sessionOdometerMeters = 0f // Ez törlődik az app bezárásakor

    private var useKmh = true
    private var isEnglish = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) startTracking() else {
            val msg = if (isEnglish) "GPS permission required!" else "GPS engedély nélkül nem működik!"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("SpeedoPrefs", Context.MODE_PRIVATE)

        tvCurrentSpeed = findViewById(R.id.tvCurrentSpeed)
        tvUnit = findViewById(R.id.tvUnit)
        tvSpeedLabel = findViewById(R.id.tvSpeedLabel)
        tvOdometer = findViewById(R.id.tvOdometer)
        btnStartStop = findViewById(R.id.btnStartStop)
        btnSettings = findViewById(R.id.btnSettings)
        btnStats = findViewById(R.id.btnStats)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnStats.setOnClickListener {
            val unitText = if (useKmh) "km/h" else "mph"
            val distUnitText = if (useKmh) "km" else "mi"

            val avgSpeed = if (measureCount > 0) totalSpeed / measureCount else 0.0
            val totalDistance = if (useKmh) odometerMeters / 1000f else odometerMeters / 1609.34f
            val sessionDistance = if (useKmh) sessionOdometerMeters / 1000f else sessionOdometerMeters / 1609.34f

            val title = if (isEnglish) "Statistics" else "Statisztika"
            val msgTemplate = if (isEnglish) {
                "Max speed: %.1f %s\nAverage speed: %.1f %s\nThis session: %.2f %s\nTotal distance: %.2f %s"
            } else {
                "Maximum sebesség: %.1f %s\nÁtlag sebesség: %.1f %s\nAktuális út: %.2f %s\nÖsszes megtett út: %.2f %s"
            }
            val okBtn = if (isEnglish) "OK" else "Rendben"

            val message = msgTemplate.format(maxSpeed, unitText, avgSpeed, unitText, sessionDistance, distUnitText, totalDistance, distUnitText)

            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okBtn, null)
                .show()
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location.hasSpeed()) {
                        processNewSpeed(location.speed)
                    }

                    if (isMeasuring && lastLocation != null) {
                        val distance = location.distanceTo(lastLocation!!)
                        odometerMeters += distance
                        sessionOdometerMeters += distance
                        prefs.edit().putFloat("odometerMeters", odometerMeters).apply()
                    }
                    lastLocation = location
                    updateOdometerDisplay()
                }
            }
        }

        btnStartStop.setOnClickListener {
            if (isMeasuring) {
                val title = if (isEnglish) "Stop tracking?" else "Biztosan leállítod?"
                val yes = if (isEnglish) "Yes" else "Igen"
                val no = if (isEnglish) "Cancel" else "Mégse"

                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setPositiveButton(yes) { _, _ -> stopTracking() }
                    .setNegativeButton(no, null)
                    .show()
            } else {
                checkPermissionAndStart()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        useKmh = prefs.getBoolean("useKmh", true)
        isEnglish = prefs.getBoolean("isEnglish", false)
        odometerMeters = prefs.getFloat("odometerMeters", 0f)

        tvSpeedLabel.text = if (isEnglish) "Speed" else "Sebesség"
        tvUnit.text = if (useKmh) "km/h" else "mph"
        updateButtonText()
        updateOdometerDisplay()
    }

    private fun checkPermissionAndStart() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTracking()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun startTracking() {
        isMeasuring = true
        btnStartStop.setBackgroundColor(android.graphics.Color.RED)
        updateButtonText()

        maxSpeed = 0.0
        totalSpeed = 0.0
        measureCount = 0
        lastLocation = null

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopTracking() {
        isMeasuring = false
        btnStartStop.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
        updateButtonText()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun updateButtonText() {
        if (isMeasuring) {
            btnStartStop.text = if (isEnglish) "Stop Tracking" else "Mérés leállítása"
        } else {
            btnStartStop.text = if (isEnglish) "Start Tracking" else "Mérés indítása"
        }
    }

    private fun processNewSpeed(speedMps: Float) {
        val currentSpeed = if (useKmh) speedMps * 3.6 else speedMps * 2.23694
        if (currentSpeed > maxSpeed) maxSpeed = currentSpeed
        totalSpeed += currentSpeed
        measureCount++
        tvCurrentSpeed.text = String.format("%.1f", currentSpeed)
    }

    private fun updateOdometerDisplay() {
        val unitText = if (useKmh) "km" else "mi"
        val totalDistance = if (useKmh) odometerMeters / 1000f else odometerMeters / 1609.34f
        val sessionDistance = if (useKmh) sessionOdometerMeters / 1000f else sessionOdometerMeters / 1609.34f

        val sessionLabel = if (isEnglish) "This session" else "Aktuális út"
        val totalLabel = if (isEnglish) "Total" else "Összes"

        tvOdometer.text = String.format("%s: %.2f %s\n%s: %.2f %s", sessionLabel, sessionDistance, unitText, totalLabel, totalDistance, unitText)
    }
}