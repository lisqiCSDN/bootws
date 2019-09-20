package com.boot.webservice.utils;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import javax.xml.namespace.QName;

/**
 * @ClassName: CallWebserviceUtil
 * @Date: 2019/9/20
 * @describe:
 */
public class CallWebserviceUtil {

    private static JaxWsDynamicClientFactory clientFactory;
    private static Client client;

    /**
　　  * webservice客户端调用方法
　　  * @param wsdlURL 服务提供地址
　　  * @param operationName 方法名
　　  * @param flag true=处理webService接口和实现类namespace不同的情况，CXF动态客户端在处理此问题时，会报No operation was found with the name的异常
　　  * @param params 参数值
　　  * @return: java.lang.String
　　  * @create: 2019/6/27 13:15
　　  * @version: 1.0
　　  */
    public static Object[] clientInvokeWebService(String wsdlURL, String operationName,boolean flag, Object... params)  throws Exception {
        if (clientFactory==null){
            clientFactory = JaxWsDynamicClientFactory.newInstance();
            client = clientFactory.createClient(wsdlURL);
            HTTPConduit conduit = (HTTPConduit)client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(100000);//设置超时时间
            policy.setReceiveTimeout(100000);//设置超时时间
            conduit.setClient(policy);
        }
        Object[] objects;
        if (flag){
            Endpoint endpoint = client.getEndpoint();
            QName opName = new QName(endpoint.getService().getName().getNamespaceURI(),operationName);
            BindingInfo bindingInfo= endpoint.getEndpointInfo().getBinding();
            if(bindingInfo.getOperation(opName) == null) {
                for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {
                    if (operationName.equals(operationInfo.getName().getLocalPart())) {
                        opName = operationInfo.getName();
                        break;
                    }
                }
            }
            objects = client.invoke(opName, params);
        }else {
            objects = client.invoke(operationName, params);
        }
        return objects;
    }

//    public static Object[] clientInvokeWebService(String wsdlURL, String operationName, Object... params)  throws Exception {
//        return clientInvokeWebService(wsdlURL,operationName,false,params);
//    }
}
