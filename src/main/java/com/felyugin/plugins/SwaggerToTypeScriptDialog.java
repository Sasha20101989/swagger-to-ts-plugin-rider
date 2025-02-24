package com.felyugin.plugins;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;

public class SwaggerToTypeScriptDialog extends DialogWrapper {
    private final Project project;
    private final JBTextField urlField = new JBTextField();
    private final TextFieldWithBrowseButton pathField = new TextFieldWithBrowseButton();
    private final JBTextField fileNameField = new JBTextField("ApiClient.ts");

    public SwaggerToTypeScriptDialog(com.intellij.openapi.actionSystem.AnActionEvent e) {
        super(true);
        this.project = e.getProject();
        init();
        setTitle("Swagger to TypeScript Configuration");

        pathField.addBrowseFolderListener(
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
        panel.add(new JBLabel("Output directory:"));
        panel.add(pathField);
        panel.add(new JBLabel("Output file name (.ts):"));
        panel.add(fileNameField);
        return panel;
    }

    @Override
    protected void doOKAction() {
        String url = urlField.getText().trim();
        String directoryPath = pathField.getText().trim();
        String fileName = fileNameField.getText().trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Messages.showErrorDialog("Please enter a valid URL starting with 'http://' or 'https://'", "Invalid URL");
            return;
        }

        if (!url.endsWith(".json")) {
            Messages.showErrorDialog("The URL must end with '.json'", "Invalid URL");
            return;
        }

        if (directoryPath.isEmpty()) {
            Messages.showErrorDialog("Please select an output directory", "Invalid Path");
            return;
        }

        if (!fileName.endsWith(".ts")) {
            Messages.showErrorDialog("The output file name must end with '.ts'", "Invalid File Name");
            return;
        }

        String outputFilePath = directoryPath + "/" + fileName;

        super.doOKAction();

        boolean result = TypeScriptGenerator.generateTypeScriptClient(url, outputFilePath);
        if (result) {
            TypeScriptGenerator.replaceBaseUrl(outputFilePath);
            TypeScriptGenerator.addTsNoCheck(outputFilePath);
            FileUtils.openGeneratedFileInRider(outputFilePath, project);
        } else {
            Messages.showErrorDialog("Failed to generate TypeScript file", "Error");
        }
    }
}