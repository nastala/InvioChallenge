package com.example.inviochallenge

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item.view.*

class MoviesAdapter(private val movies : ArrayList<Movie>) : RecyclerView.Adapter<MoviesAdapter.MovieHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflatedView = parent.inflate(R.layout.movie_item, false)
        return MovieHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = movies[position]
        holder.bindPhoto(movie)
    }

    fun addNewMovie(movie : Movie) {
        movies.add(movie)
        this.notifyItemInserted(movies.size - 1)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    private fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    class MovieHolder(v : View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var movie: Movie? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.d("RecyclerView", "CLICK!")
            if (movie != null) {
                Log.d("RecyclerView", movie!!.title)
                val stateImdbId = view.context.getString(R.string.state_imdb_id)
                val imdbId = movie!!.imdbId
                val context = view.context
                val showMovieDetailIntent = Intent(context, FilmDetailActivity::class.java)
                showMovieDetailIntent.putExtra(stateImdbId, imdbId)
                context.startActivity(showMovieDetailIntent)
            }
        }

        fun bindPhoto(movie : Movie) {
            this.movie = movie
            Picasso.get().load(movie.imageURL).into(view.ivMovie)
            view.tvMovieTitle.text = movie.title
            view.tvYear.text = movie.year.toString()
        }
    }
}