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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val bottomBar = binding.bottomNavigation
        setContentView(view)
        userViewModel  = ViewModelProvider(this)[UserViewModel::class.java]
        Log.d("MAIN++1", "TEST")
        CoroutineScope(Dispatchers.Main).launch{
            // 코루틴 내에서 getUser() 메서드 호출
            val user: User = userViewModel.getUser()
            Log.d("MainActivity", "User: $user")
            val names = user.name
            // 여기서 UI를 업데이트하는 작업 수행
            Toast.makeText(applicationContext, "환영합니다, ${user.name}!", Toast.LENGTH_SHORT).show()
        }


        //val name: User = userViewModel.getUser()
        //Log.d("MAIN++2", name.name)

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
                      changeFragment(Lists())
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