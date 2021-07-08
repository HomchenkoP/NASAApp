package ru.geekbrains.nasaapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

open class BaseActivity : AppCompatActivity() {

    companion object {
        // наименование файла хранилища настроек
        private const val APP_PREFERENCES = "Preferences"

        // наименование настройки в файле настроек
        private const val NIGHT_MODE_KEY = "NightMode"

        fun setNightMode(mode: Boolean) {
            if (mode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливать тему надо только до установки макета активити
        setNightMode(nightModePref)
    }

    // Чтение настроек, параметр «ночной режим»
    private val nightModePref: Boolean
        get() {
            // getSharedPreferences() доступна только в активити
            val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
            return if (prefs.contains(NIGHT_MODE_KEY)) {
                // Чтение настроек, параметр «ночной режим», если настройка не найдена - взять по умолчанию false
                prefs.getBoolean(NIGHT_MODE_KEY, false)
            } else {
                false
            }
        }

    // Сохранение настроек
    fun savePrefs(switchNightMode: SwitchMaterial) {
        // getSharedPreferences() доступна только в активити
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val editor = prefs.edit()
        // Сохранение настроек, параметр «ночной режим»
        editor.putBoolean(NIGHT_MODE_KEY, switchNightMode.isChecked)
        editor.apply()
    }

    // Восстановление настроек
    fun loadPrefs(switchNightMode: SwitchMaterial) {
        // getSharedPreferences() доступна только в активити
        val prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        if (prefs.contains(NIGHT_MODE_KEY)) {
            // Чтение настроек, параметр «ночной режим», если настройка не найдена - взять по умолчанию false
            switchNightMode.isChecked = prefs.getBoolean(NIGHT_MODE_KEY, false)
        } else {
            switchNightMode.isChecked = false
        }
    }
}