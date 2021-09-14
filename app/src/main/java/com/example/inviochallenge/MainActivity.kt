package com.example.inviochallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val tag = "main_activity"
    private lateinit var stateTitle : String

    private lateinit var urlBase : String
    private lateinit var adapter : MoviesAdapter
    private lateinit var movies : ArrayList<Movie>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var title : String = ""
    private var totalPage = 0
    private var currentPage = 0
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        rvMovies.layoutManager = linearLayoutManager
        urlBase = getString(R.string.api_omdb_url)
        movies = arrayListOf()
        stateTitle = getString(R.string.state_title)
        fillAdapter()

        addAPIKeyToURL()

        btnSearch.setOnClickListener {
            val title = etTitle.text
            if (title.isNotBlank()) {
                clearMovies()
                makeRequest(title.toString())
            } else {
                highlightErrorEtTitle()
            }
        }

        rvMovies.addOnScrollListener (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == movies.size - 1 && currentPage < totalPage) {
                    if (!isLoading) {
                        makeRequest(title)
                    }
                }
            }
        })
    }

    private fun highlightErrorEtTitle() {
        val errMessage = getString(R.string.err_et_search_empty)
        etTitle.error = errMessage
    }

    private fun makeRequest(title : String) {
        Log.d("main_activty", "makeRequest called title = $title")
        this.title = title
        onLoadingScreen()

        var url = addTitleToURL(title)
        url = addPageNumberToUrl(url)
        Log.d("main_activty", "currentPage: $currentPage, totalPage: $totalPage")
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)

                if (totalPage == 0) {
                    totalPage = (jsonObject.getInt("totalResults") / 10) + 1
                }

                val jsonArray = jsonObject.getJSONArray("Search")
                for (i in 1..jsonArray.length()) {
                    val json = JSONObject(jsonArray[i-1].toString())
                    val movie = Movie()
                    movie.title = json.getString("Title")
                    movie.year = json.getInt("Year")
                    movie.imdbId = json.getString("imdbID")
                    movie.imageURL = json.getString("Poster")

                    //movies.add(movie)
                    adapter.addNewMovie(movie)
                }

                currentPage++
                //adapter = MoviesAdapter(movies)
                //fillAdapter()
                activateScreen()
            } catch (err : Exception) {
                highlightNoMovieResult()
                activateScreen()
                clearMovies()
            }
        }, { error ->
            Log.e(tag, error.message!!)
        })

        queue.add(stringRequest)
    }

    private fun addPageNumberToUrl(url: String): String {
        return "$url&page=${currentPage + 1}"
    }

    private fun clearMovies() {
        currentPage = 0
        totalPage = 0
        movies.clear()
        fillAdapter()
    }

    private fun fillAdapter() {
        adapter = MoviesAdapter(movies)
        rvMovies.adapter = adapter
    }

    private fun highlightNoMovieResult() {
        val errMessage = getString(R.string.err_response_empty)
        Toast.makeText(this, errMessage, Toast.LENGTH_LONG).show()
    }

    private fun onLoadingScreen() {
        pbSearch.visibility = View.VISIBLE
        btnSearch.isClickable = false
        isLoading = true
    }

    private fun activateScreen() {
        pbSearch.visibility = View.GONE
        btnSearch.isClickable = true
        isLoading = false
    }

    private fun addTitleToURL(title: String) : String {
        return "$urlBase&s=$title"
    }

    private fun addAPIKeyToURL() {
        val key = getString(R.string.api_key_omdb)
        urlBase = "$urlBase?apikey=$key&type=movie"
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val prevTitle = savedInstanceState.getString(stateTitle)
        if (!prevTitle.isNullOrBlank()) {
            title = prevTitle
            makeRequest(title)
            Log.d(tag, "onRestoreInstanceState title = $title")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val title = etTitle.text.toString()
        if (title.isNotBlank()) {
            outState.putString(stateTitle, title)
            Log.d(tag, "onSaveInstanceState title = $title")
        }
    }
}