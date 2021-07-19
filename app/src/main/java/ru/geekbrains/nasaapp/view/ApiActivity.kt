package ru.geekbrains.nasaapp.view

import ru.geekbrains.nasaapp.R

import android.os.Build
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.relex.circleindicator.CircleIndicator3

class ApiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        val viewPager: ViewPager2 = findViewById<ViewPager2>(R.id.view_pager)
        val indicator: CircleIndicator3 = findViewById(R.id.circle_indicator)
        val bottomNavigation: BottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        viewPager.adapter = ViewPagerAdapter(this)
        if (Build.VERSION.SDK_INT >= 21) {
            viewPager.setPageTransformer(DepthPageTransformer())
        } else {
            viewPager.setPageTransformer(ZoomOutPageTransformer())
        }

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // связываем междусобой события ViewPager.PageSelected и BottomNavigationView.NavigationItemSelected
                when (position) {
                    0 -> bottomNavigation.menu.findItem(R.id.bottom_view_earth).setChecked(true)
                    1 -> bottomNavigation.menu.findItem(R.id.bottom_view_mars).setChecked(true)
                    2 -> bottomNavigation.menu.findItem(R.id.bottom_view_weather).setChecked(true)
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        indicator.setViewPager(viewPager)

        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // связываем междусобой события BottomNavigationView.NavigationItemSelected и ViewPager.PageSelected
                R.id.bottom_view_earth -> {
                    viewPager.setCurrentItem(0);
                    true
                }
                R.id.bottom_view_mars -> {
                    viewPager.setCurrentItem(1);
                    true
                }
                R.id.bottom_view_weather -> {
                    viewPager.setCurrentItem(2);
                    true
                }
                else -> false
            }
        }

        // повторное нажатие игнорируем
        bottomNavigation.setOnNavigationItemReselectedListener { _ -> }
    }
}