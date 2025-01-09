package dev.beecube31.crazyae2.common.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class SystemUtils {
    public static void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        StringSelection str = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(str, null);
    }

    public static String pasteFromClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);

            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException ignored) {}

        return null;
    }
}
