package dms.config.multitenant;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer, InitializingBean {

    //    private String currentTenant = "public";
    private String currentTenant;
    @Autowired
    DataSource dataSource;

    public void setCurrentTenant(String tenant) {
        currentTenant = tenant;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        return currentTenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<String> result = jdbcTemplate
                .query("SELECT schema_name " +
                        "FROM information_schema.schemata " +
                        "WHERE schema_name LIKE 'drtu_%' ORDER BY 1 DESC ", new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                });
        currentTenant = result.get(0);

//        Connection connection = dataSource.getConnection();
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery("SELECT schema_name FROM information_schema.schemata ");
//
//        List<String> result = new ArrayList<>();
//        while (resultSet.next()) {
//
//            String name = resultSet.getString(1);
//            if (name.startsWith("drtu_")) {
//
//                result.add(name);
//            }
//        }
//        result.sort(Comparator.naturalOrder());
//        currentTenant = result.get(result.size() - 1);
//        System.out.println();
//        connection.close();
//        statement.close();
    }
}
