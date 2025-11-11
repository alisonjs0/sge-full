package com.example.SGE.Repository;

import com.example.SGE.Entity.ExtinguisherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtinguisherRepository extends JpaRepository<ExtinguisherEntity, Long> {
}
