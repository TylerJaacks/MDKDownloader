package com.tylerj.mdkdownloader;

public class Main {
    static String MDKVersion = "";
    static String MinecraftVersion = "";
    static String modName = "";
    static String baseDirectory = "/Users/tylerjaacks/";
    static MDKDownloader mdkDownloader;

    public static void main(String[] args) throws Exception {
        MinecraftVersion = args[0];
        MDKVersion = args[1];
        modName = args[2];

        System.out.println("Minecraft Version: " + MinecraftVersion);
        System.out.println("MDK Version: " + MDKVersion);

        mdkDownloader = new MDKDownloader(MinecraftVersion, MDKVersion, modName, baseDirectory);

        mdkDownloader.prepareEnviroment();
    }
}

