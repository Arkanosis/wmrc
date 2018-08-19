package net.arkanosis.wmrc.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar);
        web_view_id.loadData(getResources().openRawResource(R.raw.diff).reader().use { it.readText() }, "text/html; charset=utf-8", null)
        web_view_id.setInitialScale(1)
        web_view_id.getSettings().setUseWideViewPort(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }

}
