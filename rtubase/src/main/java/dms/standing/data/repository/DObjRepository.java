package dms.standing.data.repository;


import dms.standing.data.entity.LineObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DObjRepository extends JpaRepository<LineObjectEntity, String> {
}
