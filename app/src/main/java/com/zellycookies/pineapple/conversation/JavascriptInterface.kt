package com.zellycookies.pineapple.conversation

import android.webkit.JavascriptInterface

class JavascriptInterface(val conversationActivity: ConversationActivity) {

    @JavascriptInterface
    public fun onPeerConnected() {
        conversationActivity.onPeerConnected()
    }

}