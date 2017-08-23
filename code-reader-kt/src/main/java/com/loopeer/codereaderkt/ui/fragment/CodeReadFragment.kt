package com.loopeer.codereaderkt.ui.fragment

import android.annotation.TargetApi
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.FragmentCodeReadBinding
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.ui.loader.CodeFragmentContentLoader
import com.loopeer.codereaderkt.ui.loader.ILoadHelper
import com.loopeer.codereaderkt.ui.view.NestedScrollWebView
import com.loopeer.codereaderkt.utils.*
import com.todou.markdownj.MarkdownProcessor
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.util.concurrent.TimeUnit


open class CodeReadFragment : BaseFullscreenFragment(), NestedScrollWebView.ScrollChangeListener {
    private val TAG = "CodeReadFragment"

    private lateinit var mBinding: FragmentCodeReadBinding
    private lateinit var mToolbar: Toolbar


    private var mNode: DirectoryNode? = null
    private var mRootNode: DirectoryNode? = null
    private var scrollFinishDelaySubscription: Subscription? = null
    private var mScrollDown = false
    private var mOpenFileAfterLoadFinish = false
    private var mCodeContentLoader: ILoadHelper? = null

    private var mOrientationChange: Boolean = false


    companion object {
        fun newInstance(node: DirectoryNode?, root: DirectoryNode?): CodeReadFragment {
            val codeReadFragment = CodeReadFragment()
            codeReadFragment.mNode = node
            codeReadFragment.mRootNode = root
            return codeReadFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_code_read, container, false)
        mToolbar=mBinding.root.findViewById(R.id.toolbar)!!
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCodeContentLoader = CodeFragmentContentLoader(view)

        setupToolbar()

        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(mToolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(context, R.drawable.ic_view_list_white))
        mBinding.webCodeRead.setScrollChangeListener(this)
        mBinding.webCodeRead.settings.javaScriptEnabled = true
        mBinding.webCodeRead.settings.setSupportZoom(true)
        mBinding.webCodeRead.settings.builtInZoomControls = true
        mBinding.webCodeRead.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                mCodeContentLoader!!.showContent()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Navigator().startWebActivity(context, url)
                return true
            }

            @RequiresApi(Build.VERSION_CODES.M)
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Navigator().startWebActivity(context, request.url.toString())
                return true
            }
        }

        mBinding.webCodeRead.webChromeClient = object : WebChromeClient() {

        }
        if (Build.VERSION.SDK_INT >= 11) {
            (Runnable { mBinding.webCodeRead.settings.displayZoomControls = false }).run()
        }
        openFile()
    }

    private fun setupToolbar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        val params = mToolbar.getLayoutParams() as AppBarLayout.LayoutParams
        params.height = (DeviceUtils.dpToPx(activity, 56f) + DeviceUtils.statusBarHeight).toInt()
        mToolbar.setLayoutParams(params)
        mToolbar.setPadding(0, DeviceUtils.statusBarHeight, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openFile() {
        mCodeContentLoader!!.showProgress()
        if (mBinding.webCodeRead == null) {
            return
        }
        mBinding.webCodeRead.clearHistory()
        if (mNode == null) {
            if (mOpenFileAfterLoadFinish)
                mCodeContentLoader!!.showEmpty(getString(R.string.code_read_no_file_open))
        } else if (FileTypeUtils.isImageFileType(mNode!!.absolutePath)) {
            openImageFile()
        } else if (FileTypeUtils.isMdFileType(mNode!!.absolutePath)) {
            openMdShowFile()
        } else {
            openCodeFile()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openImageFile() {
        val string = "<html>" +
                "<body style=\"background-color:${ColorUtils.getColorString(context, R.color.code_read_background_color)}" + ";margin-top: 40px; margin-bottom: 40px; text-align: center; vertical-align: center;\">" + "<img src='file:///" + mNode!!.absolutePath + "'>" + "</body></html>"
        mBinding.webCodeRead.loadDataWithBaseURL(null, string, "text/html", "utf-8", null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun openFile(node: DirectoryNode) {
        mOpenFileAfterLoadFinish = true
        mNode = node
        if (!isVisible) return
        openFile()
    }

    private fun openCodeFile() {
        Observable.create(Observable.OnSubscribe<String> { subscriber ->
            var stream: InputStream? = null
            try {
                stream = FileInputStream(mNode!!.absolutePath)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            if (stream == null) {
                subscriber.onCompleted()
                return@OnSubscribe
            }
            val finalStream = stream
            val names = mNode!!.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var jsFile = BrushMap.getJsFileForExtension(names[names.size - 1])
            if (jsFile == null) {
                jsFile = "Txt"
            }
            val sb = StringBuilder()
            val localStringBuilder = StringBuilder()
            try {
                val localBufferedReader = BufferedReader(
                        InputStreamReader(finalStream, "UTF-8"))
                while (true) {
                    val str = localBufferedReader.readLine() ?: break
                    localStringBuilder.append(str)
                    localStringBuilder.append("\n")
                }

                localBufferedReader.close()
                sb.append("<pre class='brush: ")
                sb.append(jsFile!!.toLowerCase())
                sb.append(";'>")
                sb.append(TextUtils.htmlEncode(localStringBuilder.toString()))
                sb.append("</pre>")
                subscriber.onNext(HtmlParser.buildHtmlContent(activity, sb.toString(), jsFile, mNode!!.name))
            } catch (e: OutOfMemoryError) {
                subscriber.onError(e)
            } catch (e: FileNotFoundException) {
                subscriber.onError(e)
            } catch (e: IOException) {
                subscriber.onError(e)
            }

            subscriber.onCompleted()
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { o -> mBinding.webCodeRead.loadDataWithBaseURL("file:///android_asset/", o, "text/html", "UTF-8", "") }
                .doOnError { e -> mCodeContentLoader!!.showEmpty(e.message!!) }
                .onErrorResumeNext(Observable.empty())
                .subscribe()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected fun openMdShowFile() {
        registerSubscription(
                Observable.create(Observable.OnSubscribe<String> { subscriber ->
                    var stream: InputStream? = null
                    try {
                        stream = FileInputStream(mNode!!.absolutePath)
                    } catch (e: FileNotFoundException) {
                        subscriber.onError(e)
                    }

                    if (stream == null)
                        return@OnSubscribe
                    val finalStream = stream
                    val localStringBuilder = StringBuilder()
                    try {
                        val localBufferedReader = BufferedReader(
                                InputStreamReader(finalStream, "UTF-8"))
                        while (true) {
                            val str = localBufferedReader.readLine() ?: break
                            localStringBuilder.append(str)
                            localStringBuilder.append("\n")
                        }
                        val textString = localStringBuilder.toString()

                        if (textString != null) {
                            val m = MarkdownProcessor(mRootNode!!.absolutePath)
                            m.setTextColorString(ColorUtils.getColorString(context, R.color.text_color_primary))
                            m.setBackgroundColorString(ColorUtils.getColorString(context, R.color.code_read_background_color))
                            m.setCodeBlockColor(ColorUtils.getColorString(context, R.color.code_block_color))
                            m.setTableBorderColor(ColorUtils.getColorString(context, R.color.table_block_border_color))
                            val html = m.markdown(textString)
                            subscriber.onNext(html)
                        }
                        subscriber.onCompleted()
                    } catch (e: OutOfMemoryError) {
                        subscriber.onError(e)
                    } catch (e: FileNotFoundException) {
                        subscriber.onError(e)
                    } catch (e: IOException) {
                        subscriber.onError(e)
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { s -> mBinding.webCodeRead.loadDataWithBaseURL("fake://", s, "text/html", "UTF-8", "") }
                        .doOnError { e -> mCodeContentLoader!!.showEmpty(e.message!!) }
                        .onErrorResumeNext(Observable.empty())
                        .subscribe()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mBinding.webCodeRead.destroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mOrientationChange = true
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        if (mOrientationChange) {
            mOrientationChange = false
            return
        }

        if (scrollFinishDelaySubscription != null && !scrollFinishDelaySubscription!!.isUnsubscribed()) {
            scrollFinishDelaySubscription!!.unsubscribe()
        }
        if (t - oldt > 70) {
            if (mScrollDown)
                return

            mScrollDown = true
        } else if (t - oldt < 0) {
            if (!mScrollDown)
                return

            mScrollDown = false
            closeFullScreen()
        }
        if (mScrollDown) {
            scrollFinishDelaySubscription = Observable
                    .timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { lo -> openFullScreen() }
                    .subscribe()
            registerSubscription(subscription = scrollFinishDelaySubscription!!)
        }
    }

    fun updateRootNode(directoryNode: DirectoryNode) {
        mRootNode = directoryNode
    }

    fun getCodeContentLoader(): ILoadHelper? {
        return this!!.mCodeContentLoader
    }

    private fun openFullScreen() {
        hide()
    }

    private fun closeFullScreen() {
        show()
    }
}