package ru.geekbrains.nasaapp.view

import ru.geekbrains.nasaapp.R

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ApiActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        val viewPager: ViewPager = findViewById<ViewPager>(R.id.view_pager)
        val bottomNavigation: BottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        bottomNavigation.setOnNavigationItemReselectedListener { menuItem -> }
    }
}