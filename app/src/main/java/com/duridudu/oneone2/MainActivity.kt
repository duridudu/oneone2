package com.duridudu.oneone2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.duridudu.oneone2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val bottomBar = binding.bottomNavigation
        setContentView(view)

        changeFragment(Calender())

        bottomBar.run{
          setOnItemSelectedListener{
              it ->
              when(it.itemId) {
                  R.id.tab1 -> {
                  changeFragment(Calender())
              }
                  R.id.tab2->{
                      changeFragment(Write())
                  }
                  R.id.tab3->{
                      changeFragment(List())
                  }
          }
              true

            }
        }

    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }


}