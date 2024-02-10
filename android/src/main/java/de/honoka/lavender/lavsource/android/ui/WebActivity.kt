package de.honoka.lavender.lavsource.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.lavsource.android.util.JsInterfaceContainerFactory
import de.honoka.lavender.lavsource.android.util.LavsourceServer
import de.honoka.lavender.lavsource.bilibili.R
import de.honoka.sdk.util.android.common.launchCoroutineOnUiThread
import de.honoka.sdk.util.android.jsinterface.JavascriptInterfaceContainer
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.HttpServerVariables
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

@SuppressLint("SetJavaScriptEnabled")
class WebActivity : AppCompatActivity() {

    private lateinit var url: String

    /**
     * 是否是该应用当中第一个被开启的WebActivity
     */
    private var firstWebActivity: Boolean = false

    lateinit var webView: WebView

    private val webViewClient = object : WebViewClient() {

        //重写此方法，解决WebView在重定向时打开系统浏览器的问题
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            val url = request.url.toString()
            //禁止WebView加载未知协议的URL
            if(!url.startsWith("http")) return true
            view.loadUrl(url)
            return true
        }
    }

    private val webChromeClient = WebChromeClient()

    private lateinit var jsInterfaceContainer: JavascriptInterfaceContainer

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {

        private var lastTimePressBack = 0L

        override fun handleOnBackPressed() = launchCoroutineOnUiThread {
            try {
                val result = dispatchEventToListenersInWebView("onBackButtonPressedListeners")
                if(!result) doBack()
            } catch(t: Throwable) {
                doBack()
            }
        }

        private fun doBack() {
            if(webView.canGoBack()) {
                webView.goBack()
                return
            }
            if(!firstWebActivity) {
                finish()
                return
            }
            if(System.currentTimeMillis() - lastTimePressBack > 2500) {
                Toast.makeText(this@WebActivity, "再进行一次返回退出应用", Toast.LENGTH_SHORT).show()
                lastTimePressBack = System.currentTimeMillis()
            } else {
                finish()
                if(firstWebActivity) exitProcess(0)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //解决状态栏白底白字的问题
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_web)
        initActivityParams()
        initWebView()
    }

    override fun onPause() {
        dispatchEventToListenersInWebViewDirectly("onActivityPauseListeners")
        super.onPause()
    }

    override fun onResume() {
        HttpServer.checkOrRestartInstance()
        LavsourceServer.checkOrRestartInstance()
        dispatchEventToListenersInWebViewDirectly("onActivityResumeListeners")
        super.onResume()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    private fun initActivityParams() {
        url = intent.getStringExtra("url") ?: HttpServerVariables.getUrlByPrefix("")
        firstWebActivity = intent.getBooleanExtra("firstWebActivity", false)
    }

    private fun initWebView() {
        webView = findViewById(R.id.web_view)
        webView.apply {
            webViewClient = this@WebActivity.webViewClient
            webChromeClient = this@WebActivity.webChromeClient
            settings.run {
                //必须打开，否则网页可能显示为空白
                javaScriptEnabled = true
            }
            isVerticalScrollBarEnabled = false
            scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
            jsInterfaceContainer = JsInterfaceContainerFactory(this@WebActivity).getContainer()
            loadUrl(this@WebActivity.url)
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    fun evaluateJavascript(script: String, callback: (String) -> Unit = {}) = runOnUiThread {
        webView.evaluateJavascript(script, callback)
    }

    //返回true表示有监听器的预定义行为被触发
    private suspend fun dispatchEventToListenersInWebView(listenerName: String): Boolean {
        val script = "window.androidEventListeners.executeListeners('$listenerName')"
        var result: String? = null
        webView.evaluateJavascript(script) {
            result = it
        }
        while(true) {
            if(result != null) break
            delay(1)
        }
        return result.toBoolean()
    }

    private fun dispatchEventToListenersInWebViewDirectly(listenerName: String) {
        launchCoroutineOnUiThread { dispatchEventToListenersInWebView(listenerName) }
    }
}