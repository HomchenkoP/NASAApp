package ru.geekbrains.nasaapp.view

import ru.geekbrains.nasaapp.R
import ru.geekbrains.nasaapp.databinding.FragmentMainBinding
import ru.geekbrains.nasaapp.model.ApodResponseDTO
import ru.geekbrains.nasaapp.utils.indexesOf
import ru.geekbrains.nasaapp.utils.ViewBindingDelegate
import ru.geekbrains.nasaapp.viewmodel.MainState
import ru.geekbrains.nasaapp.viewmodel.MainViewModel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding: FragmentMainBinding by ViewBindingDelegate(FragmentMainBinding::bind)

    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) } // привязка viewModel к жизненному циклу фрагмента MainFragment

    private lateinit var title: TextView
    private lateinit var explanation: TextView

    private var imgIsExpanded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.bottom_sheet_title)
        explanation = view.findViewById(R.id.bottom_sheet_explanation)

        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://en.wikipedia.org/wiki/${binding.inputEditText.text.toString()}")
            })
        }
        binding.prevDate.setOnClickListener { changeApod(binding.currentDate.text.toString(), -1) }
        binding.nextDate.setOnClickListener { changeApod(binding.currentDate.text.toString(), +1) }

        binding.imageView.setOnClickListener {
            imgIsExpanded = !imgIsExpanded
            TransitionManager.beginDelayedTransition(
                binding.collapsing, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )

            val params: ViewGroup.LayoutParams = binding.imageView.layoutParams
            if (imgIsExpanded) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                binding.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            binding.imageView.layoutParams = params
        }

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

        hideApodDetails(newDate, viewModel::getApod) // вызов viewModel.getApod(newDate)
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
        explanation.text = spannedText(data.explanation, data.title)
        showApodDetails()
    }

    // выделяем в тексте все встретившиеся лексемы
    private fun spannedText(text: String?, tokens: String?): SpannableString {
        val tokenizer = StringTokenizer(tokens, " .,-:")
        // список лексем
        var tokensList: MutableList<String> = mutableListOf()

        while (tokenizer.hasMoreTokens()) {
            // помещаем в список лексемы, состоящие из трех и более символов
            tokenizer.nextToken().toString().takeIf { it.length >= 3 }?.also { tokensList.add(it) }
        }

        // список размещения лексем в тексте
        var spanStartEndList: MutableList<Pair<Int, Int>> = mutableListOf()

        tokensList.forEach { token ->
            spanStartEndList.addAll((text.indexesOf(token) as List<Int>).map {
                Pair(it, it + token.length)
            })
        }

        val spannable = SpannableString(text)

        spanStartEndList.forEach {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD_ITALIC),
                it.first,
                it.second,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        return spannable
    }

    private fun showApodDetails() {
        binding.imageView.animate()
            .alpha(1f)
            .duration = 1000

        binding.scrolledView.animate()
            .alpha(1f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {}
            })
    }

    private fun hideApodDetails(date: String, callback: (date: String) -> Unit) {
        binding.imageView.animate()
            .alpha(0f)
            .duration = 1000

        binding.scrolledView.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    callback(date)
                }
            })
    }
}