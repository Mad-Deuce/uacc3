package dms.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class CompressUtils {

    public static void extractGzip(String sourceFilePath, String targetFilePath) throws Exception {

        File file = new File(sourceFilePath);
        log.info("exists: " + file.exists());
        log.info("is dir: " + file.isDirectory());
        log.info("is file: " + file.isFile());
        log.info("can read: " + file.canRead());

        byte[] buffer = new byte[1024];
        FileInputStream fileIn = new FileInputStream(sourceFilePath);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
        FileOutputStream fileOutputStream = new FileOutputStream(targetFilePath);
        int bytes_read;
        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes_read);
        }
        gZIPInputStream.close();
        fileOutputStream.close();
    }

    @Deprecated
    public void unzipFile() {
        String fileZip = "rtubase/src/main/resources/pd_files/d1110714.001";

        File fz = new File(fileZip);

        File destDir = new File("rtubase/src/main/resources/pd_files");

        byte[] buffer = new byte[1024];

        try {
            FileInputStream fis = new FileInputStream(fz);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                // ...
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Deprecated
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


}
