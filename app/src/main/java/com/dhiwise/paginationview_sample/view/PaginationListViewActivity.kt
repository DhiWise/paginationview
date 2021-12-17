package com.dhiwise.paginationview_sample.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.dhiwise.paginationview.pagination.bindinghelper.CustomLoadingItemHelper
import com.dhiwise.paginationview.pagination.bindinghelper.PaginationBinder
import com.dhiwise.paginationview.pagination.bindinghelper.PaginationBinder.PageBindingCallback
import com.dhiwise.paginationview.pagination.view.PaginationView
import com.dhiwise.paginationview_sample.R
import com.dhiwise.paginationview_sample.databinding.ActivityPaginationViewBinding
import com.dhiwise.paginationview_sample.databinding.RowSimpleListItemBinding
import com.dhiwise.paginationview_sample.model.SimpleModel


public class PaginationListViewActivity : AppCompatActivity(),
    PageBindingCallback {
    private val MAX_ELEMENTS_COUNT: Int = 50 // if list element are grater than this, we don't load anything
    lateinit var binding: ActivityPaginationViewBinding

    private var paginationBinder: PaginationBinder? = null

    private val recyclerListViewList: MutableLiveData<MutableList<SimpleModel>> =
        MutableLiveData(mutableListOf())

    var pageElementCount = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_pagination_view
        ) as ActivityPaginationViewBinding
        binding.lifecycleOwner = this
        setUpClicks()
        onInitialized()
    }

    public fun setUpClicks(): Unit {
        binding.switchNoData.setOnCheckedChangeListener { compoundButton, b ->
            setupPagination()
            recyclerListViewList.value = arrayListOf()
        }
    }


    public fun onInitialized(): Unit {
        binding.listButton.adapter = BasicViewAdapter(
            R.layout.row_button,
            arrayListOf("5", "10", "15", "20")
        ) { item, vh ->
            vh.itemView.findViewById<Button>(R.id.btnAction).apply {
                text = item
                setOnClickListener {
                    if (binding.switchNoData.isChecked) {
                        binding.switchNoData.isChecked = false
                    }

                    pageElementCount = item.toInt()
                    setupPagination()
                    recyclerListViewList.value = arrayListOf()
                }
            }
        }
        recyclerListViewList.value = arrayListOf()

        val recyclerListViewAdapter =
            BasicViewAdapter(
                R.layout.row_simple_list_item,
                recyclerListViewList.value ?: mutableListOf()
            ) { item, vh ->
                val innerBinding =
                    DataBindingUtil.bind<RowSimpleListItemBinding>(vh.view)
                innerBinding?.simpleModel = item
            }
        binding.paginationView.adapter = recyclerListViewAdapter

        recyclerListViewList.observe(this) {
            recyclerListViewAdapter.updateData(it)
        }

        setupPagination()
    }

    private fun setupPagination() {
        if (paginationBinder == null)
            paginationBinder =
                PaginationBinder.buildWith(pageElementCount, this)
                    .setOnRefresh {
                        setupPagination()
                        recyclerListViewList.value = arrayListOf()
                    }
                    .setOnLoadingItemSpanCountCallback {
                        val spanSize = 1 // your span size of LoadingView
                        spanSize
                    }
                    .setCustomLoadingItemHelper(object :
                        CustomLoadingItemHelper(R.layout.loading_item) {
                        override fun onItemBind(layout: View) {
                            layout.findViewById<TextView>(R.id.tvLoading)
                                .setText("Loading Page : ${paginationBinder!!.getCurrentPage() + 1}")
                        }
                    })
                    .build()
        else {
            paginationBinder!!.reset(pageElementCount)
        }
    }

    override fun onLoadNext(
        newPageToLoad: Int,
        currentLoadedItemCnt: Int,
        pageElementCount: Int
    ) {
        if (binding.switchNoData.isChecked) {
            paginationBinder?.onLoadFinish(0)
        } else {
            dummyApiCall(newPageToLoad, pageElementCount)
        }
    }

    override fun onNoDataFound() {
        //TODO handle on No data loaded
    }

    override fun onAllItemLoaded() {
        //TODO handle on data loaded
    }

    override fun getPaginationView(): PaginationView {
        return binding.paginationView
    }

    fun dummyApiCall(newPageToLoad: Int, pageElementCount: Int) {

        var size = recyclerListViewList.value!!.size

        if (size > MAX_ELEMENTS_COUNT) {
            //pass the same list size so pagination will disable automatically
            paginationBinder?.onLoadFinish(size)
            return
        }

        /*Toast.makeText(
            this@PaginationListViewActivity,
            "Loading Page $newPageToLoad",
            Toast.LENGTH_LONG
        ).show()*/

        binding.paginationView.postDelayed({
            recyclerListViewList.value?.let {
                it.addAll((1..pageElementCount).map {
                    SimpleModel("Page : $newPageToLoad", "page element number :- #$it")
                })
                recyclerListViewList.value = it
            }

            val size = recyclerListViewList.value?.size ?: 0
            paginationBinder?.onLoadFinish(size)
        }, 2000)
    }
}
