package net.arkanosis.wmrc.activities

import android.os.Bundle

import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

import net.arkanosis.wmrc.BuildConfig

class AboutActivity : BaseActivity() {

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
