package com.example.transfer.manager;

import javax.swing.*;

class MultiLineToolTip extends JToolTip {
    public MultiLineToolTip() {
        setUI(new MultiLineToolTipUI());
    }
}