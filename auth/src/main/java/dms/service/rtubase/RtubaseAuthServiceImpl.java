package dms.service.rtubase;

import dms.RtubaseAuthService;
import dms.jwt.JwtUserDetails;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@Primary
public class RtubaseAuthServiceImpl implements RtubaseAuthService {

    @Override
    public String getAuthConditionsPartOfFindDeviceByFilterQuery (){
        StringBuilder queryAuthConditionsPart = new StringBuilder();
//        UserEntity principal = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JwtUserDetails principal = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if (principal != null) {
            queryAuthConditionsPart
//                    .append(" AND SUBSTRING(CAST (d.facility.id as string), 1, 3) ")
                    .append(" AND SUBSTRING(CAST (d.facility.id as string), 1, ")
                    .append(principal.getPermitCode().length())
                    .append(") ")
                    .append(" = '")
                    .append(principal.getPermitCode())
                    .append("' ")
            ;
        } else {
            return "";
        }
        return queryAuthConditionsPart.toString();
    }

    @Override
    public String getPrincipalPermitCode(){
        JwtUserDetails principal = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            return principal.getPermitCode();
        } else {
            return "";
        }
    }
}
