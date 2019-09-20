package com.boot.webservice.inter;

import com.boot.webservice.entity.SysLog;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface SysLogService {

    @WebMethod
    String getIp(@WebParam(name = "id") String id);

    @WebMethod
    SysLog getLog(String id);

    @WebMethod
    List<SysLog> getLogAll();
}
