package com.example.ozturkse.sinebu.listing

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.example.ozturkse.sinebu.R
import com.example.ozturkse.sinebu.licence.LicenceActivity
import com.example.ozturkse.sinebu.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: MoviesFragmentPagerAdapter
    private lateinit var tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // set toolbar
        app_bar_home_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(app_bar_home_toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_menu_white_24dp)
        }


        // drawer toggle
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, app_bar_home_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // nav drawer listener
        activity_home_navigation_view.setNavigationItemSelectedListener(this)

        viewPager = home_viewpager

        // Create the adapter that will return a fragment for each section of the activity.
        viewPagerAdapter = MoviesFragmentPagerAdapter(this, supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        viewPager.adapter = viewPagerAdapter

        tabLayout = app_bar_home_tablayout

       // tabLayout.getTabAt(0)

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager)


        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

    }




    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        // Search
        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.home_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.home_search -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_licence -> {
                startActivity(LicenceActivity.newIntent(this@HomeActivity))
            }
            R.id.nav_settings -> {
                startActivity(SettingsActivity.newIntent(this@HomeActivity))
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
