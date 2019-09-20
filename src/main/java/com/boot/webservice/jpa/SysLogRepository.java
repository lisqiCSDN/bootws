package com.boot.webservice.jpa;

import com.boot.webservice.entity.SysLog;
import org.springframework.stereotype.Repository;

/**
 * 操作日志
 */
@Repository
public interface SysLogRepository extends BaseRepository<SysLog,String> {
}
