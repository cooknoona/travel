package com.travel.repository;

import com.travel.entity.UserEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventLogRepository extends JpaRepository<UserEventLog, Long> {
}
