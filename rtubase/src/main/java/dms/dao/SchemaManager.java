package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import dms.dto.DeviceDTO;
import dms.utils.CompressUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
}
