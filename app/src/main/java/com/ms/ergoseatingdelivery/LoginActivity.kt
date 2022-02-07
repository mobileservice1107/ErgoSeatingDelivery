package com.ms.ergoseatingdelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ms.ergoseatingdelivery.api.ApiInterface
import com.ms.ergoseatingdelivery.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(){
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnSignIn: Button
    lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        val isLoggedIn = Preferences(this@LoginActivity).loggedIn
        if(isLoggedIn) {
            finish()
            startMainActivity()
        }
    }

    private fun initView() {
        title = "Sign In"
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnSignIn = findViewById(R.id.btn_siginin)

        btnSignIn.setOnClickListener {
            onSignIn()
        }
        loadingDialog = LoadingDialog(this)

    }

    private fun onSignIn() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if(email.isEmpty()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return
        }
        if(password.isEmpty()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            etEmail.requestFocus()
            return
        }
        userLogin(email, password)
    }

    private fun userLogin(email: String, password: String) {
        loadingDialog.show()
        val apiInterface = ApiInterface.create().userLogin(email, password)
        apiInterface.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                loadingDialog.dismiss()
                val res = response.body()
                if (res != null && res.token.isNotEmpty()) {
                    Preferences(this@LoginActivity).loggedIn = true
                    Preferences(this@LoginActivity).token = res.token
                    startMainActivity()
                }

            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loadingDialog.dismiss()
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}