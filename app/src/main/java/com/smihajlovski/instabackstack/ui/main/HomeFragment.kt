package com.smihajlovski.instabackstack.ui.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.databinding.FragmentHomeBinding
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils

import com.smihajlovski.instabackstack.common.Constants.HOME_FRAGMENT

class HomeFragment : BaseFragment() {
    private var binder: FragmentHomeBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        init()
        return binder!!.root
    }

    private fun init() {
        binder!!.button.setOnClickListener { FragmentUtils.sendActionToActivity(ACTION_DASHBOARD, BaseFragment.currentTab, true, fragmentInteractionCallback!!) }
    }

    companion object {

        val ACTION_DASHBOARD = HOME_FRAGMENT + "action.dashboard"
    }
}
