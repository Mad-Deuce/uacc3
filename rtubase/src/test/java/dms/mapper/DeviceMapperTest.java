package dms.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dms.dto.DeviceDTO;
import dms.entity.DeviceEntity;
import dms.entity.DeviceLocationEntity;
import dms.standing.data.dock.val.LocateType;
import dms.standing.data.dock.val.RegionType;
import dms.standing.data.dock.val.Status;
import dms.standing.data.entity.DeviceTypeEntity;
import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.entity.RtuObjectEntity;
import dms.standing.data.service.sdev.SDevService;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeviceMapperTest {

//    @Resource(name="classpath: entity.json")
//    private org.springframework.core.io.Resource defaultFile;

    @Autowired
    private DeviceMapperAbstract mapperAbstract;

    @Autowired
    protected SDevService deviceTypeService;

    @Test
    void entityToDTO() {
        DeviceEntity entity = new DeviceEntity();

        entity.setId(1L);

        DeviceTypeEntity type = new DeviceTypeEntity();
        type.setId(4321L);
        type.setName("some_type_name");

        DeviceTypeGroupEntity group = new DeviceTypeGroupEntity();
        group.setId(5);
        group.setName("some_group_name");
        type.setGroup(group);
        entity.setType(type);

        entity.setNumber("2");
        entity.setReleaseYear("1990");

        entity.setTestDate(Date.valueOf("2001-11-11"));
        entity.setNextTestDate(Date.valueOf("2021-11-11"));

        entity.setReplacementPeriod(120);
        entity.setStatus(Status.PS11);
        entity.setDetail("some_detail_text");

        RtuObjectEntity object = new RtuObjectEntity();
        object.setId("3");
        object.setName("some_object_name");
        entity.setObject(object);

        DeviceLocationEntity location = new DeviceLocationEntity();
        location.setId(4L);
        location.setDescription("some_location_description");
        location.setRegion("some_region");
        location.setRegionType(RegionType.EC);
        location.setLocate("some_locate");
        location.setLocateType(LocateType.AS);
        location.setPlaceNumber("4A");
        location.setDetail("some_location_detail");
        entity.setLocation(location);

//===================================================================
        DeviceDTO dto = DeviceMapper.INSTANCE.entityToDTO(entity);
//===================================================================
        assertEquals(dto.getId(), entity.getId());

        assertEquals(dto.getTypeId(), entity.getType().getId());
        assertEquals(dto.getTypeName(), entity.getType().getName());

        assertEquals(dto.getTypeGroupId(), entity.getType().getGroup().getId());
        assertEquals(dto.getTypeGroupName(), entity.getType().getGroup().getName());

        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getReleaseYear(), entity.getReleaseYear());

        assertEquals(dto.getTestDate(), entity.getTestDate());
        assertEquals(dto.getNextTestDate(), entity.getNextTestDate());

        assertEquals(dto.getReplacementPeriod(), entity.getReplacementPeriod());
        assertEquals(dto.getStatusName(), entity.getStatus().getName());
        assertEquals(dto.getStatusComment(), entity.getStatus().getComm());
        assertEquals(dto.getDetail(), entity.getDetail());

        assertEquals(dto.getObjectId(), entity.getObject().getId());
        assertEquals(dto.getObjectName(), entity.getObject().getName());

        assertEquals(dto.getLocationId(), entity.getLocation().getId());
        assertEquals(dto.getDescription(), entity.getLocation().getDescription());
        assertEquals(dto.getRegion(), entity.getLocation().getRegion());
        assertEquals(dto.getRegionTypeName(), entity.getLocation().getRegionType().getName());
        assertEquals(dto.getRegionTypeComment(), entity.getLocation().getRegionType().getComm());
        assertEquals(dto.getLocate(), entity.getLocation().getLocate());
        assertEquals(dto.getLocateTypeName(), entity.getLocation().getLocateType().getName());
        assertEquals(dto.getLocateTypeComment(), entity.getLocation().getLocateType().getComm());
        assertEquals(dto.getPlaceNumber(), entity.getLocation().getPlaceNumber());
        assertEquals(dto.getLocationDetail(), entity.getLocation().getDetail());

    }

    @Test
    void entityToDTOAlt() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.scanClasspathForConcreteTypes(true);

        EasyRandom generator = new EasyRandom(parameters);
        DeviceEntity entity = generator.nextObject(DeviceEntity.class);

        ////===================================================================
        DeviceDTO dto = DeviceMapper.INSTANCE.entityToDTO(entity);
        ////===================================================================
        assertEquals(dto.getId(), entity.getId());

        assertEquals(dto.getTypeId(), entity.getType().getId());
        assertEquals(dto.getTypeName(), entity.getType().getName());

        assertEquals(dto.getTypeGroupId(), entity.getType().getGroup().getId());
        assertEquals(dto.getTypeGroupName(), entity.getType().getGroup().getName());

        assertEquals(dto.getNumber(), entity.getNumber());
        assertEquals(dto.getReleaseYear(), entity.getReleaseYear());

        assertEquals(dto.getTestDate(), entity.getTestDate());
        assertEquals(dto.getNextTestDate(), entity.getNextTestDate());

        assertEquals(dto.getReplacementPeriod(), entity.getReplacementPeriod());
        assertEquals(dto.getStatusName(), entity.getStatus().getName());
        assertEquals(dto.getStatusComment(), entity.getStatus().getComm());
        assertEquals(dto.getDetail(), entity.getDetail());

        assertEquals(dto.getObjectId(), entity.getObject().getId());
        assertEquals(dto.getObjectName(), entity.getObject().getName());

        assertEquals(dto.getLocationId(), entity.getLocation().getId());
        assertEquals(dto.getDescription(), entity.getLocation().getDescription());
        assertEquals(dto.getRegion(), entity.getLocation().getRegion());
        assertEquals(dto.getRegionTypeName(), entity.getLocation().getRegionType().getName());
        assertEquals(dto.getRegionTypeComment(), entity.getLocation().getRegionType().getComm());
        assertEquals(dto.getLocate(), entity.getLocation().getLocate());
        assertEquals(dto.getLocateTypeName(), entity.getLocation().getLocateType().getName());
        assertEquals(dto.getLocateTypeComment(), entity.getLocation().getLocateType().getComm());
        assertEquals(dto.getPlaceNumber(), entity.getLocation().getPlaceNumber());
        assertEquals(dto.getLocationDetail(), entity.getLocation().getDetail());
    }

    @Test
    void dTOToEntity() throws IOException {
//        EasyRandomParameters parameters = new EasyRandomParameters();
//        parameters.scanClasspathForConcreteTypes(true);
//
//        EasyRandom generator = new EasyRandom(parameters);
//        DeviceDTO dto = generator.nextObject(DeviceDTO.class);
//
//        //===================================================================
//        DeviceEntity entity = mapperAbstract.dTOToEntity(dto);
        //===================================================================

        // create object mapper instance
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//        mapper.registerSubtypes(RtuObjectEntity.class);

        Resource resource = new ClassPathResource("device_entity.json");
        InputStream is = resource.getInputStream();
//        mapper.writeValue(Paths.get("entity.json").toFile(), entity);
        // convert map to JSON file
//        mapper.writeValue(file, entity);
        DeviceEntity entity1 = mapper.readValue(is, DeviceEntity.class);

        System.out.println("1");
    }
}