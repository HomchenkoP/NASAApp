package ru.geekbrains.nasaapp.view

import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ViewPagerAdapter(private val fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        private const val EARTH_FRAGMENT = 0
        private const val MARS_FRAGMENT = 1
        private const val WEATHER_FRAGMENT = 2
    }

    private val fragments = arrayOf(EarthFragment(), MarsFragment(), WeatherFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragments[EARTH_FRAGMENT]
            1 -> fragments[MARS_FRAGMENT]
            2 -> fragments[WEATHER_FRAGMENT]
            else -> fragments[EARTH_FRAGMENT]
        }
    }
}

@RequiresApi(21)
class DepthPageTransformer : ViewPager2.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.75f
    }

    override fun transformPage(view: View, position: Float) {
        // Когда страница целиком заполняет экран, значение ее позиции равно 0.
        // Когда страница только начинает прорисовываться с правой стороны экрана, значение ее позиции равно 1.
        // Когда пользователь прокручивает страницы на полпути между первой и второй,
        // первая страница имеет позицию -0,5, а вторая страница имеет позицию 0,5.
        view.apply {
            val pageWidth = view.width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // Эта страница находится за пределами экрана слева.
                    view.alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Используем исходные значения, пока страница появляется с левой стороны экрана.
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationZ = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Страница скрывается за правой стороной экрана.
                    // Становится прозрачной.
                    view.alpha = 1 - position

                    // Движение вправо заменяем движением в глубину:
                    // Компенсируем изменение координаты X;
                    view.translationX = pageWidth * -position
                    view.translationZ = -1f
                    // Уменьшаем масштаб страницы (от 1 до MIN_SCALE).
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // Эта страница находится за пределами экрана справа.
                    view.alpha = 0f
                }
            }
        }
    }
}

class ZoomOutPageTransformer : ViewPager2.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // Эта страница находится за пределами экрана слева.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Страница появляется с слевой стороны, становится полностью видна, затем скрывается за правой стороной.
                    // Масштаб страницы увеличивается от MIN_SCALE до 1, затем уменьшается от 1 до MIN_SCALE.
                    val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                    // Значения изменяются пропорционально масштабу
                    val vertMargin = pageHeight * (1 - scaleFactor) / 2
                    val horzMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horzMargin - vertMargin / 2
                    } else {
                        horzMargin + vertMargin / 2
                    }

                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Прозрачность изменяется пропорционально размеру страницы.
                    alpha = (MIN_ALPHA +
                            (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { // (1,+Infinity]
                    // Эта страница находится за пределами экрана справа.
                    alpha = 0f
                }
            }
        }
    }
}
