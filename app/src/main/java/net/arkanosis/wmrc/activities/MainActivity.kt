package net.arkanosis.wmrc.activities

import android.os.Bundle
import android.view.Menu

import kotlinx.android.synthetic.main.activity_main.*

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
