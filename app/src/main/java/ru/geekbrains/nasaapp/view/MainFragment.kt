package ru.geekbrains.nasaapp.view

import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.databinding.FragmentMainBinding
import ru.geekbrains.nasaapp.model.ApodResponseDTO
import ru.geekbrains.nasaapp.utils.ViewBindingDelegate
import ru.geekbrains.nasaapp.viewmodel.MainState
import ru.geekbrains.nasaapp.viewmodel.MainViewModel

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding: FragmentMainBinding by ViewBindingDelegate(FragmentMainBinding::bind)

    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) } // привязка viewModel к жизненному циклу фрагмента MainFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // подписываемся на изменения LiveData<AppState>
        // связка с жизненным циклом вьюхи(!) фрагмента MainFragment
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getApod()
    }

    private fun renderData(state: MainState) {
        when (state) {
            is MainState.Loading -> {
                binding.imageView.visibility = View.GONE
                binding.mainFragmentLoadingLayout.loadingLayout.visibility =
                    View.VISIBLE // отображаем прогрессбар
            }
            is MainState.Success -> {
                binding.imageView.visibility = View.VISIBLE
                binding.mainFragmentLoadingLayout.loadingLayout.visibility =
                    View.GONE // скрываем прогрессбар
                displayApod(state.data)
            }
            is MainState.Error -> {
                binding.imageView.visibility = View.VISIBLE
                binding.mainFragmentLoadingLayout.loadingLayout.visibility =
                    View.GONE // скрываем прогрессбар
                Toast.makeText(context, state.error.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun displayApod(data: ApodResponseDTO) {
        val url = data.hdurl ?: data.url
        //загрузка и отображение картинки
        //Coil в работе: достаточно вызвать у нашего ImageView
        //нужную extension-функцию и передать ссылку и заглушки для placeholder
        binding.imageView.load(url) {
            lifecycle(this@MainFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)

        }
    }
}