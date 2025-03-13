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

public class AutoApiCsDialog extends DialogWrapper {
    private final Project project;
    private final JBTextField urlField = new JBTextField();
    private final TextFieldWithBrowseButton csPathField = new TextFieldWithBrowseButton();
    private final JBTextField csFileNameField = new JBTextField("ApiClient.cs");
    private final JBTextField csNamespaceField = new JBTextField("Application.ExternalApi.Example");
    private final JBTextField csExceptionClassPathField = new JBTextField("Infrastructure.ExternalApi.Exceptions.ApiException");

    public AutoApiCsDialog(com.intellij.openapi.actionSystem.AnActionEvent e) {
        super(true);
        this.project = e.getProject();
        init();
        setTitle("Swagger Configuration");
        
        csPathField.addBrowseFolderListener(
                project,
                new FileChooserDescriptor(false, true, false, false, false, false)
                        .withTitle("Select Output Directory")
                        .withDescription("Choose a folder where the C Sharp client will be generated")
        );
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JBLabel("Swagger URL:"));
        panel.add(urlField);
        panel.add(new JBLabel("Output cs directory:"));
        panel.add(csPathField);
        panel.add(new JBLabel("Output file name (.cs):"));
        panel.add(csFileNameField);
        panel.add(new JBLabel("File namespace:"));
        panel.add(csNamespaceField);
        panel.add(new JBLabel("Exception class path:"));
        panel.add(csExceptionClassPathField);
        return panel;
    }

    @Override
    protected void doOKAction() {
        String url = urlField.getText().trim();
        String directoryCsPath = csPathField.getText().trim();
        String csFileName = csFileNameField.getText().trim();
        String csNamespace = csNamespaceField.getText().trim();
        String csExceptionClassPath = csExceptionClassPathField.getText().trim();
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Messages.showErrorDialog("Please enter a valid URL starting with 'http://' or 'https://'", "Invalid URL");
            return;
        }

        if (!url.endsWith(".json")) {
            Messages.showErrorDialog("The URL must end with '.json'", "Invalid URL");
            return;
        }

        if (directoryCsPath.isEmpty()) {
            Messages.showErrorDialog("Please select an output cs directory", "Invalid Path");
            return;
        }

        if (!csFileName.endsWith(".cs")) {
            Messages.showErrorDialog("The output file name must end with '.cs'", "Invalid File Name");
            return;
        }
        
        String outputCsFilePath = directoryCsPath + "/" + csFileName;

        String os = System.getProperty("os.name").toLowerCase();

        if (!os.contains("win") && !os.contains("mac")) {
            Messages.showErrorDialog("This plugin supports only Windows and macOS.", "Unsupported OS");
            return;
        }

        super.doOKAction();
        
        boolean resultCs = CSharpGenerator.generateCSharpClient(url, outputCsFilePath, csNamespace, csExceptionClassPath, os);
        if (resultCs) {
            FileUtils.openGeneratedFileInRider(outputCsFilePath, project);
        } else {
            Messages.showErrorDialog("Failed to generate C Sharp file", "Error");
        }
    }
}