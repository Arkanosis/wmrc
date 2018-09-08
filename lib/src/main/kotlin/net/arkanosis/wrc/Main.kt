package net.arkanosis.wrc

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.gson.*

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import com.saladevs.rxsse.RxSSE

import java.net.UnknownHostException

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

data class Response(
	val compare: Compare? = null
)

private val pageTemplate = Any::class.java.getResource("/page.html").readText();

private fun showDiff(serverUrl: String, serverScriptPath: String, revision: Int) {
	val (_, http, result) = "${serverUrl}${serverScriptPath}/api.php?action=compare&torelative=prev&fromrev=${revision}&format=json".httpGet().responseObject<Response>()
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

private fun showRecentChanges(showDiffs: Boolean = false) {
	try {
		val gson = Gson()
		RxSSE()
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
							if (showDiffs && recentChange.revision != null) {
								showDiff(recentChange.server_url, recentChange.server_script_path, recentChange.revision.new)
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
		System.err.println("Unable to reach the server; are you connected to the network?");
	}
}

fun main(args : Array<String>) {
	showRecentChanges(showDiffs=true);
}
