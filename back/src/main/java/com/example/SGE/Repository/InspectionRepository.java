package com.example.SGE.Repository;

import com.example.SGE.Entity.InspectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionRepository extends JpaRepository<InspectionEntity, Long> {
}
