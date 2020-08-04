package com.example.smartgardenapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartgardenapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val firstFragment: Fragment =
        FirstFragment()
    private val secondFragment: Fragment =
        SecondFragment()
    private val thirdFragment: Fragment =
        ThirdFragment()
    private val fourthFragment: Fragment =
        FourthFragment()
    private val fm:FragmentManager = supportFragmentManager
    private var active = firstFragment
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when(item.itemId){
            R.id.First -> {
                replaceFragment(firstFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.Second -> {
                replaceFragment(secondFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.Third -> {
                replaceFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.Fourth -> {
                replaceFragment(fourthFragment)
                return@OnNavigationItemSelectedListener true
            }

        }

        false

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fm.beginTransaction().add(R.id.fragmentContainer, fourthFragment,"4").hide(fourthFragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, thirdFragment,"3").hide(thirdFragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, secondFragment,"2").hide(secondFragment).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, firstFragment,"1").commit()


    }
    private fun replaceFragment(fragment: Fragment){
        fm.beginTransaction().hide(active).show(fragment).commit()
        active = fragment

    }
}
