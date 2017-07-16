package com.tylerj.mdkdownloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MDKDownloader {
    String URLBase = "http://files.minecraftforge.net/maven/net/minecraftforge/forge/";
    String MDKVersion = "";
    String MinecraftVersion = "";
    String CompleteURL;
    String ModName = "";
    String BaseDirectory = "";
    String ZipName = "";
    String ZipFilePath = "";
    String DestDirectory = "";
    final int BUFFER_SIZE = 4096;

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

    private Path download(String sourceURL, String targetDirectory) throws IOException {
        URL url = new URL(sourceURL);
        String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
        Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));

        byte[] bytesIn = new byte[BUFFER_SIZE];

        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }

        bos.close();
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();


    }

    /**
     * Prepares the enviroment, by remove useless junk.
     * @throws Exception if anything fails.
     */
    public void prepareEnviroment() throws Exception {
        download(CompleteURL, BaseDirectory);

        unzip(ZipFilePath, DestDirectory);

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
    }
}

