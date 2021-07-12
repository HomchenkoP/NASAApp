package ru.geekbrains.nasaapp.view

import android.os.Build
import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.databinding.FragmentSettingsBinding
import ru.geekbrains.nasaapp.utils.ViewBindingDelegate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val binding: FragmentSettingsBinding by ViewBindingDelegate(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Восстановление настроек из файла настроек
        (requireActivity() as BaseActivity).loadPrefs(binding.switchNightMode) // переключатель ночного режима

        // назначаем обработчик переключателя
        binding.switchNightMode.setOnClickListener(View.OnClickListener {
            // сохраним настройки
            (requireActivity() as BaseActivity).savePrefs(binding.switchNightMode) // переключатель ночного режима
            // пересоздадим активити, чтобы тема применилась
            if (Build.VERSION.SDK_INT >= 11) {
                requireActivity().recreate()
            } else {
                requireActivity().finish()
                requireActivity().startActivity(requireActivity().intent)
            }
        })
    }

}