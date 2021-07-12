package ru.geekbrains.nasaapp.view

import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.databinding.FragmentMainBinding
import ru.geekbrains.nasaapp.model.ApodResponseDTO
import ru.geekbrains.nasaapp.utils.ViewBindingDelegate
import ru.geekbrains.nasaapp.viewmodel.MainState
import ru.geekbrains.nasaapp.viewmodel.MainViewModel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding: FragmentMainBinding by ViewBindingDelegate(FragmentMainBinding::bind)

    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) } // привязка viewModel к жизненному циклу фрагмента MainFragment

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var title: TextView
    private lateinit var explanation: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetBehavior(view.findViewById(R.id.bottom_sheet_container))
        title = view.findViewById(R.id.bottom_sheet_title)
        explanation = view.findViewById(R.id.bottom_sheet_explanation)

        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${binding.inputEditText.text.toString()}")
            })
        }
        binding.prevDate.setOnClickListener { changeApod(binding.currentDate.text.toString(), -1) }
        binding.nextDate.setOnClickListener { changeApod(binding.currentDate.text.toString(), +1) }

        // подписываемся на изменения LiveData<AppState>
        // связка с жизненным циклом вьюхи(!) фрагмента MainFragment
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getApod()
    }

    private fun changeApod(currentDate: String, shift: Int) {
        lateinit var newDate: String
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(currentDate)
            calendar.add(Calendar.DATE, shift) // на день больше или меньше
            newDate = sdf.format(calendar.time)
        } catch (e: ParseException) {
            Log.e("", e.toString())
            newDate = sdf.format(Date())
        }

        if (newDate == sdf.format(Date())) {
            binding.nextDate.visibility = View.INVISIBLE
        } else {
            binding.nextDate.visibility = View.VISIBLE
        }

        viewModel.getApod(newDate)
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
        binding.currentDate.text = data.date
        val url = data.hdurl ?: data.url
        //загрузка и отображение картинки
        //Coil в работе: достаточно вызвать у нашего ImageView
        //нужную extension-функцию и передать ссылку и заглушки для placeholder
        binding.imageView.load(url) {
            lifecycle(this@MainFragment)
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
        title.text = data.title
        explanation.text = data.explanation
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                    BottomSheetBehavior.STATE_HIDDEN -> {}
                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }
}