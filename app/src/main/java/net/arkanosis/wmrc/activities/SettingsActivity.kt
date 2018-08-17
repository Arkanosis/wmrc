package net.arkanosis.wmrc.activities

import android.os.Bundle

import kotlinx.android.synthetic.main.activity_main.*

import net.arkanosis.wmrc.R

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.activity_settings_title);
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar);
    }

}
