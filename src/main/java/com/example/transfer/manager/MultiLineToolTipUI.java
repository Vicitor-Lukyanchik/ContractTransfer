package com.example.transfer.manager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToolTipUI;
import java.awt.*;

class MultiLineToolTipUI extends BasicToolTipUI {
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null) return new Dimension(0, 0);

        FontMetrics metrics = c.getFontMetrics(c.getFont());
        int width = Math.min(metrics.stringWidth(tipText) + 20, 600); // Макс. ширина 600px
        int height = metrics.getHeight() * (tipText.split("\n").length) + 10;

        return new Dimension(width, height);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null) return;

        FontMetrics metrics = c.getFontMetrics(c.getFont());
        Dimension size = c.getSize();
        g.setColor(c.getBackground());
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(c.getForeground());

        String[] lines = tipText.split("\n");
        int y = metrics.getAscent() + 5;

        for (String line : lines) {
            g.drawString(line, 10, y);
            y += metrics.getHeight();
        }
    }
}