package org.javaapp.timer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.javaapp.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rankFragment = RankFragment()
        val startFramgment = StartFragment()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val baseFragment = rankFragment
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, baseFragment).commit()
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.fragment_rank -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, rankFragment).commit()
                    true
                }
                R.id.fragment_start -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, startFramgment).commit()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }


}