package com.smihajlovski.instabackstack.ui.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.ACTION
import com.smihajlovski.instabackstack.common.Constants.DATA_KEY_1
import com.smihajlovski.instabackstack.common.Constants.DATA_KEY_2
import com.smihajlovski.instabackstack.common.Constants.TAB_DASHBOARD
import com.smihajlovski.instabackstack.common.Constants.TAB_HOME
import com.smihajlovski.instabackstack.common.Constants.TAB_NOTIFICATIONS
import com.smihajlovski.instabackstack.databinding.ActivityMainBinding
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.addAdditionalTabFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.addInitialTabFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.addShowHideFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.removeFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.showHideTabFragment
import com.smihajlovski.instabackstack.utils.StackListManager.resetTabStackIndex
import com.smihajlovski.instabackstack.utils.StackListManager.updateStackIndex
import com.smihajlovski.instabackstack.utils.StackListManager.updateStackToIndexFirst
import com.smihajlovski.instabackstack.utils.StackListManager.updateTabStackIndex
import java.util.*

class MainActivity : AppCompatActivity(), BaseFragment.FragmentInteractionCallback {

    private var stacks: MutableMap<String, Stack<Fragment>>? = null
    private var currentTab: String? = null
    private var binder: ActivityMainBinding? = null
    private var stackList: MutableList<String>? = null
    private var menuStacks: MutableList<String>? = null
    private var currentFragment: Fragment? = null
    private var homeFragment: Fragment? = null
    private var dashboardFragment: Fragment? = null
    private var notificationFragment: Fragment? = null
    private val handler = Handler()
    private val runnable = Runnable { doublePressToExit = false }
    private var doublePressToExit = false


    private val onNavigationItemSelectedListener = { item: MenuItem ->
        when (item.getItemId()) {
            R.id.tab_home -> {
                if (menuStacks?.size == 1 && menuStacks?.get(0) != TAB_HOME) {
                    resetSelectedTab(TAB_HOME)
                } else {
                    selectedTab(TAB_HOME)
                }
                true
            }
            R.id.tab_dashboard -> {
                selectedTab(TAB_DASHBOARD)
                true
            }
            R.id.tab_notifications -> {
                selectedTab(TAB_NOTIFICATIONS)
                true
            }
        }
        false
    }

    private val currentFragmentFromShownStack: Fragment
        get() = stacks!!.get(currentTab)!!.elementAt(stacks!!.get(currentTab)!!.size - 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        createStacks()
    }

    override fun onFragmentInteractionCallback(bundle: Bundle) {
        val action = bundle.getString(ACTION)

        if (action != null) {
            when (action) {
                HomeFragment.ACTION_DASHBOARD -> showFragment(bundle, DashboardFragment())
                DashboardFragment.ACTION_NOTIFICATION -> showFragment(bundle, NotificationsFragment())
                NotificationsFragment.ACTION_DASHBOARD -> showFragment(bundle, DashboardFragment())
            }
        }
    }

    override fun onBackPressed() {
        resolveBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        handler?.removeCallbacks(runnable)
    }

    private fun createStacks() {
        binder!!.bottomNavigationView.inflateMenu(R.menu.bottom_nav_tabs)
        binder!!.bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        homeFragment = HomeFragment()
        dashboardFragment = DashboardFragment()
        notificationFragment = NotificationsFragment()

        stacks = LinkedHashMap()
        stacks!![TAB_HOME] = Stack()
        stacks!![TAB_DASHBOARD] = Stack()
        stacks!![TAB_NOTIFICATIONS] = Stack()

        menuStacks = ArrayList()
        menuStacks!!.add(TAB_HOME)

        stackList = ArrayList()
        stackList!!.add(TAB_HOME)
        stackList!!.add(TAB_DASHBOARD)
        stackList!!.add(TAB_NOTIFICATIONS)

        binder!!.bottomNavigationView.selectedItemId = R.id.tab_home
    }

    private fun resetSelectedTab(tabId: String) {

        currentTab = tabId
        BaseFragment.currentTab = currentTab!!

        /*
         * We are switching tabs, and target tab already has at least one fragment.
         * Show the target fragment
         */
        showHideTabFragment(supportFragmentManager, stacks!![tabId]!!.lastElement(), currentFragment!!)
        resetStackLists(tabId)
        assignCurrentFragment(stacks!![tabId]!!.lastElement())
    }

    private fun selectedTab(tabId: String) {

        currentTab = tabId
        BaseFragment.currentTab = currentTab!!

        if (stacks!![tabId]!!.size == 0) {
            /*
             * First time this tab is selected. So add first fragment of that tab.
             * We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            when (tabId) {
                TAB_HOME -> {
                    addInitialTabFragment(supportFragmentManager, stacks!!, TAB_HOME, homeFragment!!, R.id.frame_layout, true)
                    resolveStackLists(tabId)
                    assignCurrentFragment(homeFragment)
                }
                TAB_DASHBOARD -> {
                    addAdditionalTabFragment(supportFragmentManager, stacks!!, TAB_DASHBOARD, dashboardFragment!!, currentFragment!!, R.id.frame_layout, true)
                    resolveStackLists(tabId)
                    assignCurrentFragment(dashboardFragment)
                }
                TAB_NOTIFICATIONS -> {
                    addAdditionalTabFragment(supportFragmentManager, stacks!!, TAB_NOTIFICATIONS, notificationFragment!!, currentFragment!!, R.id.frame_layout, true)
                    resolveStackLists(tabId)
                    assignCurrentFragment(notificationFragment)
                }
            }
        } else {
            /*
             * We are switching tabs, and target tab already has at least one fragment.
             * Show the target fragment
             */
            showHideTabFragment(supportFragmentManager, stacks!![tabId]!!.lastElement(), currentFragment!!)
            resolveStackLists(tabId)
            assignCurrentFragment(stacks!![tabId]!!.lastElement())
        }
    }

    private fun popFragment() {
        /*
         * Select the second last fragment in current tab's stack,
         * which will be shown after the fragment transaction given below
         */
        val fragment = stacks!!.get(currentTab)!!.elementAt(stacks!!.get(currentTab)!!.size - 2)

        /*pop current fragment from stack */
        stacks!!.get(currentTab)!!.pop()

        removeFragment(supportFragmentManager, fragment, currentFragment!!)

        assignCurrentFragment(fragment)
    }

    private fun resolveBackPressed() {
        var stackValue = 0
        if (stacks!!.get(currentTab)!!.size == 1) {
            val value = stacks!![stackList!![1]]
            if (value!!.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu()
            }
            if (stackValue <= 1) {
                if (menuStacks!!.size > 1) {
                    navigateToPreviousMenu()
                } else {
                    finish()
                }
            }
        } else {
            popFragment()
        }
    }

    /*Pops the last fragment inside particular tab and goes to the second tab in the stack*/
    private fun popAndNavigateToPreviousMenu() {
        val tempCurrent = stackList!![0]
        currentTab = stackList!![1]
        BaseFragment.currentTab = currentTab!!
        binder!!.bottomNavigationView.selectedItemId = resolveTabPositions(currentTab!!)
        showHideTabFragment(supportFragmentManager, stacks!![currentTab!!]!!.lastElement(), currentFragment!!)
        assignCurrentFragment(stacks!![currentTab!!]!!.lastElement())
        updateStackToIndexFirst(stackList!!, tempCurrent)
        menuStacks!!.removeAt(0)
    }

    private fun navigateToPreviousMenu() {
        menuStacks!!.removeAt(0)
        currentTab = menuStacks!![0]
        BaseFragment.currentTab = currentTab!!
        binder!!.bottomNavigationView.selectedItemId = resolveTabPositions(currentTab!!)
        showHideTabFragment(supportFragmentManager, stacks!![currentTab!!]!!.lastElement(), currentFragment!!)
        assignCurrentFragment(stacks!![currentTab!!]!!.lastElement())
    }

    /*
     * Add a fragment to the stack of a particular tab
     */
    private fun showFragment(bundle: Bundle, fragmentToAdd: Fragment) {
        val tab = bundle.getString(DATA_KEY_1)
        val shouldAdd = bundle.getBoolean(DATA_KEY_2)
        addShowHideFragment(supportFragmentManager, stacks!!, tab, fragmentToAdd, currentFragmentFromShownStack, R.id.frame_layout, shouldAdd)
        assignCurrentFragment(fragmentToAdd)
    }

    private fun resolveTabPositions(currentTab: String): Int {
        var tabIndex = 0
        when (currentTab) {
            TAB_HOME -> tabIndex = R.id.tab_home
            TAB_DASHBOARD -> tabIndex = R.id.tab_dashboard
            TAB_NOTIFICATIONS -> tabIndex = R.id.tab_notifications
        }
        return tabIndex
    }

    private fun resolveStackLists(tabId: String) {
        updateStackIndex(stackList!!, tabId)
        updateTabStackIndex(menuStacks!!, tabId)
    }

    private fun resetStackLists(tabId: String) {
        updateStackIndex(stackList!!, tabId)
        resetTabStackIndex(menuStacks!!, tabId)
    }

    private fun assignCurrentFragment(current: Fragment?) {
        currentFragment = current
    }
}
