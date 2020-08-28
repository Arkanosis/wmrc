package net.arkanosis.wrc

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.gson.*

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import com.saladevs.rxsse.RxSSE

import java.net.UnknownHostException

import kotlin.collections.ArrayList

import net.arkanosis.wrc.Arktest

import okhttp3.*
import okhttp3.Interceptor.*

// Get the version number from the build.gradle
const val VERSION_NAME = "0.1.0-dev"

var USER_AGENT = "wmrc/v$VERSION_NAME"

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
	val offset: Long,
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

private val pageTemplate = Any::class.java.getResource("/page.html").readText()

private val cookies = ArrayList<String>()

private fun getToken(serverUrl: String, serverScriptPath: String, type: String): String? {
	val (_, http, result) = Fuel.post("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "query",
			"type" to type,
			"meta" to "tokens",
			"format" to "json"
		))
		.header(
			"User-Agent" to USER_AGENT,
			"Cookie" to cookies.joinToString(separator=";")
		)
		.responseObject<TokenResponse>()
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
				if (response.query == null) {
					println("KO query")
				} else {
					if (response.query.tokens?.logintoken != null) {
						println("OK login token")
						println(response.query.tokens.logintoken)
						return response.query.tokens.logintoken
					} else if (response.query.tokens?.csrftoken != null) {
						println("OK csrf token")
						println(response.query.tokens.csrftoken)
						return response.query.tokens.csrftoken
					} else if (response.query.tokens?.patroltoken != null) {
						println("OK patrol token")
						println(response.query.tokens.patroltoken)
						return response.query.tokens.patroltoken
					}  else {
						println("KO token")
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
	return null
}

private fun login(serverUrl: String, serverScriptPath: String, userName: String, password: String): Boolean {
	val token = getToken(serverUrl, serverScriptPath, "login")
	if (token == null) {
		return false
	}

	val (_, http, result) = Fuel.post("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "login",
			"lgname" to userName,
			"lgpassword" to password,
			"lgtoken" to token,
			"format" to "json"
		))
		.header(
			"User-Agent" to USER_AGENT,
			"Cookie" to cookies.joinToString(separator=";")
		)
		.responseObject<LoginResponse>()
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
				if (response.login == null) {
					println("KO login")
					println(response)
				} else {
					if (response.login.result == "Success") {
						println("OK login")
						println(response.login.lgusername)
						return true
					} else if (response.login.result == "NeedToken") {
						println("KO token needed again")
					} else {
						println("KO success")
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
	println("END")
	return false
}

private fun editPage(serverUrl: String, serverScriptPath: String, title: String, comment: String, text: String, minor: Boolean = false): Boolean {
	val token = getToken(serverUrl, serverScriptPath, "csrf")
	if (token == null) {
		return false
	}

	val (_, http, result) = Fuel.post("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "edit",
			"tags" to "wmrc",
			"assert" to "user",
			"title" to title,
			"summary" to comment,
			"text" to text,
			"minor" to minor,
			"token" to token,
			"format" to "json"
		))
		.header(
			"User-Agent" to USER_AGENT,
			"Cookie" to cookies.joinToString(separator=";")
		)
		.responseObject<EditResponse>()
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
				if (response.edit == null || response.edit.result != "Success") {
					println("KO edit")
				} else {
					println("OK")
					println(response.edit.newrevid)
					return true
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
	println("END")
	return false
}

private fun undo(serverUrl: String, serverScriptPath: String, title: String, revision: Int, comment: String): Boolean {
	val token = getToken(serverUrl, serverScriptPath, "csrf")
	if (token == null) {
		return false
	}

	val (_, http, result) = Fuel.post("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "edit",
			"tags" to "wmrc",
			"assert" to "user",
			"title" to title,
			"summary" to comment,
			"undo" to revision,
			"token" to token,
			"format" to "json"
		))
		.header(
			"User-Agent" to USER_AGENT,
			"Cookie" to cookies.joinToString(separator=";")
		)
		.responseObject<UndoResponse>()
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
				if (response.edit == null || response.edit.result != "Success") {
					println("KO edit")
				} else {
					println("OK")
					println(response.edit.newrevid)
					return true
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
	println("END")
	return false
}

private fun patrol(serverUrl: String, serverScriptPath: String, revision: Int): Boolean {
	val token = getToken(serverUrl, serverScriptPath, "patrol")
	if (token == null) {
		return false
	}

	val (_, http, result) = Fuel.post("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "patrol",
			"tags" to "wmrc",
			"assert" to "user",
			"revid" to revision,
			"token" to token,
			"format" to "json"
		))
		.header(
			"User-Agent" to USER_AGENT,
			"Cookie" to cookies.joinToString(separator=";")
		)
		.responseObject<PatrolResponse>()
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
				if (response.patrol == null) {
					println("KO patrol")
				} else {
					println("OK")
					println(response.patrol.rcid)
					return true
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
	println("END")
	return false
}

private fun showDiff(serverUrl: String, serverScriptPath: String, revision: Int) {
	val (_, http, result) = Fuel.get("${serverUrl}${serverScriptPath}/api.php",
		listOf(
			"action" to "compare",
			"fromrev" to revision,
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
					println(pageTemplate.replace("\${content}", response.compare.diff))
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
	println("END")
}

private fun monitorRecentChanges(showDiffs: Boolean = false, autoRevert: Boolean = false) {
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
						    (recentChange.wiki == "frwiki" || recentChange.wiki == "frwiktionary") &&
						    recentChange.type == "edit") {
							println("User '${recentChange.user}' edited '${recentChange.title}' on ${recentChange.wiki}${if (recentChange.minor) " (minor edit)" else ""}")
							if (recentChange.revision != null) {
								if (showDiffs) {
									showDiff(recentChange.server_url, recentChange.server_script_path, recentChange.revision.new)
								}
								if (autoRevert && recentChange.comment.contains("wmrc:autorevert")) {
									undo(recentChange.server_url, recentChange.server_script_path, recentChange.title, recentChange.revision.new, "Autorevert")
								}
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

fun main(args : Array<String>) {
	val serverUrl = "https://fr.wikipedia.org"
	//val serverUrl = "http://localhost:8080"
	// TODO FIXME handle login at the SUL level
	login(serverUrl, "/w", Arktest.LOGIN, Arktest.PASSWORD)
	// editPage(serverUrl, "/w", "Utilisateur:Arktest/test", "Test", "Test.")
	// undo(serverUrl, "/w", "Utilisateur:Arktest/test", 152215779, "Revert")
	patrol("https://fr.wikipedia.org", "/w", 152223800)
}
