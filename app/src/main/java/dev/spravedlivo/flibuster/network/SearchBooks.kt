package dev.spravedlivo.flibuster.network

import android.content.Context
import dev.spravedlivo.flibuster.data.BookInfoSearch
import dev.spravedlivo.flibuster.data.UrlName
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.LinkedList
import kotlin.properties.Delegates


suspend fun searchBooks(context: Context, query: String, onSuccess: (List<BookInfoSearch>) -> Unit, onError: (String) -> Unit) {
    val queue = LinkedList<Int>().run { this.add(0); this }
    val items = mutableListOf<BookInfoSearch>()
    var page by Delegates.notNull<Int>()
    while (!queue.isEmpty()) {
        page = queue.pop()
        val resp = FlibustaHelper.request(context, "/booksearch?ask=$query&page=$page")
        if (resp.error != null) {
            onError(resp.error)
            return
        }
        val response = resp.responseBodyString!!
        val document = Jsoup.parse(response)
        val children = document.select("#main").first()!!.children()
        val booksList = children.run {
            forEachIndexed { index, element ->
                if (element.tag().normalName() == "ul" &&
                    index > 0 && index < children.size - 1
                    && children[index-1].text().startsWith("Найденные книги")) {
                    return@run element
                }
            }
        }
        if (booksList is Element) {
            booksList.select("li").forEach { element ->
                val links = element.select("a")
                val title = links.first()!!
                val urlNames = links.slice(1 until links.size).map {
                    UrlName(it.attr("href"), it.text()) }
                items.add(BookInfoSearch(title.attr("href"), title.text(), urlNames))
            }
        }
        if (page == 0) {
            val pager = document.selectFirst("ul.pager")
            if (pager != null) {
                queue.addAll((page+1 until pager.childrenSize() - 2).toList())
            }
        }
    }
    onSuccess(items)
}