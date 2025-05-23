/*
 * Copyright (c) 2003, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/* @test
 * @bug 4890934
 * @summary Tests if JEditor Pane updates the correct frame when using <BASE target="xxxx">
 * @library /java/awt/regtesthelpers
 * @build PassFailJFrame
 * @run main/manual bug4890934
*/

import java.io.File;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class bug4890934 implements HyperlinkListener {

    static final String INSTRUCTIONS = """
        The test window displays an HTML frameset with a frame
        on the left and another to the right.
        Click the link in the left frame which should change the view.
        The resulting page will tell you if the test PASSED or FAILED.
    """;

    static JFrame createUI() {

        JFrame frame = new JFrame("bug4890934");
        JEditorPane jep = new JEditorPane();
        jep.setEditorKit(new HTMLEditorKit());
        jep.setEditable(false);
        jep.addHyperlinkListener(new bug4890934());

        try {
            File file = new File(System.getProperty("test.src", "."), "frameset.html");
            System.out.println(file.toURI().toURL());
            jep.setPage(file.toURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        frame.add(jep);
        frame.setSize(600, 300);
        return frame;
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        PassFailJFrame.builder()
            .title("Test Instructions")
            .instructions(INSTRUCTIONS)
            .columns(40)
            .testUI(bug4890934::createUI)
            .build()
            .awaitAndCheck();
    }
}
