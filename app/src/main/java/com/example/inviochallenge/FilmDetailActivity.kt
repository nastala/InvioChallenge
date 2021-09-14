package com.example.inviochallenge

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_film_detail.*
import kotlinx.android.synthetic.main.movie_item.ivMovie
import org.json.JSONObject
import java.lang.Exception

class FilmDetailActivity : AppCompatActivity() {
    private val tag = "film_detail_activity"
    private var imdbId = ""
    private lateinit var urlBase : String
    private val movie = Movie()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_detail)

        val extras = intent.extras

        if (extras != null) {
            val stateImdbId = getString(R.string.state_imdb_id)
            val imdbIdFromIntent = extras.getString(stateImdbId)
            if (!imdbIdFromIntent.isNullOrEmpty()) {
                onLoadingScreen()
                imdbId = imdbIdFromIntent
                Log.d("film_detail_activity", "imdbId = $imdbId")
            }
        } else {
            finish()
        }

        urlBase = getString(R.string.api_omdb_url)

        addAPIKeyToURL()
        makeRequest()

        tvPlot.movementMethod = ScrollingMovementMethod()

        tvImdbSite.setOnClickListener {
            imdbId = movie.imdbId
            if (imdbId.isNotBlank()) {
                val openImdbIntent = Intent(Intent.ACTION_VIEW)
                val url = getString(R.string.url_imdb)
                openImdbIntent.data = Uri.parse("${url}title/$imdbId")
                startActivity(openImdbIntent)
            }
        }
    }

    private fun onLoadingScreen() {
        pbLoading.visibility = View.VISIBLE
        tvImdbSite.isClickable = false
    }

    private fun activateScreen() {
        pbLoading.visibility = View.GONE
        tvImdbSite.isClickable = true
    }

    private fun makeRequest() {
        val url = addImdbIdToURL()
        val queue = Volley.newRequestQueue(this)
        Log.d("film_detail_activity", "makeRequest called url: $url")

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)
                val check = jsonObject.getBoolean("Response")

                if (!check) {
                    finish()
                }
                Log.d("film_detail_activity", "makeRequest response true")
                movie.title = jsonObject.getString("Title")
                movie.plot = jsonObject.getString("Plot")
                movie.imageURL = jsonObject.getString("Poster")
                movie.genre = jsonObject.getString("Genre")
                movie.imdbRating = jsonObject.getString("imdbRating")
                movie.actors = jsonObject.getString("Actors")
                movie.director = jsonObject.getString("Director")
                movie.awards = jsonObject.getString("Awards")
                movie.imdbId = imdbId

                updateScreen()
                activateScreen()
            } catch (err : Exception) {
                clearMovies()
                finish()
            }
        }, { error ->
            Log.e(tag, error.message!!)
        })

        queue.add(stringRequest)
    }

    private fun clearMovies() {
        movie.clear()
        updateScreen()
    }

    private fun updateScreen() {
        Log.d("film_detail_activity", "updateScreen called imageURL: ${movie.imageURL}")
        Picasso.get().load(movie.imageURL).into(ivMovie)
        tvTitle.text = movie.title
        tvPlot.text = movie.plot
        tvGenre.text = movie.genre
        tvImdbRating.text = movie.imdbRating
        tvActors.text = movie.actors
        tvDirector.text = movie.director
        tvAwards.text = movie.awards
    }

    private fun addImdbIdToURL() : String {
        return "$urlBase&i=$imdbId"
    }

    private fun addAPIKeyToURL() {
        val key = getString(R.string.api_key_omdb)
        urlBase = "$urlBase?apikey=$key"
    }
}