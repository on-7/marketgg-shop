package com.nhnacademy.marketgg.server.repository.pointhistory;

import com.nhnacademy.marketgg.server.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, PointHistoryRepositoryCustom {

}