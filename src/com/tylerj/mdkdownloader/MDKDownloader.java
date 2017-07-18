package com.tylerj.mdkdownloader;

import org.json.simple.JSONArray;
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
    private final String URLBase = "http://files.minecraftforge.net/maven/net/minecraftforge/forge/";
    private final String MCVERSIONSJSONURL = "http://s3.amazonaws.com/Minecraft.Download/versions/versions";
    private final String MDKVERSIONSJSONURL = "http://files.minecraftforge.net/maven/net/minecraftforge/forge/json";
    private final int BUFFER_SIZE = 4096;

    private String MDKVersion = "";
    private String MinecraftVersion = "";
    private String CompleteURL;
    private String ModName = "";
    private String BaseDirectory = "";
    private String ZipName = "";
    private String ZipFilePath = "";
    private String DestDirectory = "";

    /**
     * Creates a new instance that downloads the Forge MDK.
     * @param mcVersion The Minecraft version you want to use.
     * @param forgeVersion Forge or MDK version you want to use.
     * @param modName The name of your mod.
     * @param path Where you want to store the mod.
     */
    public MDKDownloader(String mcVersion, String forgeVersion, String modName, String path) {
        MinecraftVersion = mcVersion;
        MDKVersion = forgeVersion;
        ModName = modName;
        BaseDirectory = path;

        ZipName = "forge-" + MinecraftVersion + "-" + MDKVersion + "-mdk" + ".zip";
        CompleteURL = URLBase + "/" + MinecraftVersion + "-" + MDKVersion + "/" + ZipName;
        ZipFilePath = BaseDirectory + ZipName;
        DestDirectory = BaseDirectory + modName;
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

    public void PrepareEnvironment() throws Exception {
        DownloadFile(CompleteURL, BaseDirectory);

        Unzip(ZipFilePath, DestDirectory);

        ArrayList<File> filesToDelete = new ArrayList<>();

        filesToDelete.add(new File(BaseDirectory + ZipName));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "README.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "Paulscode SoundSystem CodecIBXM License.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "Paulscode IBXM Library License.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "MinecraftForge-Credits.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "LICENSE-new.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "forge-" + MinecraftVersion + "-" + MDKVersion + "-changelog.txt"));
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "CREDITS-fml.txt"));

        // TODO Delete the eclipse folder.
        filesToDelete.add(new File(BaseDirectory + ModName + "/" + "eclipse/"));

        for (File f : filesToDelete) {
            f.delete();
        }

        for (String s : GetMDKVersions()) {
            System.out.println(s);
        }
    }

    public ArrayList<String> GetMCVersions() {
        ArrayList<String> MinecraftVersions = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("D:\\Documents\\Projects\\IntelliJ IDEA Projects\\MDKDownloader\\json\\minecraft.json"));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray versions = (JSONArray) jsonObject.get("versions");

            for (int i = 0; i < versions.size(); i++) {
                JSONObject mcversion = (JSONObject) versions.get(i);

                MinecraftVersions.add(mcversion.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  MinecraftVersions;
    }

    public ArrayList<String> GetMDKVersions() {
        ArrayList<String> MDKVersions = new ArrayList<>();

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("D:\\Documents\\Projects\\IntelliJ IDEA Projects\\MDKDownloader\\json\\mdk.json"));

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

