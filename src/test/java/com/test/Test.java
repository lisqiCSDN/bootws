package com.test;

import com.alibaba.fastjson.JSON;
import com.boot.webservice.utils.CallWebserviceUtil;
import com.google.gson.Gson;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.*;

import javax.xml.namespace.QName;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Test
 * @Date: 2019/9/20
 * @describe:
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        JaxWsDynamicClientFactory dcflient=JaxWsDynamicClientFactory.newInstance();
//
//        Client client=dcflient.createClient("http://127.0.0.1:9000/ws/sysLogService?wsdl");
////        //处理webService接口和实现类namespace不同的情况，CXF动态客户端在处理此问题时，会报No operation was found with the name的异常
//        Endpoint endpoint = client.getEndpoint();
//        QName opName = new QName(endpoint.getService().getName().getNamespaceURI(),"getLog");
//        BindingInfo bindingInfo= endpoint.getEndpointInfo().getBinding();
//        if(bindingInfo.getOperation(opName) == null) {
//            for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {
//                if ("getLog".equals(operationInfo.getName().getLocalPart())) {
//                    opName = operationInfo.getName();
//                    break;
//                }
//            }
//        }
//
//        HTTPConduit conduit = (HTTPConduit)client.getConduit();
//        HTTPClientPolicy policy = new HTTPClientPolicy();
//        policy.setConnectionTimeout(100000);//设置超时时间
//        policy.setReceiveTimeout(100000);//设置超时时间
//        conduit.setClient(policy);
//
//        Object[] objects=client.invoke(opName,"44541188771938304");
//        System.out.println("*******"+new Gson().toJson(objects[0]));

//        objects=client.invoke("getLog","44541188771938304");
//        System.out.println("*******"+objects[0].toString());
//
//        objects=client.invoke("getLogAll");
//        System.out.println("*******"+objects[0].toString());
//        dynamicCallWebServiceByCXF("http://127.0.0.1:9000/ws/sysLogService?wsdl","getLog","http://Impl.inter.webservice.boot.com/","SysLogServiceImpl",Arrays.asList("44541188771938304"));
//        pojoInvokes();
        Object[] objects = CallWebserviceUtil.clientInvokeWebService("http://127.0.0.1:9000/ws/sysLogService?wsdl", "getLog", true, "44541188771938304");
        System.out.println("-----------" +new Gson().toJson(objects));
    }

    static Map<String,Endpoint> factoryMap = new HashMap<>();
    static Map<String,Client> clientMap = new HashMap<>();
    /**
     *
     * @param wsdlUrl  wsdl的地址：http://localhost:8001/demo/HelloServiceDemoUrl?wsdl
     * @param methodName 调用的方法名称 selectOrderInfo
     * @param targetNamespace 命名空间 http://service.limp.com/
     * @param name  name HelloServiceDemo
     * @param paramList 参数集合
     * @throws Exception
     */
    public  static String dynamicCallWebServiceByCXF(String wsdlUrl,String methodName,String targetNamespace,String name,List<Object> paramList)throws Exception {
        //临时增加缓存，增加创建速度
        if (!factoryMap.containsKey(methodName)) {
            // 创建动态客户端
            JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
            // 创建客户端连接
            Client client = factory.createClient(wsdlUrl);
            ClientImpl clientImpl = (ClientImpl) client;
            Endpoint endpoint = clientImpl.getEndpoint();
            factoryMap.put(methodName, endpoint);
            clientMap.put(methodName, client);
            System.out.println("初始化");
        }
        //从缓存中换取 endpoint、client
        Endpoint endpoint = factoryMap.get(methodName);
        Client client = clientMap.get(methodName);
        // Make use of CXF service model to introspect the existing WSDL
        ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);
        // 创建QName来指定NameSpace和要调用的service
        String localPart = name + "SoapBinding";
        QName bindingName = new QName(targetNamespace, localPart);
        BindingInfo binding = serviceInfo.getBinding(bindingName);

        //创建QName来指定NameSpace和要调用的方法绑定方法
        QName opName = new QName(targetNamespace, methodName);//selectOrderInfo

        BindingOperationInfo boi = binding.getOperation(opName);
//		BindingMessageInfo inputMessageInfo = boi.getInput();
        BindingMessageInfo inputMessageInfo = null;
        if (!boi.isUnwrapped()) {
            //OrderProcess uses document literal wrapped style.
            inputMessageInfo = boi.getWrappedOperation().getInput();
        } else {
            inputMessageInfo = boi.getUnwrappedOperation().getInput();
        }

        List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();

        /***********************以下是初始化参数，组装参数；处理返回结果的过程******************************************/
        Object[] parameters = new Object[parts.size()];
        for (int m = 0; m < parts.size(); m++) {
            MessagePartInfo part = parts.get(m);
            // 取得对象实例
            Class<?> partClass = part.getTypeClass();//OrderInfo.class;
            System.out.println(partClass.getCanonicalName()); // GetAgentDetails
            //实例化对象
            Object initDomain = null;
            //普通参数的形参，不需要fastJson转换直接赋值即可
            if ("java.lang.String".equalsIgnoreCase(partClass.getCanonicalName())
                    || "int".equalsIgnoreCase(partClass.getCanonicalName())) {
                initDomain = paramList.get(m).toString();
            }
            //如果是数组
            else if (partClass.getCanonicalName().indexOf("[]") > -1) {
                //转换数组
                initDomain = JSON.parseArray(paramList.get(m).toString(), partClass.getComponentType());
            } else {
                initDomain = JSON.parseObject(paramList.get(m).toString(), partClass);
            }
            parameters[m] = initDomain;

        }
        //定义返回结果集
        Object[] result = null;
        //普通参数情况 || 对象参数情况  1个参数 ||ArryList集合
        try {
            result = client.invoke(opName, parameters);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "参数异常" + ex.getMessage();
        }
        //返回调用结果
        if (result.length > 0) {
            return JSON.toJSON(result[0]).toString();
        }
        return "invoke success, but is void ";
    }

    private static  String   wsdlUrl="http://127.0.0.1:9000/ws/sysLogService?wsdl";
    //	private static final QName SERVICE_NAME = new QName("namespace", "serviceName");
    private static final QName SERVICE_NAME = new QName("http://inter.webservice.boot.com/", "SysLogService");

    /**
     * 网上流传的方法【初始化复杂，后续有改进版】
     * @throws Exception
     */
    public  static void pojoInvokes()throws Exception {
        // 创建动态客户端
        JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
        // 创建客户端连接
        Client client = factory.createClient(wsdlUrl, SERVICE_NAME);
        ClientImpl clientImpl = (ClientImpl) client;
        Endpoint endpoint = clientImpl.getEndpoint();
        // Make use of CXF service model to introspect the existing WSDL
        ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);
        // 创建QName来指定NameSpace和要调用的service
        QName bindingName = new QName("http://inter.webservice.boot.com/", "SysLogServiceImpl");
        BindingInfo binding = serviceInfo.getBinding(bindingName);

        //todo:??????????    // 创建QName来指定NameSpace和要调用的方法
        QName opName = new QName("http://inter.webservice.boot.com/", "getLog");

        BindingOperationInfo boi = binding.getOperation(opName);
        BindingMessageInfo inputMessageInfo = boi.getInput();
        List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();
        // 取得对象实例
        MessagePartInfo partInfo = parts.get(0);
        Class<?> partClass = partInfo.getTypeClass();
        Object inputObject = partClass.newInstance();

        // 取得字段的set方法并赋值
        PropertyDescriptor partPropertyDescriptor = new PropertyDescriptor("id", partClass);
        Method userNoSetter = partPropertyDescriptor.getWriteMethod();
        userNoSetter.invoke(inputObject, "44541188771938304");

        // 调用客户端invoke()方法，把inputObject传递给要调用的方法并取得结果对象
        Object[] result = client.invoke(opName, inputObject);
        // 取得的结果是一个对象
        Class<?> resultClass = result[0].getClass();
        // 取得返回结果的get方法并得到它的值
        PropertyDescriptor resultDescriptor = new PropertyDescriptor("id", resultClass);
        Object resultGetter = resultDescriptor.getReadMethod().invoke(result[0]);
        System.out.println("result：" + resultGetter);
        // 取得返回结果的get方法并得到它的值
        PropertyDescriptor tokenDescriptor = new PropertyDescriptor("id", resultClass);
        // 取得的是一个对象实例
        Object getObj = tokenDescriptor.getReadMethod().invoke(result[0]);
        if ("tokenGetter " != null) {
            Class<?> resultTokenClass = tokenDescriptor.getReadMethod().invoke(result[0]).getClass();
            // 得到对象实例下的***属性值
            PropertyDescriptor expiredTimeDescriptor = new PropertyDescriptor("id", resultTokenClass);
            Object getter = expiredTimeDescriptor.getReadMethod().invoke(getObj);
            System.out.println("字段名：" + getter);
        }
    }
}
