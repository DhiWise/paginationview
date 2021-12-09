package com.dhiwise.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.dhiwise.paginationview.R

/**
 * the custom view for easy pagination setup in application
 * the pagination view contains the following.
 *
 *
 * 1. Recycler View - for loading the list
 * 2. No Data Layout - is used when no data found in list
 * 3. Refresh Layout - which enable refresh functionality
 * 4. After binding the view with PaginationBinder it enables the functionality of load more.
 */
class PaginationView : FrameLayout {

    /**
     * to get or set adapter using inner recycler view
     */
    var adapter: RecyclerView.Adapter<*>? = null
        set(value) {
            getRecyclerView().adapter = value
            field = value
        }

    /**
     * the resource layout ID of no data layout view
     */
    private var resNoDataView: Int = DEFAULT_NO_DATA_LAYOUT

    private var strNoDataMessage: String? = null
    private var colorNoDataMessage: Int? = null
    private var sizeNoDataMessage: Float? = null

    private var strNoDataTitle: String? = null
    private var colorNoDataTitle: Int? = null
    private var sizeNoDataTitle: Float? = null

    private var resImgNoData: Int? = null
    private var urlImgNoData: String? = null
    private var drawableNoData: Drawable? = null

    /**
     * method used to set message into no data layout
     * @param string message
     */
    fun setMessage(string: String?) {
        setNoDataMessage(string)
    }

    /**
     * method used to set title into no data layout
     * @param string title
     */
    fun setTitle(string: String?) {
        setNoDataTitle(string)
    }

    /**
     * method used to set image into no data layout
     * @param string url/file path
     */
    fun setImageUrl(string: String?) {
        string?.let { setNoDataImage(it) }
    }

    /**
     * method used to setup item spacing in recycler view
     * @param itemSpacing spacing value
     */
    fun setItemSpacing(itemSpacing: Float) {
        if (itemSpacing > 0) {
            getRecyclerView().addItemDecoration(RecyclerItemDecoration(itemSpacing.toInt()))
        }
    }

    companion object {
        //No data layout
        val DEFAULT_NO_DATA_LAYOUT = R.layout.empty_view_layout
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /**
     * to get recycler list view this method is used
     * @return RecyclerView
     */
    fun getRecyclerView(): RecyclerView = findViewById(R.id.recyclerListView)

    /**
     * to get no data layouts parent view this method is used
     * @return FrameLayout
     */
    fun getNoDataContainer(): FrameLayout = findViewById(R.id.noDataFoundContainer)

    /**
     * to get no data layouts title textview this method is used
     * @return TextView
     */
    fun getTitleTextView(): TextView = findViewById(R.id.lblNoDataFound)

    /**
     * to get no data layouts message textview this method is used
     * @return TextView
     */
    fun getMessageTextView(): TextView = findViewById(R.id.lblMsgNoDataFound)

    /**
     * to get no data layouts image view this method is used
     * @return ImageView
     */
    fun getNoDataImageView(): ImageView = findViewById(R.id.imgNoData)

    /**
     * to get refresh layout view object this method is used
     * @return SwipeRefreshLayout
     */
    fun getRefreshLayout(): SwipeRefreshLayout = findViewById(R.id.refreshLayout)

    /**
     * method is used to implement attributes value from the xml
     * which call from the constructor
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        context.apply {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.pagination_view_layout, this@PaginationView, true)
        }

        attrs?.apply {
            val recyclerAttr = context.obtainStyledAttributes(
                this, R.styleable.PaginationView,
                0, 0
            )
            recyclerViewAttribute(context, recyclerAttr)
            noDataViewAttribute(recyclerAttr)
            recyclerAttr.recycle()
        }
    }

    /**
     * setup recycler view attribute
     */
    private fun recyclerViewAttribute(context: Context, attrs: TypedArray) {
        val layoutManager: Int = attrs.getInt(R.styleable.PaginationView_listLayoutManager, 1)
        val spanCount = attrs.getInt(R.styleable.PaginationView_spanCount, 2)
        val itemSpacing = attrs.getDimension(R.styleable.PaginationView_itemSpacing, 0f)
        setItemSpacing(itemSpacing)


        val grid = 2
        val staggered = 3

        getRecyclerView().layoutManager = when (layoutManager) {
            grid -> GridLayoutManager(context, spanCount)
            staggered -> StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
            else ->
                LinearLayoutManager(context)
        }
    }

    /**
     * setup no data layout attribute
     */
    private fun noDataViewAttribute(attrs: TypedArray) {
        val titleText = attrs.getString(R.styleable.PaginationView_title)
        setNoDataTitle(titleText)

        val titleTextColor = attrs.getColor(R.styleable.PaginationView_titleTextColor, Color.BLACK)
        setNoDataTitleTextColor(titleTextColor)

        val titleTextSize = attrs.getDimension(R.styleable.PaginationView_titleTextSize, 0f)
        if (titleTextSize > 0f)
            setNoDataTitleTextSize(titleTextSize)

        val messageText = attrs.getString(R.styleable.PaginationView_message)
        setNoDataMessage(messageText)

        val messageTextColor =
            attrs.getColor(R.styleable.PaginationView_messageTextColor, Color.BLACK)
        setNoDataMessageTextColor(messageTextColor)

        val messageTextSize = attrs.getDimension(R.styleable.PaginationView_messageTextSize, 0f)
        if (messageTextSize > 0f)
            setNoDataMessageTextSize(messageTextSize)

        val bgColor =
            attrs.getColor(R.styleable.PaginationView_noDataVewBg, Color.WHITE)
        setMainViewBgColor(bgColor)

        val customNoDataVewRes = attrs.getResourceId(
            R.styleable.PaginationView_customNoDataVew,
            DEFAULT_NO_DATA_LAYOUT
        )

        setNoDataLayoutRes(customNoDataVewRes)

        if (!isInEditMode) {
            val url = attrs.getString(R.styleable.PaginationView_imageUrl)

            if (url != null) {
                setNoDataImage(url)
            } else {
                val drawable = attrs.getDrawable(R.styleable.PaginationView_image)
                drawable?.let { setNoDataImage(it) }
            }
        } else {
            val drawable = attrs.getDrawable(R.styleable.PaginationView_image)
            drawable?.let { getNoDataImageView().setImageDrawable(it) }
        }
    }

    /**
     * method which used to identify refresh layout is enabled or not.
     * @return boolean if the refresh layout is enable then it returns true
     */
    fun isRefreshLayoutEnable(): Boolean = getRefreshLayout().isEnabled

    /**
     * method which used to setup the refresh layout enabled or disable.
     * @param boolean the true/false value to enable/disable the refresh layout.
     */
    fun setRefreshLayoutEnable(boolean: Boolean) {
        getRefreshLayout().isEnabled = boolean
    }

    /**
     * method which used to identify refresh layout is refreshing(loading).
     * @return boolean if the refresh layout is refreshing(loading) then it returns true
     */
    fun isRefreshing(): Boolean = getRefreshLayout().isRefreshing

    /**
     * method which used to setup the refresh layout refreshing(loading).
     * @param boolean the true/false value to enable/disable the refreshing(loading).
     */
    fun setRefreshing(boolean: Boolean) {
        getRefreshLayout().isRefreshing = boolean
    }

    /**
     * Set the callback to be notified when a refresh is triggered via the swipe
     * gesture.
     * @param onRefreshCallback the callback
     */
    fun setOnRefresh(onRefreshCallback: (() -> Unit)? = null) {
        onRefreshCallback?.let { onRefresh ->
            getRefreshLayout().setOnRefreshListener {
                onRefresh()
            }
        }
    }

    /**
     * to set no data layout custom view
     * @param resNoDataView is layout id
     */
    fun setNoDataLayoutRes(resNoDataView: Int) {
        if (resNoDataView != DEFAULT_NO_DATA_LAYOUT) {
            this.resNoDataView = resNoDataView
            buildNoDataView(resNoDataView)
        }
    }

    /**
     * from no data layout resource ID we inflate the new view
     * @param resNoDataView layout id
     */
    private fun buildNoDataView(resNoDataView: Int) {
        getNoDataContainer().removeAllViews()
        LayoutInflater.from(context).inflate(resNoDataView, getNoDataContainer(), true)
        validateNewNoDataView()

        setNoDataTitle(strNoDataTitle)
        colorNoDataTitle?.let { setNoDataTitleTextColor(it) }
        sizeNoDataTitle?.let { setNoDataTitleTextSize(it) }

        setNoDataMessage(strNoDataMessage)
        colorNoDataMessage?.let { setNoDataMessageTextColor(it) }
        sizeNoDataMessage?.let { setNoDataMessageTextSize(it) }

        resImgNoData?.let { setNoDataImage(it) }
        urlImgNoData?.let { setNoDataImage(it) }
        drawableNoData?.let { setNoDataImage(it) }

    }

    /**
     * validate new no data layout, if not valid then throw the exception
     */
    private fun validateNewNoDataView() {
        var notFoundKey = ""
        hashMapOf(
            R.id.imgNoData to "imgNoData",
            R.id.lblNoDataFound to "lblNoDataFound",
            R.id.lblMsgNoDataFound to "lblMsgNoDataFound",
        ).filter {
            getNoDataContainer().findViewById<View?>(it.key) == null
        }.values.forEach {
            notFoundKey += "$it,"
        }
        notFoundKey = notFoundKey.removeSuffix(",")

        if (notFoundKey.isNotEmpty()) {
            throw IllegalArgumentException(
                "Unable to find the id=> $notFoundKey; " +
                        "for custom No data layout file add the following id for view" +
                        "\n imgNoData => ImageView as No data Image" +
                        "\n lblNoDataFound => TextView as Title" +
                        "\n lblMsgNoDataFound => TextView as Message"
            )
        }
    }


    /**
     * message to show in no data view
     * @param int is resource id of string
     */
    fun setNoDataMessage(@StringRes int: Int) {
        strNoDataMessage = context.getString(int)
        getMessageTextView().text = strNoDataMessage
    }

    /**
     * message to show in no data view
     * @param string is the message to show
     */
    fun setNoDataMessage(string: String?) {
        string?.let {
            strNoDataMessage = string
            getMessageTextView().text = it
        }
    }

    /**
     * title to show in no data view
     * @param string is the string title to show
     */
    fun setNoDataTitle(string: String?) {
        string?.let {
            strNoDataTitle = string
            getTitleTextView().text = it
        }
    }

    /**
     * set title text color
     * @param color is Int color value
     */
    fun setNoDataTitleTextColor(@ColorInt color: Int) {
        this.colorNoDataTitle = color
        getTitleTextView().setTextColor(color)
    }

    /**
     * set title text size
     * @param size is the float size value
     */
    fun setNoDataTitleTextSize(size: Float) {
        this.sizeNoDataTitle = size
        getTitleTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    /**
     * set message text color
     * @param color is Int color value
     */
    fun setNoDataMessageTextColor(@ColorInt color: Int) {
        this.colorNoDataMessage = color
        getMessageTextView().setTextColor(color)
    }

    /**
     * set message text size
     * @param size is the float size value
     */
    fun setNoDataMessageTextSize(size: Float) {
        this.sizeNoDataMessage = size
        getMessageTextView().setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    /**
     * set no data view background color
     * @param color is Int color value
     */
    fun setMainViewBgColor(@ColorInt color: Int) {
        if (getNoDataContainer().childCount > 0) {
            getNoDataContainer().getChildAt(0).setBackgroundColor(color)
        }
    }

    /**
     * title to show in no data view
     * @param int is resource id of string
     */
    fun setNoDataTitle(@StringRes int: Int) {
        strNoDataTitle = context.getString(int)
        getTitleTextView().text = strNoDataTitle
    }

    /**
     * image to load from file path or network url for no data layout
     * @param string is the url or file path
     */
    fun setNoDataImage(string: String) {
        urlImgNoData = string
        if (urlImgNoData != null) {
            Glide.with(getNoDataImageView()).load(urlImgNoData).into(getNoDataImageView())
        }
    }

    /**
     * image to load for no data layout
     * @param drawable is drawable object to load
     */
    fun setNoDataImage(drawable: Drawable) {
        drawableNoData = drawable
        Glide.with(getNoDataImageView()).load(drawable).into(getNoDataImageView())
    }

    /**
     * image to load for no data layout
     * @param res is drawable resource id
     */
    fun setNoDataImage(@DrawableRes res: Int) {
        resImgNoData = res
        if (resImgNoData != 0) {
            Glide.with(getNoDataImageView()).load(resImgNoData).into(getNoDataImageView())
        }
    }

    /**
     * to visible or invisible no data layout view this method is used.
     * @param bool set true for visible no data layout
     */
    fun isNothingToLoad(bool: Boolean) {
        getRecyclerView().isVisible = !bool
        getNoDataContainer().post {
            getNoDataContainer().isVisible = bool
            validateTitleAndMessageImage()
        }
    }

    /**
     * method is use to validate visibility state of title or message when [isNothingToLoad] called,
     * if title don't have text then it will not visible, same way if message don't have text the it will not visible,
     * when the both title and message don't have text then we set default message here.
     */
    private fun validateTitleAndMessageImage() {
        getMessageTextView().isVisible = !getMessageTextView().text.isNullOrEmpty()

        getTitleTextView().isVisible = !getTitleTextView().text.isNullOrEmpty()

        getNoDataImageView().isVisible =
            !(urlImgNoData.isNullOrEmpty() && drawableNoData == null && resImgNoData == null)

        if (!getTitleTextView().isVisible && !getMessageTextView().isVisible && !getNoDataImageView().isVisible) {
            getMessageTextView().isVisible = true
            getMessageTextView().text = context.getString(R.string.msg_no_data_found)
        }
    }
}