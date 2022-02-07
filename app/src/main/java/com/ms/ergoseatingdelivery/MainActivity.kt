package com.ms.ergoseatingdelivery

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.ms.ergoseatingdelivery.fragment.DeliveryFragment
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(){

    lateinit var btnFromDate: Button
    lateinit var btnToDate: Button
    private var fromCalendar = Calendar.getInstance()
    private var toCalendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    lateinit var fromDate: String
    lateinit var toDate: String

    lateinit var tabLayout: TabLayout
    lateinit var frameLayout: FrameLayout
    lateinit var fragment: DeliveryFragment
    lateinit var fragmentManager: FragmentManager
    lateinit var fragmentTransaction: FragmentTransaction

    lateinit var chairsFragment: DeliveryFragment
    lateinit var desksFragment: DeliveryFragment
    lateinit var accessoriesFragment: DeliveryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_log_out -> {
                Preferences(this).loggedIn = false
                Preferences(this).token = ""
                startLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        title = "Delivery List"
        initDatePicker()
        initTabLayout()
   }

    private fun initTabLayout() {
        tabLayout = findViewById(R.id.tabLayout)
        frameLayout = findViewById<FrameLayout>(R.id.frameLayout)

        chairsFragment = DeliveryFragment()
        chairsFragment.productType = "chair"

        desksFragment = DeliveryFragment()
        desksFragment.productType= "desk"

//        accessoriesFragment = DeliveryFragment()
//        accessoriesFragment.productType = "accessory"

        fragment = chairsFragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.commit()
        //adding listener for tab select
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // creating cases for fragment
                when (tab.position) {
                    0 -> fragment = chairsFragment
                    1 -> fragment = desksFragment
                }
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.frameLayout, fragment)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun initDatePicker() {
        btnFromDate  = findViewById(R.id.btn_from_date)
        btnToDate  = findViewById(R.id.btn_to_date)

        toCalendar.add(Calendar.DATE, 7)

        fromDate = sdf.format(fromCalendar.time)
        toDate = sdf.format(toCalendar.time)

        btnFromDate.text = fromDate
        btnToDate.text = toDate

        val fromDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            fromCalendar.set(Calendar.YEAR, year)
            fromCalendar.set(Calendar.MONTH, monthOfYear)
            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            btnFromDate.text = sdf.format(fromCalendar.time)
            fromDate = sdf.format(fromCalendar.time)
            fragment.loadDeliveryList()
        }
        btnFromDate.setOnClickListener {
            DatePickerDialog(this@MainActivity, fromDateSetListener,
                fromCalendar.get(Calendar.YEAR),
                fromCalendar.get(Calendar.MONTH),
                fromCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val toDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            toCalendar.set(Calendar.YEAR, year)
            toCalendar.set(Calendar.MONTH, monthOfYear)
            toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            btnToDate.text = sdf.format(toCalendar.time)
            toDate = sdf.format(toCalendar.time)
            fragment.loadDeliveryList()
        }
        btnToDate.setOnClickListener {
            DatePickerDialog(this@MainActivity, toDateSetListener,
                toCalendar.get(Calendar.YEAR),
                toCalendar.get(Calendar.MONTH),
                toCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}