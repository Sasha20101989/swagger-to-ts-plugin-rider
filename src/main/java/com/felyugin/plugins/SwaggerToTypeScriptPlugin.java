package com.felyugin.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class SwaggerToTypeScriptPlugin extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        SwaggerToTypeScriptDialog dialog = new SwaggerToTypeScriptDialog(e);
        dialog.showAndGet();
    }
}