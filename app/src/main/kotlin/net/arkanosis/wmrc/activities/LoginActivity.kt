package net.arkanosis.wmrc.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.AppCompatButton
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_login.*

import mu.KotlinLogging

import net.arkanosis.wmrc.R

class LoginActivity : BaseActivity() {

    private val logger = KotlinLogging.logger {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.activity_login_title);
        setContentView(R.layout.activity_login)

        val loginButton = findViewById(R.id.button_id) as AppCompatButton
        loginButton.setOnClickListener {
            logger.debug { "LOGIN!!" }
            val userapp = (findViewById(R.id.edit_login_id) as EditText).text.toString()
            val password = (findViewById(R.id.edit_password_id) as EditText).text.toString()
            if (userapp.contains("@") and (password.length > 0)) {
		// TODO actually log in to check the credentials, report an error if there's a problem
		val preferences = PreferenceManager.getDefaultSharedPreferences(this)
		with (preferences.edit()) {
		    putString("userapp", userapp)
		    putString("password", password)
		    commit()
		}
		val main  = Intent(this, MainActivity::class.java)
		main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
		startActivity(main)
            } else {
                // TODO display error message
            }
        }
    }

}
