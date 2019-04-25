package com.smihajlovski.instabackstack.ui.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smihajlovski.instabackstack.R
import com.smihajlovski.instabackstack.common.Constants.NOTIFICATIONS_FRAGMENT
import com.smihajlovski.instabackstack.databinding.FragmentNotificationsBinding
import com.smihajlovski.instabackstack.ui.base.BaseFragment
import com.smihajlovski.instabackstack.utils.FragmentUtils.sendActionToActivity

class NotificationsFragment : BaseFragment() {
    private var binder: FragmentNotificationsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        init()
        return binder!!.root
    }

    private fun init() {
        binder!!.button.setOnClickListener { sendActionToActivity(ACTION_DASHBOARD, BaseFragment.currentTab, true, fragmentInteractionCallback) }
    }

    companion object {

        val ACTION_DASHBOARD = NOTIFICATIONS_FRAGMENT + "action.dashboard"
    }
}
