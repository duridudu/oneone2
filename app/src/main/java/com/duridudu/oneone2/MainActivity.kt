package com.duridudu.oneone2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.duridudu.oneone2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fragment = Calender()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)

        // 트랜잭션 완료
        transaction.commit()
    }
}