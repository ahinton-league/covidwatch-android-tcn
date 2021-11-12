package life.league.healthjourney.articles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import life.league.core.analytics.AnalyticsTracker
import life.league.core.base.RootFragment
import life.league.core.observable.Failed
import life.league.core.observable.Loaded
import life.league.healthjourney.analytics.viewHealthGetInspired
import life.league.healthjourney.databinding.FragmentArticlesBinding
import org.koin.android.ext.android.inject

class ArticlesFragment : RootFragment() {

    private lateinit var binding: FragmentArticlesBinding

    private val viewModel: ArticlesViewModel by inject()
    private val analyticsTracker: AnalyticsTracker by inject()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentArticlesBinding.inflate(inflater, container, false)

        binding.getInspiredWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int,
                                         description: String, failingUrl: String) {
                ShowEmptyState()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.getInspiredWebView.visibility = View.VISIBLE
                binding.loadingSpinner.visibility = View.GONE
            }
        }

        binding.getInspiredWebView.settings.javaScriptEnabled = true
        binding.getInspiredWebView.settings.domStorageEnabled = true

        viewModel.articlesUrlStringRes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Loaded -> {
                    binding.getInspiredWebView.loadUrl(getStringIfAdded(state.data))
                }
                is Failed -> {
                    ShowEmptyState()
                }
            }
        }

        viewModel.fetchArticlesUrl()

        return binding.root
    }

    private fun ShowEmptyState() {
        binding.getInspiredWebView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        analyticsTracker.viewHealthGetInspired()
    }

}
