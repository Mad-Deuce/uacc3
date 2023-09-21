package dms.dao;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SchemaManager {

    @PersistenceContext
    private EntityManager em;

    public final String DRTU_SCHEMA_NAME = "drtu";
    public final String DOCK_SCHEMA_NAME = "dock";

    public void renameSchema(String oldName, String newName) {
        String queryString = String.format("ALTER SCHEMA %s RENAME TO %s", oldName, newName);
        em.createNativeQuery(queryString)
                .executeUpdate();
    }

    public void removeSchema(String schemaName) {
        String queryString = String.format("DROP SCHEMA IF EXISTS %s CASCADE;", schemaName);
        em.createNativeQuery(queryString)
                .executeUpdate();
    }

    public void restoreEmpty() {
        String command = "pg_restore -U postgres -w -d rtubase " +
                "/vagrant/ansible/roles/postgresql/files/d20230324.backup";

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

    public void createDevicesMainView() {
        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.doWork(connection ->
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/CreateDeviceMainView.sql"))
        );
        session.close();
    }

    public String getSchemaNameListByDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String dateString = date.format(formatter);
        List<String> schemaNameList = getSchemaNameList();
        return schemaNameList.stream().filter(value -> value.equals(DRTU_SCHEMA_NAME + dateString))
                .findFirst().orElse(null);
    }

    public List<String> getSchemaNameList() {
        return em.createNativeQuery(
                        "SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE :schemaName"
                ).setParameter("schemaName", DRTU_SCHEMA_NAME + "_%")
                .getResultList();
    }

    public List<String> getReceivedFileNameList(String schemaName) {
        String queryString = String.format("SELECT name FROM %s.dev_trans", schemaName);
        return em.createNativeQuery(queryString)
                .getResultList();
    }

}
