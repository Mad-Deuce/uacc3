package dms.service.schema;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.schema.SchemaDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SchemaServiceImpl implements SchemaService {


    @Autowired
    private SchemaDao schemaDao;

    @Autowired
    private TenantIdentifierResolver tenantIdentifierResolver;

    @Override
    public List<LocalDate> getDatesOfExistingSchemas() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        List<LocalDate> result = new ArrayList<>();
        List<String> schemaNameList = schemaDao.getSchemaNameListLikeString(schemaDao.DRTU_SCHEMA_NAME + "_%");
        schemaNameList.forEach(item -> {
            LocalDate date = LocalDate.parse(item.substring(schemaDao.DRTU_SCHEMA_NAME.length()), formatter);
            result.add(date);
        });
        return result;
    }

    @Override
    public LocalDate getDateOfActiveSchema() {
        LocalDate result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
        String dateString = tenantIdentifierResolver.resolveCurrentTenantIdentifier()
                .substring(schemaDao.DRTU_SCHEMA_NAME.length());
        try {
            result = LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            log.warn("May be Schema with date suffix is not exists!!! Will return default date - 01/01/1900!!!");
            result = LocalDate.of(1900,1,1);
        }
        return result;
    }

    @Override
    public LocalDate setActiveSchemaDate(LocalDate schemaDate) {
        String schemaNameSuffix = ("_" + schemaDate).replace("-", "_");
        List<String> schemaNameList = schemaDao.getSchemaNameListLikeString(schemaDao.DRTU_SCHEMA_NAME + "_%");
        if (schemaNameList.contains(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix))
            tenantIdentifierResolver.setCurrentTenant(schemaDao.DRTU_SCHEMA_NAME + schemaNameSuffix);
        return getDateOfActiveSchema();
    }

    //        todo - must be moved in other class
    public void restoreFromFile() {
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


}
