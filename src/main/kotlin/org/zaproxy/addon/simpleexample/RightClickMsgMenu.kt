/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.simpleexample

import org.parosproxy.paros.Constant
import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.view.View
import org.zaproxy.zap.view.messagecontainer.http.HttpMessageContainer
import org.zaproxy.zap.view.popup.PopupMenuItemHttpMessageContainer

/**
 * A pop up menu item shown in components that contain HTTP messages, it shows an internationalised
 * message with the request-uri of the HTTP message.
 *
 * @see HttpMessageContainer
 */
class RightClickMsgMenu     /*
         * This is how you can pass in your extension, which you may well need to use
         * when you actually do anything of use.
         */(private val extension: NextgenAutomationFramework, label: String?) : PopupMenuItemHttpMessageContainer(label) {
    public override fun performAction(msg: HttpMessage) {
        // This is where you do what you want to do.
        // In this case we'll just show a popup message.
        View.getSingleton()
            .showMessageDialog(
                Constant.messages.getString(
                    NextgenAutomationFramework.PREFIX + ".popup.msg",
                    msg.requestHeader.uri.toString()
                )
            )
    }

    override fun isEnableForInvoker(invoker: Invoker, httpMessageContainer: HttpMessageContainer): Boolean {
        // This pop up menu item is enabled for all tabs/components that have just one
        // message (selected, if it shows more than one and allows to selected them)
        // You can examine the invoker if you wish to restrict this to specific tabs
        return true
    }

    override fun isSafe(): Boolean {
        // The action of menu item does not do any (potentially) unsafe operation, like starting a
        // scan against a target.
        return true
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}