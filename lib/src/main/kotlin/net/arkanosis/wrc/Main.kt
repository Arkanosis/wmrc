package net.arkanosis.wrc

import com.beust.klaxon.Klaxon

import com.saladevs.rxsse.RxSSE

import java.net.UnknownHostException

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

fun main(args : Array<String>) {
	try {
		RxSSE()
			.connectTo("https://stream.wikimedia.org/v2/stream/recentchange")
			.subscribe { event ->
				if (event.data != "") {
					val recentChange = Klaxon().parse<RecentChange>(event.data)
					if (recentChange != null &&
					    !recentChange.bot &&
					    recentChange.wiki == "frwiki" &&
					    recentChange.type == "edit") {
						println("User '${recentChange.user}' edited '${recentChange.title}'${if (recentChange.minor) " (minor edit)" else ""}")
					}
				}
			}
			.dispose()
	} catch (e: UnknownHostException) {
		System.err.println("Unable to reach the server; are you connected to the network?");
	}
}
