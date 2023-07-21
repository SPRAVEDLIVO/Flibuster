package dev.spravedlivo.flibuster.data

import android.graphics.Bitmap

data class UrlName(val url: String, val name: String)
data class BookInfoSearch(val url: String, val title: String, val authors: List<UrlName>)
data class BookSearchScreenState(val displayError: Boolean = false, val errorText: String = "", val list: List<BookInfoSearch> = listOf())
data class BookInfo(var title: String = "",
                    var imageUrl: String? = null,
                    var url: String = "",
                    var image: Bitmap? = null,
                    var authors: List<UrlName> = mutableListOf(),
                    var translators: List<UrlName> = mutableListOf(),
                    var genres: List<UrlName> = mutableListOf(),
                    var lemma: String = "",
                    var downloadFormats: List<Pair<String, String>> = mutableListOf()
)
