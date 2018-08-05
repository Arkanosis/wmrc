package net.arkanosis.wmrc

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

/**
 * An about screen that offers information on the app.
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aboutPage = AboutPage(this)
                .isRTL(false)
                .addItem(Element().setTitle("wmrc v" + BuildConfig.VERSION_NAME))
                .setDescription("THIS IS A SUPER SECRET PROJECT AND I WON'T TELL YOU ANYTHING ABOUT IT and btw, why have you installed something on you device if you don't know what it does?!")
                .addEmail("jroquet@arkanosis.net")
                .addWebsite("https://wmrc.arkanosis.net/")
                .addGitHub("Arkanosis/wmrc")
                .create()
        setContentView(aboutPage)
    }

}
