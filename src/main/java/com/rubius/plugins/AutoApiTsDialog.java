package com.rubius.plugins;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;

public class AutoApiTsDialog extends DialogWrapper {
    private final Project project;
    private final JBTextField urlField = new JBTextField();
    private final TextFieldWithBrowseButton tsPathField = new TextFieldWithBrowseButton();
    private final JBTextField tsFileNameField = new JBTextField("ApiClient.ts");

    public AutoApiTsDialog(com.intellij.openapi.actionSystem.AnActionEvent e) {
        super(true);
        this.project = e.getProject();
        init();
        setTitle("Swagger Configuration");

        tsPathField.addBrowseFolderListener(
                project,
                new FileChooserDescriptor(false, true, false, false, false, false)
                        .withTitle("Select Output Directory")
                        .withDescription("Choose a folder where the TypeScript client will be generated")
        );
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JBLabel("Swagger URL:"));
        panel.add(urlField);
        panel.add(new JBLabel("Output ts directory:"));
        panel.add(tsPathField);
        panel.add(new JBLabel("Output file name (.ts):"));
        panel.add(tsFileNameField);
        return panel;
    }

    @Override
    protected void doOKAction() {
        String url = urlField.getText().trim();
        String directoryTsPath = tsPathField.getText().trim();
        String tsFileName = tsFileNameField.getText().trim();
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Messages.showErrorDialog("Please enter a valid URL starting with 'http://' or 'https://'", "Invalid URL");
            return;
        }

        if (!url.endsWith(".json")) {
            Messages.showErrorDialog("The URL must end with '.json'", "Invalid URL");
            return;
        }

        if (directoryTsPath.isEmpty()) {
            Messages.showErrorDialog("Please select an output ts directory", "Invalid Path");
            return;
        }

        if (!tsFileName.endsWith(".ts")) {
            Messages.showErrorDialog("The output file name must end with '.ts'", "Invalid File Name");
            return;
        }
        
        String outputTsFilePath = directoryTsPath + "/" + tsFileName;

        String os = System.getProperty("os.name").toLowerCase();

        if (!os.contains("win") && !os.contains("mac")) {
            Messages.showErrorDialog("This plugin supports only Windows and macOS.", "Unsupported OS");
            return;
        }

        super.doOKAction();

        boolean resultTs = TypeScriptGenerator.generateTypeScriptClient(url, outputTsFilePath, os);
        if (resultTs) {
            FileUtils.openGeneratedFileInRider(outputTsFilePath, project);
        } else {
            Messages.showErrorDialog("Failed to generate Type Script file", "Error");
        }
    }
}