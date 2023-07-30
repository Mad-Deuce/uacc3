package dms.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Slf4j
public class CompressUtils {



    public static File extractGzip(File sourceFile) throws IOException {

        File targetFile = new File(sourceFile.getPath().substring(0, sourceFile.getPath().length() - 4));

        byte[] buffer = new byte[1024];
        FileInputStream fileIn = new FileInputStream(sourceFile);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        int bytes_read;
        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes_read);
        }
        gZIPInputStream.close();
        fileOutputStream.close();
        return targetFile;
    }

}
