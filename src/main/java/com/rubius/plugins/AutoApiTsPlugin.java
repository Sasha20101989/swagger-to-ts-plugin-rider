package com.rubius.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class AutoApiTsPlugin extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        AutoApiTsDialog dialog = new AutoApiTsDialog(e);
        dialog.showAndGet();
    }
}