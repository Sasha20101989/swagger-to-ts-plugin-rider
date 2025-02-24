package com.felyugin.plugins;

import com.intellij.openapi.ui.Messages;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TypeScriptGenerator {
    public static boolean generateTypeScriptClient(String swaggerUrl, String outputPath) {
        if (!NetworkUtils.isUrlAccessible(swaggerUrl)) {
            Messages.showErrorDialog("Swagger URL is not accessible: " + swaggerUrl +
                    "\nPlease check the URL and your network connection.", "Error");
            return false;
        }

        ClassLoader classLoader = TypeScriptGenerator.class.getClassLoader();
        String resourceFolder = "NSwag";

        try {
            File tempDir = Files.createTempDirectory("nswag").toFile();
            tempDir.deleteOnExit();

            URL jarUrl = classLoader.getResource(resourceFolder);
            if (jarUrl == null) {
                Messages.showMessageDialog("NSwag resource folder not found!", "Error", Messages.getErrorIcon());
                return false;
            }

            String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
            File jarFile = new File(URLDecoder.decode(jarPath, StandardCharsets.UTF_8));

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith("NSwag")) {
                        FileUtils.extractFileFromJar(entry, jar, tempDir);
                    }
                }
            }

            File nswagExecutable = new File(tempDir, "nswag");
            if (!nswagExecutable.exists() || !nswagExecutable.setExecutable(true)) {
                return false;
            }

            String[] nswagCommand = {
                    nswagExecutable.getAbsolutePath(),
                    "openapi2tsclient",
                    "/input:" + swaggerUrl,
                    "/output:" + outputPath,
                    "/template:axios",
                    "/className:{controller}ApiClient",
                    "/ClientBaseClass:BaseApiClient",
                    "/useTransformOptionsMethod:true",
                    "/generateOptionalParameters:true"
            };

            FileUtils.deleteFileIfExists(outputPath);
            return FileUtils.runProcess(nswagCommand);
        } catch (IOException e) {
            Messages.showErrorDialog("An error occurred during the process: " + e.getMessage(), "Error");
            return false;
        }
    }

    public static void replaceBaseUrl(String outputPath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(outputPath)));
            
            int index = content.indexOf("this.baseUrl = baseUrl ?? \"http");
            while (index != -1) {
                int endIndex = content.indexOf(";", index);
                if (endIndex != -1) {
                    content = content.substring(0, index) + "this.baseUrl = baseUrl ?? this.baseApiClient;" + content.substring(endIndex + 1);
                }
                index = content.indexOf("this.baseUrl = baseUrl ?? \"http", endIndex);
            }
            
            Files.write(Paths.get(outputPath), content.getBytes());

            System.out.println("BaseUrl replaced successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while modifying the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addTsNoCheck(String outputPath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(outputPath)));
            
            int index = content.indexOf("/* eslint-disable */");
            if (index != -1) {
                content = content.substring(0, index + "/* eslint-disable */".length()) + "\n// @ts-nocheck" + content.substring(index + "/* eslint-disable */".length());

                Files.write(Paths.get(outputPath), content.getBytes());

                System.out.println("Comments added successfully.");
            } else {
                System.out.println("No '/* eslint-disable */' found in the file.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while modifying the file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}