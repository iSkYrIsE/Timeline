package Utils;

import javafx.stage.FileChooser;
import main.Main;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileController {

    static String mainFolderName = "time_manager";
    String selectedFilePath;
    static File customDir  = new File(System.getProperty("user.home"), mainFolderName);

    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            Scanner sc = new Scanner(file);
            String str = "";
            while(sc.hasNext()) {
                str = sc.nextLine();
                sb.append(str + "\n");
            }
            sc.close();

        }
        catch (IOException e) {
            e.printStackTrace ();
        }
        return sb.toString();

    }

    public static File writeFile(File file, String str) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(str);
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getAbsolutePath (File file) {
        return file.getAbsolutePath();
    }

    public static File fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle ("Select File");

        File selectedFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
        return selectedFile;
    }

    public static String imageChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle ("Select File");
        fileChooser.setTitle("Select  Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.PNG","*.jpg","*.jpeg","*.JPG","*.JPEG"));
        File selectedFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if(selectedFile!=null) {
            if (!hasHomeDirectory()) {
                customDir.mkdirs();
            }
            try {
                //FileUtils.copyFileToDirectory(selectedFile, customDir);
                File to = new File(System.getProperty("user.home"), mainFolderName+"/"+selectedFile.getName());
                copyFile(selectedFile, to);
                return to.toURI().toString();
            }catch (Exception e) {
                System.err.println("Error moving file");
                System.err.println(e.getMessage());
                return selectedFile.toURI().toString();
            }
        }
        return null;
    }

    public static String extractFileName(String path) {
       String[] parts = path.split("/");
       if(parts.length>0) {
           return parts[parts.length-1];
       }
       return path;
    }

    public static void copyFile(File from, File to) throws IOException{
        FileUtils.copyFile(from, to);
    }

    public static void moveFileToDir(File file) throws IOException {
        if (!hasHomeDirectory()) {
            customDir.mkdirs();
        }
        FileUtils.copyFileToDirectory(file, customDir);

    }

    public static boolean hasHomeDirectory() {
        if (customDir.exists()) {
            return true;
        }
        else {
            return false;
        }
    }
}
