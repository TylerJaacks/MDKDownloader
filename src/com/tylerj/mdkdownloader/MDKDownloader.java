package com.tylerj.mdkdownloader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MDKDownloader {
    private final String URLBASE = "http://files.minecraftforge.net/maven/net/minecraftforge/forge/";
    private final int BUFFER_SIZE = 4096;

    private String ModName = "";
    private String BaseDirectory = "";
    private String ZipName = "";

    /**
     * Creates a new instance that downloads the Forge MDK.
     */
    public MDKDownloader() {

    }

    private Path DownloadFile(String sourceURL, String targetDirectory) throws IOException {
        URL url = new URL(sourceURL);
        String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
        Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath;
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

    public void Unzip(String zipFilePath, String destDirectory) throws IOException {
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

    public void PrepareEnvironment(String version, String dest, String name) throws Exception {
        String s1 = version.substring(6, version.length());
        String s2 = s1.substring(0, s1.length() - 4);

        DownloadFile(URLBASE + s2 + "/" + version + ".zip", dest);

        System.out.println(s2);

        Unzip(dest + version + ".zip", dest + "/" + name);

        ArrayList<File> filesToDelete = new ArrayList<>();

        filesToDelete.add(new File(dest + version + ".zip"));
        filesToDelete.add(new File(dest + name + "/" + "README.txt"));
        filesToDelete.add(new File(dest + name + "/" + "Paulscode SoundSystem CodecIBXM License.txt"));
        filesToDelete.add(new File(dest + name + "/" + "Paulscode IBXM Library License.txt"));
        filesToDelete.add(new File(dest + name + "/" + "MinecraftForge-Credits.txt"));
        filesToDelete.add(new File(dest + name + "/" + "LICENSE-new.txt"));
        filesToDelete.add(new File(dest + name + "/" + "forge-" + s2 + "-changelog.txt"));
        filesToDelete.add(new File(dest + name + "/" + "CREDITS-fml.txt"));

        // TODO Delete the eclipse folder.
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "eclipse/"));

        for (File f : filesToDelete) {
            f.delete();
        }
    }

    public ArrayList<String> GetMDKVersions() {
        ArrayList<String> MDKVersions = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("json/mdk.json"));

            JSONObject object = (JSONObject) obj;
            JSONObject number = (JSONObject) object.get("number");

            for (int i = 1; i < 2415; i++) {
                if (number.get("" + i) != null) {
                    JSONObject id = (JSONObject) number.get("" + i);
                    String mcversion = (String) id.get("mcversion");
                    String mdkversion = (String) id.get("version");

                    String completeName = "forge-" + mcversion + "-" + mdkversion + "-mdk";

                    MDKVersions.add(completeName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return MDKVersions;
    }
}
