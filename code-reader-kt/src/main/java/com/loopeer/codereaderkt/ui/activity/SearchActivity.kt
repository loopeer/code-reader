package com.loopeer.codereaderkt.ui.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.loopeer.codereaderkt.R


class SearchActivity : BaseActivity(), SearchView.OnQueryTextListener {

    private lateinit var mRepositoryFragment: RepositoryFragment
    private lateinit var mSearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mRepositoryFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_repository) as RepositoryFragment
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_file_search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mSearchView = menu.findItem(R.id.action_search).actionView as SearchView
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        mSearchView.onActionViewExpanded()
        mSearchView.maxWidth = Integer.MAX_VALUE
        mSearchView.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        if (!TextUtils.isEmpty(query) && mRepositoryFragment != null) {
            Log.d("SearchActivityLog", " searchText is " + query)
            mRepositoryFragment.setSearchText(query)
            mSearchView.clearFocus()
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean = false
}