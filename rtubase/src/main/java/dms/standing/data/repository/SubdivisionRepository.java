package dms.standing.data.repository;

import dms.standing.data.entity.SubdivisionEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Map;

public interface SubdivisionRepository extends JpaRepository<SubdivisionEntity, String> {

    List<SubdivisionEntity> findAllByIdStartingWithOrderById(String parentId);

    List<SubdivisionEntity> findByIdIn(List<String> idList);

}
