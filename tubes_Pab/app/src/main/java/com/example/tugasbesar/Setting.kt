 package com.example.tugasbesar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tugasbesar.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import com.google.firebase.auth.EmailAuthProvider
import java.util.*

 class Setting : AppCompatActivity() {

    lateinit var calender: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var date: String
    lateinit var binding: ActivitySettingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var database: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("User")

        val User = firebaseAuth.currentUser

        loadProfile()

        calender = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        date = simpleDateFormat.format(calender.time)
        binding.textView9.text = date

        binding.LogOut.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        binding.btnHome.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.btnTimer.setOnClickListener{
            startActivity(Intent(this, timer::class.java))
        }
        binding.btnBack.setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        binding.btnSave.setOnClickListener {
            changeProfile()
        }
    }
     private fun loadProfile(){
         val user = firebaseAuth.currentUser
         val userreference = databaseReference?.child(user?.uid!!)

         userreference?.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot){
                 binding.textView8.text=snapshot.child("name").value.toString()
                 binding.editText3.text=snapshot.child("email").value.toString()
                 binding.etNama.text=snapshot.child("name").value.toString()
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }
         })
     }
     private fun changeProfile() {

         if (binding.etPassword.text.isNotEmpty() && binding.etNPassword.text.isNotEmpty() && binding.etNPassword2.text.isNotEmpty()) {

             if (binding.etNPassword.text.toString().equals(binding.etNPassword2.text.toString())) {

                 val user = firebaseAuth.currentUser
                 if (user != null && user.email != null) {
                     val credential = EmailAuthProvider
                         .getCredential(user.email!!, binding.etPassword.text.toString())

                     user?.reauthenticate(credential)
                         ?.addOnCompleteListener {
                             if (it.isSuccessful) {
                                 Toast.makeText(this, "Re-Authentication success.", Toast.LENGTH_SHORT).show()
                                 user?.updatePassword(binding.etNPassword.text.toString())
                                     ?.addOnCompleteListener { task ->
                                         if (task.isSuccessful) {
                                             Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                                             firebaseAuth.signOut()
                                             startActivity(Intent(this, Login::class.java))
                                             finish()
                                         }
                                     }

                             } else {
                                 Toast.makeText(this, "Re-Authentication failed.", Toast.LENGTH_SHORT).show()
                             }
                         }
                 } else {
                     startActivity(Intent(this, Login::class.java))
                     finish()
                 }

             } else {
                 Toast.makeText(this, "Password mismatching.", Toast.LENGTH_SHORT).show()
             }

         } else {
             Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()
         }

     }
}