package com.duridudu.oneone2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.duridudu.oneone2.databinding.ActivityMainBinding
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val bottomBar = binding.bottomNavigation
        setContentView(view)
        userViewModel  = ViewModelProvider(this)[UserViewModel::class.java]
        //val name: User = userViewModel.getUser()
       //Log.d("MAIN++", name.name)
        //Toast.makeText(this, "환영합니다, ${name}!", Toast.LENGTH_SHORT).show()
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