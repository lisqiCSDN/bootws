package com.boot.webservice.inter.Impl;

import com.boot.webservice.entity.SysLog;
import com.boot.webservice.inter.SysLogService;
import com.boot.webservice.jpa.SysLogRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: SysLogServiceImpl
 * @Date: 2019/9/20
 * @describe:
 */
@Slf4j
@WebService(targetNamespace = "Impl.inter.webservice.boot.com"
        ,endpointInterface = "com.boot.webservice.inter.SysLogService")
public class SysLogServiceImpl implements SysLogService {

    @Autowired private SysLogRepository sysLogRepository;
    @Override
    public String getIp(String id) {
        Optional<SysLog> optionalLog = sysLogRepository.findById(id);
        SysLog sysLog = optionalLog.get();
        log.info("---",new Gson().toJson(sysLog));
        return sysLog.getIp();
    }

    @Override
    public SysLog getLog(String id) {
        return sysLogRepository.findById(id).get();
    }

    @Override
    public List<SysLog> getLogAll() {
        return sysLogRepository.findAll();
    }
}
