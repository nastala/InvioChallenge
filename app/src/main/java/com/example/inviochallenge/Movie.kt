package com.example.inviochallenge

class Movie {
    var title : String = ""
    var year : Int = 1900
    var realeased : String = ""
    var duration : String = ""
    var imageURL : String = ""
    var genre : String = ""
    var director : String = ""
    var writer : String = ""
    var imdbRating : String = "0.0"
    var plot : String = ""
    var imdbId : String = ""
    var actors : String = ""
    var awards : String = ""

    fun clear() {
        title = ""
        year = 1900
        realeased = ""
        duration = ""
        imageURL = ""
        genre = ""
        director = ""
        writer = ""
        imdbRating = "0.0"
        plot = ""
        imdbId = ""
        actors = ""
        awards = ""
    }
}