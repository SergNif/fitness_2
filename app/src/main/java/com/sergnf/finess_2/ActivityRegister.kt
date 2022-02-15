package com.sergnf.finess_2

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sergnf.finess_2.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_register.*

class ActivityRegister : AppCompatActivity() {

    //    private lateinit var binding : ActivityMainBinding
    private lateinit var database: DatabaseReference


    //    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        to_login.setOnClickListener { onBackPressed() }
        registerBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(Email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@ActivityRegister,
                    "Пожалуйста введите Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@ActivityRegister,
                    "Пожалуйста введите Пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(fullName.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@ActivityRegister,
                    "Как к Вам обращаться? Имя.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                else -> {
                    val emailFB: String = Email.text.toString().trim() { it <= ' ' }
                    val passwordFB: String = password.text.toString().trim() { it <= ' ' }
                    val fullName = fullName.text.toString().trim() { it <= ' ' }
                    textCreateAcc.text = fullName


                    // Create an instanse and create a register a user with email and password.
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailFB, passwordFB)
                        .addOnCompleteListener { task ->
                            //If the registration is successfully done
                            if (task.isSuccessful) {
                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@ActivityRegister,
                                "Вы успешно зарегистрированы !",
                                    Toast.LENGTH_SHORT
                                ).show()
                                /**
                                 * Здесь пользователь авторизован и добавлен в Аутентификацию
                                 * добавим данные в Realtime Database
                                 */
//                            val fullName = fullName.text.toString()
//                            val lastName = lastName.text.toString()
//                            val passwordF = password.text.toString()
                                val email = Email.text.toString()

                                database = FirebaseDatabase.getInstance().getReference("UserData")
                                val UserDat = UsersData(fullName, email, passwordFB, email, email)
                                database.child(fullName).setValue(UserDat).addOnSuccessListener {

//                                    fullName.text.clear()
//                                lastName.text.clear()
                                    password.text.clear()
                                    Email.text.clear()

                                    Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT)
                                        .show()

                                }.addOnFailureListener {

                                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                                }

                                /** здесь новый пользователь зарегистрирован и автоматически залогинен,
                                 * мы должны перебросить его на Главный экран
                                 */
                                val intent =
                                    Intent(this@ActivityRegister, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("full_name_us", fullName)
                                startActivity(intent)
                                finish()
                            } else {
                                // Если регистрация не прошла, покажем ошибку
                                Toast.makeText(
                                    this@ActivityRegister,
                                task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

            }
        }


    }
}