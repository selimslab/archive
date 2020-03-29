package com.example.ozturkse.sinebu.listing

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.ozturkse.sinebu.R

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MoviesFragmentPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        return MoviesFragment.newInstance(position)
    }

    // determine the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return context.getString(R.string.now_playing)
            1 -> return context.getString(R.string.popular)
            2 -> return context.getString(R.string.top_rated)
            3 -> return context.getString(R.string.upcoming)
            else -> {
                return null
            }
        }
    }

    // determine the number of tabs
    override fun getCount(): Int {
        return 4
    }


}