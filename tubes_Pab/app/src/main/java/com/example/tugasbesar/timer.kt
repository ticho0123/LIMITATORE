package com.example.tugasbesar

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tugasbesar.databinding.ActivitySettingBinding
import com.example.tugasbesar.databinding.ActivityTimerBinding
import com.example.tugasbesar.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import java.util.*
import kotlin.collections.HashMap
import kotlin.time.Duration

class timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    private lateinit var mCountDownTimer: CountDownTimer

    private var mTimerRunning = false

    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnHome.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.btnSetting.setOnClickListener{
            startActivity(Intent(this, Setting::class.java))
        }
        binding.imageView10.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.buttonSubmit.setOnClickListener(object : OnClickListener {
            var xp: Int =0
            override fun onClick(v: View) {
                val input : String = binding.submission.text.toString()
                if (input.isEmpty()) {
                    Toast.makeText(this@timer,"Field can't be empty",Toast.LENGTH_SHORT).show()
                    return
                }
                var menit = Integer.parseInt(input)

                val xps : Int  = (menit / 5)

                xp = xp+xps
                val uid = firebaseAuth.uid

                val hashMap: HashMap<String, Any?> = HashMap()

                hashMap["uid"] = uid
                hashMap["xp"] = xp

                val ref = FirebaseDatabase.getInstance().getReference("xperience")
                ref.child(uid!!)
                    .setValue(hashMap)

                val passer : Int = Integer.parseInt(input)

                val millisInput : Long = passer.toLong() * 60000
                if (millisInput == 0L) {
                    Toast.makeText(this@timer, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                    return
                }

                setTime(millisInput)
                binding.submission.setText("")
            }
        })

        binding.buttonStartPause.setOnClickListener {
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.buttonReset.setOnClickListener { resetTimer()

        }
    }

    fun setTime(milliseconds : Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
        closeKeyboard()
    }

    private var timeLeftFormatted = ""

    fun startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished : Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                updateWatchInterface()
            }
        }.start()

        mTimerRunning = true
        updateWatchInterface()
    }

    fun pauseTimer() {
        mCountDownTimer.cancel()
        mTimerRunning = false
        updateWatchInterface()
    }

    fun resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis
        updateCountDownText()
        updateWatchInterface()
    }

    fun updateCountDownText() {
        val hours : Int =  (mTimeLeftInMillis / 1000).toInt() / 3600
        val minutes : Int =  ((mTimeLeftInMillis / 1000) % 3600).toInt()  / 60
        val seconds : Long =  (mTimeLeftInMillis / 1000) % 60

        val timeLeftFormatted : String = if (hours > 0) {
            String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, seconds)
        }

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["timeLeftFormatted"] = timeLeftFormatted

        val refs = FirebaseDatabase.getInstance().getReference("Timer")
        refs.child(uid!!)
            .setValue(hashMap)
        binding.textViewCountdown.text =timeLeftFormatted

        if (timeLeftFormatted.equals("00:00")){
//            Masukin db buat xp nantinya
//            Ambil value dari editText nama nya mEditTextInput
        }
    }

    fun updateWatchInterface() {
        if (mTimerRunning) {
            binding.submission.visibility = INVISIBLE
            binding.buttonSubmit.visibility = INVISIBLE
            binding.buttonReset.visibility = INVISIBLE
            binding.buttonStartPause.setText(R.string.pause)
        } else {
            binding.submission.visibility = VISIBLE
            binding.buttonSubmit.visibility = VISIBLE
            binding.buttonStartPause.setText(R.string.start)

            if (mTimeLeftInMillis < 1000) {
                binding.buttonStartPause.visibility = INVISIBLE
            } else {
                binding.buttonStartPause.visibility = VISIBLE
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                binding.buttonReset.visibility = VISIBLE
            } else {
                binding.buttonReset.visibility = INVISIBLE
            }
        }
    }

    private fun closeKeyboard() {
        val view : View? = currentFocus
        if (view?.isActivated == true) {
            val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs : SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
//        SharedPreferences.Editor
        val editor : SharedPreferences.Editor = prefs.edit()

        editor.putLong("startTimeInMillis", mStartTimeInMillis)
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)

        editor.apply()

        mCountDownTimer.cancel()
    }

    override fun onStart() {
        super.onStart()

         val prefs : SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000)
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis)
        mTimerRunning = prefs.getBoolean("timerRunning", false)

        updateCountDownText()
        updateWatchInterface()

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0)
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis()

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0
                mTimerRunning = false
                updateCountDownText()
                updateWatchInterface()
            } else {
                startTimer()
            }
        }
    }
}