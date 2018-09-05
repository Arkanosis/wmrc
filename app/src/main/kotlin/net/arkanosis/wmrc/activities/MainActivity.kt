package net.arkanosis.wmrc.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

class MainActivity : BaseActivity() {

    private val diffs = arrayOf(R.raw.diff1, R.raw.diff2)

    private var currentDiff = 0

    private fun switchDiff() {
        web_view_id.loadDataWithBaseURL(null, getResources().openRawResource(diffs[currentDiff]).reader().use { it.readText() }, "text/html", "utf-8", null)
        currentDiff = (currentDiff + 1) % diffs.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        web_view_id.setInitialScale(1)
        web_view_id.getSettings().setUseWideViewPort(true)

        switchDiff()

        val revertButton = findViewById(R.id.revert_id) as AppCompatButton
        val onClickListener: Any = revertButton.setOnClickListener {
            switchDiff()
        }

        val approveButton = findViewById(R.id.patrol_id) as AppCompatButton
        approveButton.setOnClickListener {
            switchDiff()
        }
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
