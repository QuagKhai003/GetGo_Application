package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.data.network.WikidataApi
import com.quangkhai.getgo_application.data.network.WikipediaApi
import com.quangkhai.getgo_application.data.network.client.WikidataClient
import com.quangkhai.getgo_application.data.network.client.WikipediaClient
import com.quangkhai.getgo_application.domain.model.Fact
import com.quangkhai.getgo_application.domain.repository.FactRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLDecoder
import kotlin.coroutines.cancellation.CancellationException

class WikidataFactRepositoryImpl(
    private val wikidataApi: WikidataApi = WikidataClient.wikidataApi,
    private val wikipediaApi: WikipediaApi = WikipediaClient.wikipediaApi,
) : FactRepository {

    override suspend fun getRandomFact(lat: Double, long: Double): Result<Fact?> {
        return try {
            val response = wikidataApi.query(buildQuery(lat, long))
            val bindings = response["results"]?.jsonObject?.get("bindings")?.jsonArray
                ?: return Result.success(null)

            val candidates = bindings.mapNotNull { it.jsonObject.toCandidateOrNull() }
            val chosen = candidates.randomOrNull() ?: return Result.success(null)

            // one Wikipedia summary call gives both the text and a fast CDN thumbnail
            val summary = fetchWikiSummary(chosen.articleTitle)
            val extract = summary?.get("extract")?.jsonPrimitive?.contentOrNull
            val thumb = summary?.get("thumbnail")?.jsonObject?.get("source")?.jsonPrimitive?.contentOrNull
            Result.success(
                Fact(
                    title = chosen.title,
                    description = extract ?: chosen.title,
                    imageUrl = thumb ?: chosen.imageUrl,
                    lat = chosen.lat,
                    long = chosen.long,
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // the Wikipedia summary: holds the intro paragraph and a ready-sized thumbnail
    private suspend fun fetchWikiSummary(articleTitle: String): JsonObject? =
        try {
            wikipediaApi.summary(articleTitle)
        } catch (e: Exception) {
            null
        }

    // notable places within 15 km that have a photo AND an English Wikipedia article
    private fun buildQuery(lat: Double, long: Double): String = """
        SELECT ?itemLabel ?coord ?image ?article WHERE {
          SERVICE wikibase:around {
            ?item wdt:P625 ?coord .
            bd:serviceParam wikibase:center "Point($long $lat)"^^geo:wktLiteral .
            bd:serviceParam wikibase:radius "15" .
          }
          ?item wdt:P18 ?image .
          ?article schema:about ?item ;
                   schema:isPartOf <https://en.wikipedia.org/> .
          SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
        } LIMIT 200
    """.trimIndent()

    private data class Candidate(
        val title: String,
        val imageUrl: String,
        val articleTitle: String,
        val lat: Double,
        val long: Double,
    )

    private fun JsonObject.toCandidateOrNull(): Candidate? {
        val point = value("coord")?.let { parsePoint(it) } ?: return null
        val title = value("itemLabel") ?: return null
        val article = value("article") ?: return null
        // ".../wiki/Cho%CC%A3_Lo%CC%9Bn" -> "Chợ_Lớn"
        val articleTitle = URLDecoder.decode(article.substringAfterLast("/wiki/"), "UTF-8")
        // ask Commons for a smaller thumbnail so it loads fast (full-res P18 is huge)
        val imageUrl = value("image")?.let { "$it?width=800" } ?: ""
        return Candidate(title, imageUrl, articleTitle, point.first, point.second)
    }

    private fun JsonObject.value(key: String): String? =
        this[key]?.jsonObject?.get("value")?.jsonPrimitive?.contentOrNull

    // "Point(106.7 10.77)" -> (lat 10.77, long 106.7)
    private fun parsePoint(wkt: String): Pair<Double, Double>? {
        val inside = wkt.substringAfter("Point(", "").substringBefore(")")
        val parts = inside.split(" ")
        if (parts.size != 2) return null
        val long = parts[0].toDoubleOrNull() ?: return null
        val lat = parts[1].toDoubleOrNull() ?: return null
        return lat to long
    }
}
