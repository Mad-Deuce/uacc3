package dms.mapper;

import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.standing.data.service.sdev.SDevService;
import org.mapstruct.Context;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD)
public abstract class DeviceMapperAbstract {

//    public static final DeviceMapperAbstract INSTANCE = Mappers.getMapper(DeviceMapperAbstract.class);

    @Autowired
    protected SDevService deviceTypeService;


    @Mapping(target = "type", expression = "java(deviceTypeService.findSDevByID(dto.getTypeId()).orElse(null))")
    public abstract DeviceEntity dTOToEntity(DeviceDTO dto);
}
