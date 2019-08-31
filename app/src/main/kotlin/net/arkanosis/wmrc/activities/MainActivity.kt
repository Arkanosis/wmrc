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

import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

import net.arkanosis.wmrc.BuildConfig
import net.arkanosis.wmrc.R

import okhttp3.*
import okhttp3.Interceptor.*

// TODO Get the version number from the build.gradle
const val VERSION_NAME = "0.1.0-dev"

var USER_AGENT = "wmrc/v$VERSION_NAME"

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

    private var recentChanges = Channel<String>(RC_BUFFER_SIZE)
    private val cookies = ArrayList<String>()

    private suspend fun showNextDiff() {
        withContext(Dispatchers.IO) {
            val diff = recentChanges.receive()
            web_view_id.post {
                web_view_id.loadDataWithBaseURL(null, diff, "text/html", "utf-8", null)
            }
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
                                    println("User '${recentChange.user}' edited '${recentChange.title}' on ${recentChange.wiki}${if (recentChange.minor) " (minor edit)" else ""}")



                                    val (_, http, result) = Fuel.get("${recentChange.server_url}${recentChange.server_script_path}/api.php",
                                        listOf(
                                            "action" to "compare",
                                            "fromrev" to recentChange.revision.new,
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
                                                println("KO response")
                                            } else {
                                                if (response.compare == null) {
                                                    println("KO compare")
                                                } else {
                                                    println("OK")
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        val pageTemplate = getResources().openRawResource(R.raw.template).reader().use { it.readText() }
                                                        val diff = pageTemplate
                                                            .replace("\${page}", recentChange.title)
                                                            .replace("\${author}", recentChange.user)
                                                            .replace("\${content}", response.compare.diff)
                                                        recentChanges.send(diff)
                                                    }
                                                }
                                            }
                                        } else {
                                                println("KO error")
                                                println(error)
                                        }
                                    } else {
                                            println("KO http")
                                            println(http)
                                    }
                                }
                            }
                        },
                        { error ->
                            println("KO SSE")
                            println(error)
                        }
                    )
                    .dispose()
            } catch (e: UnknownHostException) {
                System.err.println("Unable to reach the server; are you connected to the network?")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        web_view_id.setInitialScale(1)
        web_view_id.getSettings().setUseWideViewPort(true)

        val revertButton = findViewById(R.id.revert_id) as AppCompatButton
        revertButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showNextDiff()
            }
        }

        val approveButton = findViewById(R.id.patrol_id) as AppCompatButton
        approveButton.setOnClickListener {
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
