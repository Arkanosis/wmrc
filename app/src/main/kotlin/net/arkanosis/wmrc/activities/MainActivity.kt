package net.arkanosis.wmrc.activities

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.gson.*

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import com.saladevs.rxsse.RxSSE

import java.net.SocketTimeoutException
import java.net.UnknownHostException

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar

import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

import mu.KotlinLogging

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

import okhttp3.*
import okhttp3.Interceptor.*

var USER_AGENT = "wmrc/v" + BuildConfig.VERSION_NAME

const val RC_BUFFER_SIZE = 100

// TODO all fields should be nullable and checked
// because we can't reliably expect that the HTTP
// answer will contain anything

data class Length(
        val new: Int,
        val old: Int = 0
)

data class Meta(
        val domain: String,
        val dt: String,
        val id: String,
        val offset: Int,
        val partition: Int,
        val request_id: String,
        val schema_uri: String,
        val topic: String,
        val uri: String
)

data class Revision(
        val new: Int,
        val old: Int? = null
)

data class RecentChange(
        val bot: Boolean = false,
        val comment: String = "",
        val id: Int? = null,
        val length: Length? = null,
        val meta: Meta,
        val minor: Boolean = false,
        val namespace: Int,
        val parsedcomment: String,
        val revision: Revision? = null,
        val server_name: String,
        val server_script_path: String,
        val server_url: String,
        val timestamp: Int,
        val title: String,
        val type: String,
        val user: String,
        val wiki: String
)

data class Edit(
        val result: String,
        val pageid: Int,
        val title: String,
        val contentmodel: String,
        val oldrevid: Int?,
        val newrevid: Int,
        val newtimestamp: String
)

data class EditResponse(
        val edit: Edit? = null
)

data class UndoResponse(
        val edit: Edit? = null
)

data class Patrol(
        val rcid: Int,
        val ns: Int,
        val title: String
)

data class PatrolResponse(
        val patrol: Patrol? = null
)

data class Compare(
        val fromid: Int,
        val fromrevid: Int,
        val fromns: Int,
        val fromtitle: String,
        val toid: Int,
        val torevid: Int,
        val tons: Int,
        val totitle: String,
        @SerializedName("*")
        val diff: String
)

data class CompareResponse(
        val compare: Compare? = null
)

data class Token(
        val logintoken: String?,
        val csrftoken: String?,
        val patroltoken: String?
)

data class TokenQuery(
        val tokens: Token? = null
)

data class TokenResponse(
        val query: TokenQuery? = null
)

data class Login(
        val result: String,
        val lguserid: Int,
        val lgusername: String
)

data class LoginResponse(
        val login: Login? = null
)

class MainActivity : BaseActivity() {

    private val logger = KotlinLogging.logger {}

    private var spinner: ProgressBar? = null
    private var revertButton: AppCompatButton? = null
    private var ignoreButton: AppCompatButton? = null
    private var approveButton: AppCompatButton? = null

    private val recentChanges = Channel<String>(RC_BUFFER_SIZE)
    private val cookies = ArrayList<String>()

    private suspend fun showNextDiff() {
        withContext(Dispatchers.IO) {
            val diff = recentChanges.receive()
            web_view_id.post {
                web_view_id.loadDataWithBaseURL(null, diff, "text/html", "utf-8", null)
                spinner?.visibility = View.GONE
                revertButton?.isEnabled = true
                ignoreButton?.isEnabled = true
                approveButton?.isEnabled = true
            }
        }
    }

    private fun queueChange(change: RecentChange) {
	val (_, http, result) = Fuel.get("${change.server_url}${change.server_script_path}/api.php",
	    listOf(
		"action" to "compare",
		"fromrev" to change.revision?.new,
		"torelative" to "prev",
		"format" to "json"
	    ))
	    .header(
		"User-Agent" to USER_AGENT,
		"Cookie" to cookies.joinToString(separator=";")
	    )
	    .responseObject<CompareResponse>()
	http.headers.get("Set-Cookie")?.forEach { string ->
	    string.split("; *".toRegex()).forEach { cookie ->
		cookies.add(cookie)
	    }
	}
	if (http.statusCode == 200) {
	    val (response, error) = result
	    if (error == null) {
		if (response == null) {
		    logger.warn { "KO response" }
		} else {
		    if (response.compare == null) {
			logger.warn { "KO compare" }
		    } else {
			logger.debug { "OK" }
			CoroutineScope(Dispatchers.Main).launch {
			    val pageTemplate = getResources().openRawResource(R.raw.template).reader().use { it.readText() }
			    val diff = pageTemplate
				.replace("\${page}", change.title)
				.replace("\${author}", change.user)
				.replace("\${content}", response.compare.diff)
			    recentChanges.send(diff)
			}
		    }
		}
	    } else {
		    logger.warn { "KO error" }
		    logger.warn { error }
	    }
	} else {
		logger.warn { "KO http" }
		logger.warn { http }
	}
   }

    private suspend fun fillChannel() {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val client = OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        override fun intercept(chain: Chain): Response {
                            val request = chain.request().newBuilder().header("User-Agent", USER_AGENT).build()
                            return chain.proceed(request)
                        }
                    })
                    .build()
                RxSSE(client)
                    .connectTo("https://stream.wikimedia.org/v2/stream/recentchange")
                    .subscribe(
                        { event ->
                            if (event.data != "") {
                                val recentChange = gson.fromJson(event.data, RecentChange::class.java)
                                if (recentChange != null &&
                                    !recentChange.bot &&
                                    (recentChange.wiki == "frwiki"/* || recentChange.wiki == "frwiktionary"*/) &&
                                    recentChange.type == "edit" &&
                                    recentChange.revision != null) {
                                    logger.info { "User '${recentChange.user}' edited '${recentChange.title}' on ${recentChange.wiki}${if (recentChange.minor) " (minor edit)" else ""}" }
                                    queueChange(recentChange)
                                }
                            }
                        },
                        { error ->
                            logger.warn { "KO SSE" }
                            logger.warn { error }
                        }
                    )
                    .dispose()
            } catch (e: UnknownHostException) {
                logger.error { "Unable to reach the server; are you connected to the network?" }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        web_view_id.setInitialScale(1)
        web_view_id.getSettings().setUseWideViewPort(true)

        spinner = findViewById(R.id.spinner_id) as ProgressBar

        revertButton = findViewById(R.id.revert_id)
        revertButton?.setOnClickListener {
            logger.debug { "REVERT!!" }
            revertButton?.isEnabled = false
            ignoreButton?.isEnabled = false
            approveButton?.isEnabled = false
            spinner?.visibility = View.VISIBLE
            web_view_id.loadUrl("about:blank")
            CoroutineScope(Dispatchers.Main).launch {
                showNextDiff()
            }
        }

        ignoreButton = findViewById(R.id.ignore_id)
        ignoreButton?.setOnClickListener {
            logger.debug { "IGNORE!!" }
            revertButton?.isEnabled = false
            ignoreButton?.isEnabled = false
            approveButton?.isEnabled = false
            spinner?.visibility = View.VISIBLE
            web_view_id.loadUrl("about:blank")
            CoroutineScope(Dispatchers.Main).launch {
                showNextDiff()
            }
        }

        approveButton = findViewById(R.id.approve_id)
        approveButton?.setOnClickListener {
            logger.debug { "APPROVE!!" }
            revertButton?.isEnabled = false
            ignoreButton?.isEnabled = false
            approveButton?.isEnabled = false
            spinner?.visibility = View.VISIBLE
            web_view_id.loadUrl("about:blank")
            CoroutineScope(Dispatchers.Main).launch {
                showNextDiff()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            fillChannel()
        }
        CoroutineScope(Dispatchers.Main).launch {
            showNextDiff()
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
