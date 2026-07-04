package pixela16.space.speedometer

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("SpeedoPrefs", Context.MODE_PRIVATE)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val rgLanguage = findViewById<RadioGroup>(R.id.rgLanguage)
        val rbHu = findViewById<RadioButton>(R.id.rbHu)
        val rbEn = findViewById<RadioButton>(R.id.rbEn)
        val rgUnit = findViewById<RadioGroup>(R.id.rgUnit)
        val rbKmh = findViewById<RadioButton>(R.id.rbKmh)
        val rbMph = findViewById<RadioButton>(R.id.rbMph)
        val btnResetOdometer = findViewById<Button>(R.id.btnResetOdometer)

        // Címkék a fordításhoz
        val tvSettingsTitle = findViewById<TextView>(R.id.tvSettingsTitle)
        val tvLanguageLabel = findViewById<TextView>(R.id.tvLanguageLabel)
        val tvUnitLabel = findViewById<TextView>(R.id.tvUnitLabel)

        // Betöltés
        val isEnglish = prefs.getBoolean("isEnglish", false)
        val useKmh = prefs.getBoolean("useKmh", true)

        if (isEnglish) rbEn.isChecked = true else rbHu.isChecked = true
        if (useKmh) rbKmh.isChecked = true else rbMph.isChecked = true

        // Fordítás frissítése ezen a képernyőn
        fun updateTexts(en: Boolean) {
            btnBack.text = if (en) "< Back" else "< Vissza"
            tvSettingsTitle.text = if (en) "Settings" else "Beállítások"
            tvUnitLabel.text = if (en) "Unit" else "Mértékegység"
            btnResetOdometer.text = if (en) "Reset Odometer" else "Odometer Nullázása"
        }
        updateTexts(isEnglish)

        // Mentés és nyelvváltás azonnal
        rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val en = checkedId == R.id.rbEn
            prefs.edit().putBoolean("isEnglish", en).apply()
            updateTexts(en)
        }

        rgUnit.setOnCheckedChangeListener { _, checkedId ->
            prefs.edit().putBoolean("useKmh", checkedId == R.id.rbKmh).apply()
        }

        // Vissza gomb (bezárja az ablakot)
        btnBack.setOnClickListener { finish() }

        // Odometer nullázás megerősítéssel
        btnResetOdometer.setOnClickListener {
            val isEn = prefs.getBoolean("isEnglish", false)
            val title = if (isEn) "Are you sure?" else "Biztosan nullázod?"
            val msg = if (isEn) "This will permanently delete the odometer data." else "Ezzel véglegesen törlöd az eddigi távolságot."
            val yes = if (isEn) "Yes" else "Igen"
            val no = if (isEn) "Cancel" else "Mégse"

            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(yes) { _, _ ->
                    prefs.edit().putFloat("odometerMeters", 0f).apply()
                }
                .setNegativeButton(no, null)
                .show()
        }
    }
}