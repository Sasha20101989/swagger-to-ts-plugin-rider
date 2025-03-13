package com.rubius.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class AutoApiCsPlugin extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        AutoApiCsDialog dialog = new AutoApiCsDialog(e);
        dialog.showAndGet();
    }
}