package com.rubius.plugins;

import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CSharpGenerator {
    public static boolean generateCSharpClient(String swaggerUrl, String outputPath, String namespace, String exceptionPath, String os) {
//        if (!NetworkUtils.isUrlAccessible(swaggerUrl)) {
//            Messages.showErrorDialog("Swagger URL is not accessible: " + swaggerUrl +
//                    "\nPlease check the URL and your network connection.", "Error");
//        }

        ClassLoader classLoader = CSharpGenerator.class.getClassLoader();
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

            String nswagExecutableName = "nswag";
            if (os.contains("win")) {
                nswagExecutableName += ".exe";
            }

            File nswagExecutable = new File(tempDir, nswagExecutableName);
            if (!nswagExecutable.exists() || !nswagExecutable.setExecutable(true)) {
                return false;
            }

            String[] nswagCommand = {
                    nswagExecutable.getAbsolutePath(),
                    "openapi2csclient",
                    "/input:" + swaggerUrl,
                    "/output:" + outputPath,
                    "/className:{controller}ApiClient",
                    "/namespace:" + namespace,
                    "/GenerateClientClasses:true",
                    "/GenerateOptionalParameters:true",
                    "/ParameterDateTimeFormat:g",
                    "/UseBaseUrl:false",
                    "/GenerateExceptionClasses:false",
                    "/ExceptionClass:" + exceptionPath
            };

//            String[] nswagCommand = {
//                    nswagExecutable.getAbsolutePath(),
//                    "openapi2cscontroller",
//                    "/input:" + swaggerUrl,
//                    "/output:" + outputPath,
//                    "/className:Pm",
//                    "/namespace:" + "Application.ExternalApi.Pm",
//                    "/ControllerBaseClass:Application.Controllers.BaseController"
//            };

            FileUtils.deleteFileIfExists(outputPath);
            boolean result =  FileUtils.runProcess(nswagCommand);
            if (!result)
                return false;
        } catch (IOException e) {
            Messages.showErrorDialog("An error occurred during the process: " + e.getMessage(), "Error");
            return false;
        }
        
        return true;
    }
}