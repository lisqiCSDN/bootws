//package com.boot.webservice.utils;
//
//import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
//
//import java.lang.reflect.Method;
//
///**
// * @ClassName: WsUtil
// * @Date: 2019/9/20
// * @describe:
// */
//public class WsUtil {
//
//    private static String wsUrl;
//    private static Class<?> interfaceClz;
//
//    public static void init(String wsUrl, Class<?> interfaceClz) {
//        WsUtil.wsUrl = wsUrl;
//        WsUtil.interfaceClz = interfaceClz;
//    }
//
//    public static String invoke(String methodName, Object... params) throws Exception {
//        Object obj = getWsObject();
//        Class<?>[] argsTypes = new Class[params.length];
//        for (int i = 0; i < params.length; i ++) {
//            if (params[i] != null) {
//                argsTypes[i] = params[i].getClass();
//            }
//        }
//
//        Method method = interfaceClz.getMethod(methodName, argsType);
//        String result = (String)method.invoke(obj, params);
//        return result;
//    }
//
//
//    private static Object getWsObject() {
//        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
//        factory.setAddress(wsUrl);
//        factory.setServiceClass(interfaceClz);
//        Object obj = (Object)factory.create();
//        return obj;
//    }
//}
