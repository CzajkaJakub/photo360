package pl.put.photo360.handlers;

import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@NoArgsConstructor
public class ZipHandler {

    public FileSystemResource createZipOfImages(String folderPath, String zipName) throws IOException {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory())
        {
            String zipFileName = folder.getName() + ".zip";
            String zipFilePath = folderPath + File.separator + zipFileName;

            File[] files = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp"));

            if (files == null || files.length == 0) {
                System.out.println("Brak plików obrazów do skompresowania.");
                return null;
            }

            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (File file : files) {
                    addToZipFile(file, zos);
                }
            }

            File file = new File(folder.getPath() + zipName);
            return new FileSystemResource(file);
        }
        return null;
    }

    public void deleteZipFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".zip"));

        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            file.delete();
        }
    }

    private void addToZipFile(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
        }
    }
}
