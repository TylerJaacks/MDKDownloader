package com.tylerj.mdkdownloader;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MDKDownloader {
    private final String URLBASE = "http://files.minecraftforge.net/maven/net/minecraftforge/forge/";
    private final String JSONFILE = URLBASE + "/json";
    private final int BUFFER_SIZE = 4096;

    public MDKDownloader() {
        try {
            DownloadFileWithName(JSONFILE, "mdk.json", System.getProperty("user.home"));
        } catch (Exception e) {

        }
    }

    private void DownloadFile(String sourceURL, String targetDirectory) throws IOException {
        URL url = new URL(sourceURL);
        String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
        Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void DownloadFileWithName(String sourceURL, String name, String targetDirectory) throws IOException {
        URL url = new URL(sourceURL);
        String fileName = name;
        Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void ExtractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));

        byte[] bytesIn = new byte[BUFFER_SIZE];

        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }

        bos.close();
    }

    private void Unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                ExtractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();
    }

    protected void PrepareEnvironment(String version, String dest, String name, String packageId) throws Exception {
        String s1 = version.substring(6, version.length());
        String s2 = s1.substring(0, s1.length() - 4);
        String modId = name.toLowerCase();
        String packageName = packageId;

        DownloadFile(URLBASE + s2 + "/" + version + ".zip", dest);

        Unzip(dest + version + ".zip", dest + "/" + name);

        ArrayList<File> filesToDelete = new ArrayList<>();

        if (version.contains("1.1-") || version.contains("1.2-") || version.contains("1.3-")
                || version.contains("1.4-") || version.contains("1.5-") || version.contains("1.6-")
                || version.contains("1.7-")) {
            Files.move(Paths.get(dest + name + "/forge"), Paths.get(dest + "/forge"));
            FileUtils.deleteDirectory(new File(dest + name));
            Files.move(Paths.get(dest + "/forge"), Paths.get(dest + name));

            filesToDelete.add(new File(dest + name + "/" + "minecraftforge_credits.txt"));
            filesToDelete.add(new File(dest + version + ".zip"));
        } else {
            filesToDelete.add(new File(dest + version + ".zip"));
            filesToDelete.add(new File(dest + name + "/" + "README.txt"));
            filesToDelete.add(new File(dest + name + "/" + "Paulscode SoundSystem CodecIBXM License.txt"));
            filesToDelete.add(new File(dest + name + "/" + "Paulscode IBXM Library License.txt"));
            filesToDelete.add(new File(dest + name + "/" + "MinecraftForge-Credits.txt"));
            filesToDelete.add(new File(dest + name + "/" + "LICENSE-new.txt"));
            filesToDelete.add(new File(dest + name + "/" + "forge-" + s2 + "-changelog.txt"));
            filesToDelete.add(new File(dest + name + "/" + "CREDITS-fml.txt"));
            filesToDelete.add(new File(dest + name + "/" + "eclipse"));
        }

        for (File f : filesToDelete) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                FileUtils.deleteDirectory(f);
            }
        }

        // TODO Replace Java file and delete extra directories
    }

    protected ArrayList<String> GetMDKVersions() {
        ArrayList<String> MDKVersions = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(System.getProperty("user.home") + "/mdk.json"));

            JSONObject object = (JSONObject) obj;
            JSONObject number = (JSONObject) object.get("number");

            // Thanks to https://github.com/hazarddev for the help writing the buildMax code
            Object[] keys = number.keySet().toArray();
            String[] stringifiedKeys = Arrays.copyOf(keys, keys.length, String[].class);

            int[] numberKeys = new int[stringifiedKeys.length];

            for(int i = 0; i < stringifiedKeys.length; i++){
                numberKeys[i] = Integer.parseInt(stringifiedKeys[i]);
            }

            Arrays.sort(numberKeys); //I'm lazy and won't be doing this by hand ~ jewsofhazard

            JSONObject largestNumberObject = (JSONObject) number.get(Integer.toString(numberKeys[numberKeys.length - 1]));

            long buildMax = (long) largestNumberObject.get("build");

            String completeName = "";

            for (int i = 1; i < buildMax; i++) {
                if (number.get("" + i) != null) {
                    JSONObject id = (JSONObject) number.get("" + i);
                    String mcversion = (String) id.get("mcversion");
                    String mdkversion = (String) id.get("version");
                    String fullname = "forge-" + mcversion + "-" + mdkversion + "-src";

                    if (fullname.contains("1.1-") || fullname.contains("1.2-") || fullname.contains("1.3-")
                            || fullname.contains("1.4-") || fullname.contains("1.5-") || fullname.contains("1.6-")
                            || fullname.contains("1.7-")) {
                        completeName = "forge-" + mcversion + "-" + mdkversion + "-src";
                    } else {
                        completeName = "forge-" + mcversion + "-" + mdkversion + "-mdk";
                    }

                    MDKVersions.add(completeName);
                }
            }

            Collections.reverse(MDKVersions);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return MDKVersions;
    }
}
