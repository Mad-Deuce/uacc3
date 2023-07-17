package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class SchemaManager {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void createSchema(String schemaName) {
        em.createNativeQuery(
                        "CREATE SCHEMA IF NOT EXISTS " +
                                schemaName +
                                " AUTHORIZATION postgres")
                .executeUpdate();
    }

    @Transactional
    public void renameSchema(String oldName, String newName) {
        em.createNativeQuery(
                        "ALTER SCHEMA " +
                                oldName +
                                " RENAME TO " +
                                newName)
                .executeUpdate();
    }

    @Transactional
    public void removeSchema(String schemaName) {
        em.createNativeQuery(
                        "DROP SCHEMA " +
                                schemaName +
                                " CASCADE")
                .executeUpdate();
    }

    public void restoreEmpty() {
        String command = "pg_restore -U postgres -w -d rtubase " +
                "/vagrant/ansible/roles/postgresql/files/d20200602.backup";

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession("postgres", "localhost", 2222);
            session.setPassword("postgres");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            channel.setOutputStream(responseStream);

            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    log.info(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    log.info("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ignore) {
                }
            }


            while (channel.isConnected()) {
                Thread.sleep(100);
            }

//            String responseString = new String(responseStream.toByteArray());
//            System.out.println(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }


    }

    public void readFileLineByLine() {

        String path = "rtubase/src/main/resources/pd_files/d1011714";

        try {
            List<String> allLines = Files.readAllLines(Paths.get(path), Charset.forName("windows-1251"));

            for (String line : allLines) {

                System.out.println(line.length());
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unzipFile() {
        String fileZip = "rtubase/src/main/resources/pd_files/d1110714.001";

        File fz = new File(fileZip);
        log.info("exists: " + String.valueOf(fz.exists()));
        log.info("is dir: " + String.valueOf(fz.isDirectory()));
        log.info("is file: " + String.valueOf(fz.isFile()));
        log.info("can read: " + String.valueOf(fz.canRead()));

        File destDir = new File("rtubase/src/main/resources/pd_files");
        log.info("exists: " + String.valueOf(destDir.exists()));
        log.info("is dir: " + String.valueOf(destDir.isDirectory()));
        log.info("is file: " + String.valueOf(destDir.isFile()));
        log.info("can read: " + String.valueOf(destDir.canRead()));

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

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public void extractZip(String sourceFilePath, String destinationFilePath) throws Exception {
        File input = new File(sourceFilePath);
        InputStream is = new FileInputStream(input);
        ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("zip", is);
        ZipArchiveEntry entry = null;
        // ZipArchiveEntry entry = (ZipArchiveEntry) in.getNextEntry();
        while((entry = (ZipArchiveEntry) in.getNextEntry()) != null) {
            OutputStream out = new FileOutputStream(new File(destinationFilePath, entry.getName()));
            IOUtils.copy(in, out);
            out.close();
        }
        in.close();
    }

    public void extract7z(String sourceFilePath, String destinationFilePath) throws Exception {
        SevenZFile sevenZFile = new SevenZFile(new File(sourceFilePath));
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(destinationFilePath, "7z_" + entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(curfile);
            byte[] content = new byte[(int) entry.getSize()];
            sevenZFile.read(content, 0, content.length);
            out.write(content);
            out.close();
        }
        sevenZFile.close();
    }

    public void extractGzip(String sourceFilePath, String destinationFilePath) throws Exception {
        byte[] buffer = new byte[1024];
        FileInputStream fileIn = new FileInputStream(sourceFilePath);
        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
        FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePath);
        int bytes_read;
        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes_read);
        }
        gZIPInputStream.close();
        fileOutputStream.close();
    }

    public void extractTarGz(String decompressionMethod, String sourceFilePath, String destinationFilePath) throws Exception {
        TarArchiveInputStream fin = null;
        switch(decompressionMethod) {
            case "TAR":{
                fin = new TarArchiveInputStream(new FileInputStream(sourceFilePath));
                break;
            }
            case "TAR_GZ":{
                fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sourceFilePath)));
                break;
            }
        }

        TarArchiveEntry entry;
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(destinationFilePath + entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
            System.out.println("tesss");
        }
        fin.close();
    }



}
