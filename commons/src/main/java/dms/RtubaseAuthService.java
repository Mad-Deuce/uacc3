package dms;

public interface RtubaseAuthService {

    default String getAuthConditionsPartOfFindDeviceByFilterQuery() {
        return "";
    }

    default String getPrincipalPermitCode() {
        return "";
    }
}
