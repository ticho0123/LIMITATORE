package com.example.tugasbesar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.ProgressDialog
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import com.example.tugasbesar.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLgn.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
        }
        binding.btnSignUp.setOnClickListener{
            validateData()
        }
    }

    private var name =""
    private var email =""
    private var password =""
    private var xp = 0


    private fun validateData(){
        name = binding.etNama.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        if(name.isEmpty()){
            Toast.makeText(this, "Enter your name ...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email Pattern ...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Enter Password ...", Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAccount()
        }
    }

    private fun createUserAccount(){
        progressDialog.setMessage("Creating Account ...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo(){
        progressDialog.setMessage("Saving user info ...")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["name"] = name
        hashMap["email"] = email
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp


        val ref = FirebaseDatabase.getInstance().getReference("User")

        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account created ...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@signup, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

        val uids = auth.uid
        xp = 0

        val hashMaps: HashMap<String, Any?> = HashMap()
        hashMaps["uid"] = uids
        hashMaps["xp"] = xp

        val refs = FirebaseDatabase.getInstance().getReference("xperience")
        refs.child(uids!!)
            .setValue(hashMaps)
    }
}