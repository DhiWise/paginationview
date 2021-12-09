package com.dhiwise.bindinghelper

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhiwise.view.PaginationView
import com.paginate.Paginate
import com.paginate.recycler.LoadingListItemSpanLookup

/**
 * The class which binds the pagination view with pagination callback
 *
 * ## NOTE: don't use constructor object, instead of it use build method [PaginationBinder.buildWith]
 */
class PaginationBinder() {
    private lateinit var pageBindingCallback: PageBindingCallback
    private lateinit var paginationCallback : SimplePaginateCallback

    /**
     * private constructor
     * @param pageElementCount is total elements in One Page
     * @param pageBindingCallback is callback which handles from fragment or activity.
     */
    private constructor(pageElementCount: Int,pageBindingCallback: PageBindingCallback):this(){
        this.pageBindingCallback = pageBindingCallback
        paginationCallback  = SimplePaginateCallback(pageElementCount, pageBindingCallback)
    }

    private var paginate: Paginate? = null
    private var isEmptyViewEnable = true


    /**
     * method is used to get current loaded page,
     * @return int value of current page.
     */
    fun getCurrentPage(): Int = paginationCallback.page

    companion object {

        /**
         * used to create builder option before binding [PaginationView].
         *
         * @param pageElementCount is total elements in One Page
         * @param onLoadNextPage is the instance of [PageBindingCallback] use to handle callbacks if needed
         *
         * @return [PaginationOption] which contains different options
         */
        fun buildWith(
            pageElementCount: Int,
            onLoadNextPage: PageBindingCallback,
        ): PaginationOption {
            return PaginationOption(pageElementCount, onLoadNextPage)
        }
    }


    /**
     * method is use to enable or disable load more functionality.
     */
    fun enable(bool: Boolean) {
        paginationCallback.isEnable = bool
        val last = (pageBindingCallback.getPaginationView().adapter?.itemCount ?: 0) - 1
        if (last >= 0)
            pageBindingCallback.getPaginationView().adapter?.notifyItemChanged(last)
    }

    /**
     * method is needs to be call after new data notified in adapter.
     * @param totalItems is the total items count after list data change,
     * once totalItems and old item count are equals means all data are loaded and it disable load more functionality and also call [PageBindingCallback.onAllItemLoaded].
     * also if totalItems is zero then it shows noDataLayout and also call [PageBindingCallback.onNoDataFound]
     */
    fun onLoadFinish(totalItems: Int) {
        paginationCallback.loading = false
        pageBindingCallback.getPaginationView().setRefreshing(false)
        if (totalItems == 0) {
            paginationCallback.isEnable = false
            pageBindingCallback.onNoDataFound()

            if (isEmptyViewEnable)
                pageBindingCallback.getPaginationView().isNothingToLoad(true)

        } else {
            if (isEmptyViewEnable)
                pageBindingCallback.getPaginationView().isNothingToLoad(false)

            if (totalItems == paginationCallback.totalLoadedItems) {
                pageBindingCallback.onAllItemLoaded()
                enable(false)
            } else {
                paginationCallback.page++
            }
        }
        paginationCallback.totalLoadedItems = totalItems
    }


    /**
     * method is used to reset the page element count and it starts the pages from initial value
     * @param pageElementCount is the new page element count value in One Page.
     */
    fun reset(
        pageElementCount: Int,
    ) {
        paginationCallback.page = 0
        paginationCallback.totalLoadedItems = 0
        paginationCallback.loading = false
        paginationCallback.pageElementCount = pageElementCount
        paginate?.setHasMoreDataToLoad(true)
        paginationCallback.isEnable = true
        pageBindingCallback.getPaginationView().isNothingToLoad(false)
    }

    /**
     * the interface is use to handle pagination callbacks events and provides data to binder
     */
    interface PageBindingCallback {

        /**
         * method is called when we reached at [PaginationOption.threshold] value for loading next page data,
         * @param newPageToLoad is next page count value
         * @param currentLoadedItemCnt is the total loaded items count
         * @param pageElementCount is the number of items needs to be load
         */
        fun onLoadNext(
            newPageToLoad: Int,
            currentLoadedItemCnt: Int,
            pageElementCount: Int
        )

        /**
         * method is called when ever there is no data found in list item count
         * once [PaginationBinder.onLoadFinish] called and the count value is zero at that time this method is called
         */
        fun onNoDataFound()

        /**
         * method is called when after loading next page,total list item count is equals to old list item count
         * once [PaginationBinder.onLoadFinish] called and the count value is same as old list item count at that time this method is called
         */
        fun onAllItemLoaded()

        /**
         * method is used to get [PaginationView] object when needed in PaginationBinder,
         * we don't want to hold any view object so the callback method is best way to handle that.
         */
        fun getPaginationView(): PaginationView
    }

    /**
     * class is used to apply different options before bind with [PaginationView],
     * once all option are set at that time must call [build] method, to bind the [PaginationView] with [PageBindingCallback]
     */
    class PaginationOption(
        private var pageElementCount: Int,
        private var pageBindingCallback: PageBindingCallback,
        private var threshold: Int = 1
    ) {


        private var onRefreshCallback: (() -> Unit)? = null
        private var customLoadingItemHelper: CustomLoadingItemHelper? = null
        private var onLoadingSpanLookup: LoadingListItemSpanLookup? = null

        private var isLoadingViewEnabled: Boolean = true
        private var isEmptyViewEnable: Boolean = true
        private var isRefreshLayoutEnable: Boolean = true

        private var resNoDataView: Int = PaginationView.DEFAULT_NO_DATA_LAYOUT

        private var strNoDataMessage: String? = null
        private var strNoDataTitle: String? = null

        private var sizeNoDataTitle: Float? = null
        private var sizeNoDataMessage: Float? = null

        private var colorNoDataTitle: Int? = null
        private var colorNoDataMessage: Int? = null


        private var resNoDataMessage: Int? = null
        private var resNoDataTitle: Int? = null

        private var drawableImgNoData: Drawable? = null
        private var resImgNoData: Int? = null

        private var urlImgNoData: String? = null


        /**
         * set one page element count to load
         * @param count - int value
         * @return PaginationOption
         */
        fun setPageElementCount(count: Int): PaginationOption {
            this.pageElementCount = count
            return this
        }

        /**
         * add threshold value to load next page
         * @param value - int value
         * @return PaginationOption
         */
        fun setThreshold(value: Int): PaginationOption {
            this.threshold = value
            return this
        }

        /**
         * to set refresh layout enable or disable
         * @param bool - true/false value
         * @return PaginationOption
         */
        fun setIsRefreshLayoutEnable(bool: Boolean): PaginationOption {
            isRefreshLayoutEnable = bool
            return this
        }

        /**
         * to set empty view layout enable or disable
         * @param bool - true/false value
         * @return PaginationOption
         */
        fun setIsEmptyViewEnable(bool: Boolean): PaginationOption {
            isEmptyViewEnable = bool
            return this
        }

        /**
         * to set list loading view enable or disable
         * @param bool - true/false value
         * @return PaginationOption
         */
        fun setIsLoadingViewEnable(bool: Boolean): PaginationOption {
            isLoadingViewEnabled = bool
            return this
        }

        /**
         * to set refresh callback
         * @param callback - function parameter to handle refresh event.
         * @return PaginationOption
         */
        fun setOnRefresh(callback: () -> Unit): PaginationOption {
            onRefreshCallback = callback
            return this
        }

        /**
         * to set custom loading view layout
         * @param loadingItem - CustomLoadingItemHelper object
         * @return PaginationOption
         */
        fun setCustomLoadingItemHelper(loadingItem: CustomLoadingItemHelper): PaginationOption {
            customLoadingItemHelper = loadingItem
            return this
        }

        /**
         * to set loading view span count callback
         * @param loadingItemSpanCountCallback - which return Int as span count when list is type of grid or staggered is used.
         * @return PaginationOption
         */
        fun setOnLoadingItemSpanCountCallback(loadingItemSpanCountCallback: () -> Int): PaginationOption {
            onLoadingSpanLookup = LoadingListItemSpanLookup {
                loadingItemSpanCountCallback()
            }
            return this
        }

        /**
         * to set title of no data found layout
         * @param int - string resource id
         * @return PaginationOption
         */
        fun setNoDataTitle(@StringRes int: Int): PaginationOption {
            resNoDataTitle = int
            return this
        }

        /**
         * to set message of no data found layout
         * @param int - string resource id
         * @return PaginationOption
         */
        fun setNoDataMessage(@StringRes int: Int): PaginationOption {
            resNoDataMessage = int
            return this
        }

        /**
         * to set message text size of no data found layout
         * @param float - text size as float
         * @return PaginationOption
         */
        fun setNoDataMessageSize(float: Float): PaginationOption {
            sizeNoDataMessage = float
            return this
        }

        /**
         * to set message text color of no data found layout
         * @param color - color value int
         * @return PaginationOption
         */
        fun setNoDataMessageColor(@ColorInt color: Int): PaginationOption {
            colorNoDataMessage = color
            return this
        }

        /**
         * to set title text size of no data found layout
         * @param float - text size as float
         * @return PaginationOption
         */
        fun setNoDataTitleSize(float: Float): PaginationOption {
            sizeNoDataTitle = float
            return this
        }

        /**
         * to set title text color of no data found layout
         * @param color - color value int
         * @return PaginationOption
         */
        fun setNoDataTitleColor(@ColorInt color: Int): PaginationOption {
            colorNoDataTitle = color
            return this
        }

        /**
         * to set message of no data found layout
         * @param string - string value for message
         * @return PaginationOption
         */
        fun setNoDataMessage(string: String): PaginationOption {
            strNoDataMessage = string
            return this
        }

        /**
         * to set title of no data found layout
         * @param string - string value for title
         * @return PaginationOption
         */
        fun setNoDataTitle(string: String): PaginationOption {
            strNoDataTitle = string
            return this
        }

        /**
         * to set file path or network url for no data layout image
         * @param string - string value for url or file path
         * @return PaginationOption
         */
        fun setNoDataImage(string: String): PaginationOption {
            urlImgNoData = string
            return this
        }

        /**
         * to set drawable for no data layout image
         * @param drawable is drawable object to load
         * @return PaginationOption
         */
        fun setNoDataImage(drawable: Drawable): PaginationOption {
            drawableImgNoData = drawable
            return this
        }

        /**
         * to set drawable from resource id for no data layout image
         * @param res is drawable resource id
         * @return PaginationOption
         */
        fun setNoDataImage(@DrawableRes res: Int): PaginationOption {
            resImgNoData = res
            return this
        }

        /**
         * to set no data layout custom view
         * @param res is layout id
         * @return PaginationOption
         */
        fun setNoDataLayoutRes(@LayoutRes res: Int): PaginationOption {
            resNoDataView = res
            return this
        }

        /**
         * method is used to bind [PaginationView] with [PageBindingCallback]
         * @return [PaginationBinder] which handles the callbacks
         */
        fun build(): PaginationBinder {
            val paginationView = pageBindingCallback.getPaginationView()
            paginationView.setRefreshLayoutEnable(isRefreshLayoutEnable)
            paginationView.setOnRefresh(onRefreshCallback)

            if (isEmptyViewEnable) {
                paginationView.setNoDataLayoutRes(resNoDataView)
                paginationView.setNoDataTitle(strNoDataTitle)
                paginationView.setNoDataMessage(strNoDataMessage)
                resNoDataMessage?.let { paginationView.setNoDataMessage(it) }
                resNoDataTitle?.let { paginationView.setNoDataTitle(it) }


                resImgNoData?.let { paginationView.setNoDataImage(it) }
                drawableImgNoData?.let { paginationView.setNoDataImage(it) }
                urlImgNoData?.let { paginationView.setNoDataImage(it) }

                sizeNoDataTitle?.let { paginationView.setNoDataTitleTextSize(it) }
                sizeNoDataMessage?.let { paginationView.setNoDataMessageTextSize(it) }

                colorNoDataTitle?.let { paginationView.setNoDataTitleTextColor(it) }
                colorNoDataMessage?.let { paginationView.setNoDataMessageTextColor(it) }


            }

            val paginationBinder =
                PaginationBinder(pageElementCount, pageBindingCallback)

            if (paginationView.getRecyclerView().layoutManager == null) {
                //Add default linear layout manager
                paginationView.getRecyclerView().layoutManager =
                    LinearLayoutManager(paginationView.context)
            }

            pageBindingCallback.getPaginationView().isNothingToLoad(false)

            paginationBinder.isEmptyViewEnable = isEmptyViewEnable

            paginationView.post {
                val paginate = getPaginate(paginationView, paginationBinder.paginationCallback)

                paginationBinder.paginate = paginate
            }

            return paginationBinder
        }

        /**
         * method which returns paginate object for binding list view
         */
        private fun getPaginate(
            paginationView: PaginationView,
            callbacks: Paginate.Callbacks
        ): Paginate? {
            return Paginate.with(paginationView.getRecyclerView(), callbacks).apply {
                customLoadingItemHelper?.let { setLoadingListItemCreator(it.listItemCreator) }
                onLoadingSpanLookup?.let { setLoadingListItemSpanSizeLookup(it) }
                addLoadingListItem(isLoadingViewEnabled)
                setLoadingTriggerThreshold(threshold)
            }.build()
        }
    }

}