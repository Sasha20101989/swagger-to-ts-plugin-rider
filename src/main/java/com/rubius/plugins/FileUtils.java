package com.rubius.plugins;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {
    public static void extractFileFromJar(JarEntry entry, JarFile jar, File tempDir) throws IOException {
        File extractedFile = new File(tempDir, entry.getName().substring(6));
        if (entry.isDirectory()) {
            extractedFile.mkdirs();
        } else {
            try (InputStream is = jar.getInputStream(entry);
                 FileOutputStream fos = new FileOutputStream(extractedFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public static void deleteFileIfExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean runProcess(String[] command) throws IOException {
        Process process = new ProcessBuilder(command).start();
        try {
            return process.waitFor() == 0;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static void openGeneratedFileInRider(String path, Project project) {
        File file = new File(path);
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            } else {
                Messages.showErrorDialog("Failed to open generated file in Rider!", "Error");
            }
        });
    }
}