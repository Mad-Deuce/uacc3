package dms.interceptor;

import dms.config.multitenant.DatabaseSessionManager;
import dms.config.multitenant.TenantIdentifierResolver;
import dms.dao.schema.SchemaDao;
import dms.dao.schema.SchemaDaoImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Component
public class GlobalInterceptor implements HandlerInterceptor {


    private final DatabaseSessionManager dsm;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final SchemaDao sm;

    public GlobalInterceptor(DatabaseSessionManager dsm,
                             TenantIdentifierResolver tenantIdentifierResolver,
                             SchemaDaoImpl sm) {
        this.dsm = dsm;
        this.tenantIdentifierResolver = tenantIdentifierResolver;
        this.sm = sm;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy_MM_dd");
//        String schemaName;
//        String SchemaDateCookieValue = "";
//        if (request.getCookies() != null) {
//            SchemaDateCookieValue = Arrays.stream(request.getCookies())
//                    .filter(cookie -> cookie.getName().equals("schema_date"))
//                    .map(Cookie::getValue)
//                    .findAny().orElse(null);
//        }
//        if (SchemaDateCookieValue != null && !SchemaDateCookieValue.equals("")) {
////            schemaName = sm.getDrtuSchemaNameListByDate(LocalDate.parse(SchemaDateCookieValue));
//            schemaName = sm.DRTU_SCHEMA_NAME + SchemaDateCookieValue;
//
//            if (!sm.isSchemaExists(schemaName)) {
//                schemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
//            }
////            if (schemaName == null) {
////                schemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
////            }
//        } else {
//            schemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
//        }
//
//        tenantIdentifierResolver.setCurrentTenant(schemaName);
//
//        //        String currentSchemaName = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
//        String strDate = schemaName.substring(sm.DRTU_SCHEMA_NAME.length());
//        LocalDate chDate = LocalDate.parse(strDate, formatter);
//
//        Cookie cookie = new Cookie("schema_date", chDate.toString());
//        cookie.setPath("/api/devices/view/");
//        cookie.setDomain("localhost");
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(86400);
//        response.addCookie(cookie);
//        System.out.println("In preHandle method");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("In postHandle method");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

//        System.out.println("In afterCompletion method");
    }
}
