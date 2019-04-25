package com.smihajlovski.instabackstack.utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.smihajlovski.instabackstack.common.Constants.ACTION
import com.smihajlovski.instabackstack.common.Constants.DATA_KEY_1
import com.smihajlovski.instabackstack.common.Constants.DATA_KEY_2
import com.smihajlovski.instabackstack.ui.base.BaseFragment.FragmentInteractionCallback
import java.util.*

object FragmentUtils {

    /*
     * Add the initial fragment, in most cases the first tab in BottomNavigationView
     */
    fun addInitialTabFragment(fragmentManager: FragmentManager,
                              stacks: Map<String, Stack<Fragment>>,
                              tag: String,
                              fragment: Fragment,
                              layoutId: Int,
                              shouldAddToStack: Boolean) {
        if (shouldAddToStack) stacks[tag]!!.push(fragment)
        fragmentManager
                .beginTransaction()
                .add(layoutId, fragment)
                .commit()
    }

    /*
     * Add additional tab in BottomNavigationView on click, apart from the initial one and for the first time
     */
    fun addAdditionalTabFragment(fragmentManager: FragmentManager,
                                 stacks: Map<String, Stack<Fragment>>,
                                 tag: String,
                                 show: Fragment,
                                 hide: Fragment,
                                 layoutId: Int,
                                 shouldAddToStack: Boolean) {
        if (shouldAddToStack) stacks[tag]!!.push(show)
        fragmentManager
                .beginTransaction()
                .add(layoutId, show)
                .show(show)
                .hide(hide)
                .commit()
    }

    /*
     * Hide previous and show current tab fragment if it has already been added
     * In most cases, when tab is clicked again, not for the first time
     */
    fun showHideTabFragment(fragmentManager: FragmentManager,
                            show: Fragment,
                            hide: Fragment) {
        fragmentManager
                .beginTransaction()
                .hide(hide)
                .show(show)
                .commit()
    }

    /*
     * Add fragment in the particular tab stack and show it, while hiding the one that was before
     */
    fun addShowHideFragment(fragmentManager: FragmentManager,
                            stacks: Map<String, Stack<Fragment>>,
                            tag: String,
                            show: Fragment,
                            hide: Fragment,
                            layoutId: Int,
                            shouldAddToStack: Boolean) {
        if (shouldAddToStack) stacks[tag]!!.push(show)
        fragmentManager
                .beginTransaction()
                .add(layoutId, show)
                .show(show)
                .hide(hide)
                .commit()
    }

    fun removeFragment(fragmentManager: FragmentManager, show: Fragment, remove: Fragment) {
        fragmentManager
                .beginTransaction()
                .remove(remove)
                .show(show)
                .commit()
    }

    /*
     * Send action from fragment to activity
     */
    fun sendActionToActivity(action: String, tab: String, shouldAdd: Boolean, fragmentInteractionCallback: FragmentInteractionCallback?) {
        val bundle = Bundle()
        bundle.putString(ACTION, action)
        bundle.putString(DATA_KEY_1, tab)
        bundle.putBoolean(DATA_KEY_2, shouldAdd)
        fragmentInteractionCallback?.onFragmentInteractionCallback(bundle)
    }

/*
    fun sendActionToActivity(bundlePair: BundlePair
            action: String,
                             tab: String,
                             shouldAdd: Boolean,
                             fragmentInteractionCallback: FragmentInteractionCallback?) {
        val bundle = Bundle()
        bundle.putString(ACTION, action)
        bundle.putString(DATA_KEY_1, tab)
        bundle.putBoolean(DATA_KEY_2, shouldAdd)
        fragmentInteractionCallback?.onFragmentInteractionCallback(bundle)
    }
*/
}
