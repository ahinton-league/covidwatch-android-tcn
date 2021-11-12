package life.league.healthjourney.articles

import android.os.Bundle
import android.view.MenuItem
import life.league.core.base.RootActivity
import life.league.healthjourney.databinding.ActivityArticlesBinding

class ArticlesActivity : RootActivity() {
    companion object {
        const val EXTRA_TITLE = "extra_title"
    }

    override fun setupViews(savedInstanceState: Bundle?) {
        val binding = ActivityArticlesBinding.inflate(layoutInflater).apply { setContentView(root) }

        supportFragmentManager.beginTransaction()
                .replace(binding.content.id, ArticlesFragment())
                .commit()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
