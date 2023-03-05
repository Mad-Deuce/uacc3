package dms.standing.data.repository;

import dms.standing.data.entity.SubdivisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubdivisionRepository extends JpaRepository<SubdivisionEntity, Long> {
    List<SubdivisionEntity> findAllByIdStartingWithOrderById(String parentId);
}
