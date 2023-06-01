package dev.spravedlivo.flibuster.network

import dev.spravedlivo.flibuster.data.BookInfo
import dev.spravedlivo.flibuster.data.UrlName
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.lang.Integer.min
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
 kotlin port of
 https://github.com/utopicnarwhal/flibusta-mobile/blob/b0560124fe00313961dc72730341822cadc412cd/lib/utils/html_parsers.dart#L327
 see author's license
*/
suspend fun bookInfo(url: String, onError: (String) -> Unit) = suspendCoroutine { continuation ->
    FlibustaHelper.request("/b/$url", { response ->
        val bookScreen = BookInfo()
        val responseString = response.body!!.string()
        val document = Jsoup.parse(responseString)
        // let the trolling begin
        val authorsList = mutableListOf<UrlName>()
        val translatorsList = mutableListOf<UrlName>()
        val genresList = mutableListOf<UrlName>()
        val downloadFormats = mutableListOf<Pair<String, String>>()

        document.getElementById("content-top")!!.remove()
        val mainElement = document.getElementById("main")!!
        val allA = mainElement.getElementsByTag("a")


        val titleElement = mainElement.getElementsByClass("title").first()
        bookScreen.title = if (titleElement != null) titleElement.text() else bookScreen.title
        val mainNode = titleElement?.parent()
        var hasTranslators = false
        if (mainNode != null) {
            for (node in mainNode.childNodes().slice(1 until mainNode.children().size)) {
                if (node.hasAttr("href") && node.attr("href").contains(Regex("^(/g/)[0-9]*$"))) continue
                if (node is Element && node.hasAttr("href") && node.attr("href").contains(Regex("^(/a/)[0-9]*"))) {
                    val idc =
                        node.attr("href").replace("/a/", "").split('?')[0].toIntOrNull() ?: continue
                    val id = idc.toString()
                    val prepared = UrlName("/a/$id", node.text())
                    when (hasTranslators) {
                        true -> translatorsList.add(prepared)
                        else -> authorsList.add(prepared)
                    }
                }
                else if (node is TextNode && node.text().contains("(перевод:")) {
                    hasTranslators = true
                }
            }
        }
        bookScreen.authors = authorsList
        bookScreen.translators = translatorsList

        val genresA = allA.filter {a ->
            a.hasAttr("href") && a.attr("href").contains(
                Regex("^(/g/)[0-9]+"))
        }
        genresA.forEach {
            val genre = it.attr("href").replace("/g/", "").split("?")[0].toIntOrNull() ?: return@forEach
            val genreId = genre.toString()
            genresList.add(UrlName("/g/$genreId", it.text()))
        }
        bookScreen.genres = genresList

        val userOptSelector = document.getElementById("useropt")
        if (userOptSelector != null) {
            val downloadFormatOptions = userOptSelector.children().filter {
                    element -> element.tag().normalName().split(":").getOrNull(1) == "option" }
            downloadFormatOptions.forEach {
                downloadFormats.add(Pair(it.text().trim(), it.attr("href")))
            }
        }
        else {
            allA.filter { a ->
                a.hasAttr("href") && a.attr("href").contains(Regex("^(/b/$url/)(?!read).*"))
            }
                .forEach { downloadFormatA ->
                    val downloadFormatName =
                        downloadFormatA.text().replace(Regex("(\u0028|\u0029)"), "")
                            .replace("скачать ", "").trim();
                    if (downloadFormatName == "mail" ||
                        downloadFormatName == "исправить" ||
                        downloadFormatName.contains("пожаловаться")
                    ) return@forEach
                    val downloadFormatType =
                        downloadFormatA.attr("href").split('/').last().split('?')[0]
                    downloadFormats.add(Pair(downloadFormatName, downloadFormatType))
                }
        }
        bookScreen.downloadFormats = downloadFormats
        // lazy to implement sequence, size, date parsing
        if (mainNode != null) {
            val mainNodeText = mainNode.text()
            val lemmaStringStarts = mainNodeText.indexOf("Аннотация") + 10;
            val lemmaStringEndsOnComplain = mainNodeText.indexOf("(пожаловаться")
            val lemmaStringEndsOnRecommendations = mainNodeText.indexOf("Рекомендации:")
            var lemmaStringEnds = mainNodeText.length
            if (lemmaStringEndsOnComplain == -1 && lemmaStringEndsOnRecommendations != -1) {
                lemmaStringEnds = lemmaStringEndsOnRecommendations
            } else if (lemmaStringEndsOnComplain != -1 && lemmaStringEndsOnRecommendations == -1) {
                lemmaStringEnds = lemmaStringEndsOnComplain
            } else if (lemmaStringEndsOnComplain != -1 /*&& lemmaStringEndsOnRecommendations != -1*/) {
                lemmaStringEnds = min(lemmaStringEndsOnComplain, lemmaStringEndsOnRecommendations)
            }
            bookScreen.lemma = mainNodeText.substring(lemmaStringStarts, lemmaStringEnds).trim()
        }
        bookScreen.imageUrl = document.getElementsByClass("fb2info-content").first()?.nextElementSibling()?.nextElementSibling()?.attr("src")

        continuation.resume(bookScreen)
    }, onError)
}