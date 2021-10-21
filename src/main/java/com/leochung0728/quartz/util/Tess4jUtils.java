package com.leochung0728.quartz.util;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class Tess4jUtils {

	public static String readChar(File imageFile){
        ITesseract instance = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        instance.setLanguage("eng");
        return getOCRText(instance, imageFile);
    }
	
	private static String getOCRText(ITesseract instance, File imageFile){
        String result = null;
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public static void main(String[] args) {
		File file = new File("src/main/resources/image", "ValidateCode.jpg");
        System.out.println(readChar(file));
    }

}
