package com.example.tugasbesar

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.renderscript.Sampler
import com.example.tugasbesar.databinding.ActivityHomeBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.example.tugasbesar.timer
import com.example.tugasbesar.databinding.ActivityTimerBinding
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class HomeActivity : AppCompatActivity() {

    lateinit var barChart: BarChart
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    var database: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    lateinit var calender: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var simpleDateFormats: SimpleDateFormat
    lateinit var date: String
    lateinit var dates: String
    var db: DatabaseReference? = null
    var dbs: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("User")
        db = database?.reference!!.child("Timer")
        dbs = database?.reference!!.child("xperience")

        loadProfile()

        calender = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        simpleDateFormats = SimpleDateFormat("HH:mm")
        date = simpleDateFormat.format(calender.time)
        dates = simpleDateFormats.format(calender.time)
        binding.textView23.text = date
        binding.textView27.text = dates

        barChart = findViewById(R.id.barChart)

        val labels = arrayListOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        barChart.setDrawGridBackground(false)
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false

        val entries = arrayListOf(
            BarEntry(0f, 1.30f),
            BarEntry(1f, 2.30f),
            BarEntry(2f, 3.0f),
            BarEntry(3f, 2.10f),
            BarEntry(4f, 4.0f),
            BarEntry(5f, 4.15f),
            BarEntry(6f, 3.15f),
        )

        val set = BarDataSet(entries, "Focus Time")
        set.setColors(Color.MAGENTA)
        set.valueTextSize = 10f
        set.valueTextColor = Color.BLACK

        barChart.data = BarData(set)
        barChart.invalidate()



        timerProfile()
        xp()

        binding.btnHome.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.btnMarket.setOnClickListener{
            startActivity(Intent(this, market::class.java))
        }
        binding.btnSetting.setOnClickListener{
            startActivity(Intent(this, Setting::class.java))
        }
        binding.btnSetting2.setOnClickListener{
            startActivity(Intent(this, Setting::class.java))
        }
        binding.btnTimer1.setOnClickListener{
            startActivity(Intent(this, timer::class.java))
        }
        binding.btnTimer.setOnClickListener{
            startActivity(Intent(this, timer::class.java))
        }
    }

    //private fun setBarChart(){
        //val entries = ArrayList<BarEntry>()
        //entries.add(BarEntry(1.45f, 0f))
        //entries.add(BarEntry(1.0f, 0f))
        //entries.add(BarEntry(2.30f, 0f))
        //entries.add(BarEntry(3.0f, 0f))
        //entries.add(BarEntry(2.0f, 0f))
        //entries.add(BarEntry(6.15f, 0f))
        //entries.add(BarEntry(3.15f, 0f))

        //val barDataSet = BarDataSet(entries, "Cells")



    private fun timerProfile(){
        val user = firebaseAuth.currentUser
        val userreference = db?.child(user?.uid!!)
        userreference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView31.text=snapshot.child("timeLeftFormatted").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun xp(){
        val user = firebaseAuth.currentUser
        val userreference = dbs?.child(user?.uid!!)
        userreference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView30.text=snapshot.child("xp").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadProfile(){
        val user = firebaseAuth.currentUser
        val userreference = databaseReference?.child(user?.uid!!)

        userreference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                binding.textView22.text="Hello, "+snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}