package com.felyugin.plugins;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String swaggerUrl = ""; // Путь к Swagger
        String outputPath = "/Users/aleksander/Developer/Rubius/team-rubius-back/api-clients/TypeScript/ApiClient.ts"; // Путь для сохранения TypeScript файла
        String nswagPath = Objects.requireNonNull(Main.class.getResource("/NSwag/nswag")).getPath();
        try {
            deleteFileIfExists(outputPath);
            
            System.out.println("Starting process...");
            
            File nswagFile = new File(nswagPath);
            if (!nswagFile.exists()) {
                System.out.println("NSwag executable not found at " + nswagPath);
                return;
            }
            
            System.out.println("NSwag executable found at: " + nswagPath);
            
            String[] nswagCommand = {
                    nswagPath,
                    "openapi2tsclient",
                    "/input:" + swaggerUrl,
                    "/output:" + outputPath,
                    "/template:axios",
                    "/className:{controller}ApiClient",
                    "/ClientBaseClass:BaseApiClient",
                    "/useTransformOptionsMethod:true",
                    "/generateOptionalParameters:true"
            };

            System.out.println("Running command: " + String.join(" ", nswagCommand));
            
            ProcessBuilder processBuilder = new ProcessBuilder(nswagCommand)
                    .redirectErrorStream(true);
            Process process = processBuilder.start();
            
            System.out.println("Reading process output...");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("Process output: " + line);
            }
            
            System.out.println("Full process output:\n" + output.toString());
            
            boolean processSuccess = process.waitFor(60, TimeUnit.SECONDS);
            if (processSuccess) {
                replaceBaseUrl(outputPath);
                addTsNoCheck(outputPath);
                addImports(outputPath);
            } else {
                System.out.println("NSwag command failed with exit code: " + process.exitValue());
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the process: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("The process was interrupted: " + e.getMessage());
            e.printStackTrace();
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

    public static void deleteFileIfExists(String outputPath) {
        try {
            File file = new File(outputPath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("Existing file deleted: " + outputPath);
                } else {
                    System.out.println("Failed to delete existing file: " + outputPath);
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the file: " + e.getMessage());
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

    public static void addImports(String outputPath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(outputPath)));

            int index = content.indexOf("import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, CancelToken } from 'axios';");
            if (index != -1) {
                String imports = "\n\nimport { BaseApiClient } from './BaseApiClient';";
                content = content.substring(0, index + "import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, CancelToken } from 'axios';".length()) + imports + content.substring(index + "import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse, CancelToken } from 'axios';".length());
            } else {
                String imports = "\nimport { BaseApiClient } from './BaseApiClient';";
                content = imports + content;
            }

            // Сохраняем изменения в файл
            Files.write(Paths.get(outputPath), content.getBytes());

            System.out.println("Imports added successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while adding imports: " + e.getMessage());
            e.printStackTrace();
        }
    }
}