package dms;

public interface DeviceAuthService {

    default String getAuthConditionsPartOfFindDeviceByFilterQuery() {
        return "";
    }

    default String getPrincipalPermitCode() {
        return "";
    }
}
