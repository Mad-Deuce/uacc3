package dms.service.rtubase;

import dms.DeviceAuthService;
import dms.entity.UserEntity;
import dms.jwt.JwtUserDetails;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@Primary
public class DeviceAuthServiceImpl implements DeviceAuthService {

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


}
