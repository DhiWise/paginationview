package com.dhiwise.bindinghelper

import com.paginate.Paginate

/**
 * class used to manage simple pagination events callback.
 * @param pageElementCount is number of element in one page.
 * @param pageBindingCallback is callback which handles from fragment or activity.
 */
class SimplePaginateCallback(
    var pageElementCount: Int,
    private val pageBindingCallback: PaginationBinder.PageBindingCallback
) : Paginate.Callbacks {

    var loading = false
    var isEnable = true
    var page = 0
    var totalLoadedItems = 0

    /**
     * override method called when list scrolled to the bottom or reached at the threshold from page bottom
     */
    override fun onLoadMore() {
        if (!hasLoadedAllItems()) {
            loading = true
            pageBindingCallback.onLoadNext(
                page + 1,
                totalLoadedItems,
                pageElementCount
            )
        }
    }

    /**
     * override method to manage loading state of loading view in list,
     * @return boolean - return true if loading is ongoing
     */
    override fun isLoading(): Boolean {
        return loading
    }

    /**
     * to identify is anything to load more,
     * @return boolean - return true if all items are loaded and nothing to load.
     */
    override fun hasLoadedAllItems(): Boolean {
        return !isEnable
    }
}