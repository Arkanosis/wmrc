package net.arkanosis.wmrc.activities

import android.os.Bundle
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_about.*

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)
        val versionLabel = findViewById(R.id.about_version) as TextView
        versionLabel.setText("v" + BuildConfig.VERSION_NAME)
    }

}
