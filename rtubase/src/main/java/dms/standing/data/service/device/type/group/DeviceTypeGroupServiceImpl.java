package dms.standing.data.service.device.type.group;

import dms.standing.data.entity.DeviceTypeGroupEntity;
import dms.standing.data.repository.DeviceTypeGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceTypeGroupServiceImpl implements DeviceTypeGroupService {
    private final DeviceTypeGroupRepository groupRepository;

    @Autowired
    public DeviceTypeGroupServiceImpl(DeviceTypeGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Optional<DeviceTypeGroupEntity> findGroupByID(Integer id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<DeviceTypeGroupEntity> findAllGroups() {
        return groupRepository.findAll();
    }
}