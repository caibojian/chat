package com.cai.chat_05.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/** 
 * Java对象和JSON字符串相互转化工具类 
 * @author penghuaiyi 
 * @date 2013-08-10 
 */  
public final class JsonUtil {  
      
    private JsonUtil(){}  
      
    /** 
     * 对象转换成json字符串 
     * @param obj  
     * @return  
     */  
    public static String toJson(Object obj) {  
        Gson gson = new Gson();  
        return gson.toJson(obj);  
    }  
  
    /** 
     * json字符串转成对象 
     * @param str   
     * @param type 
     * @return  
     */  
    public static <T> T fromJson(String str, Type type) {  
        Gson gson = new Gson();  
        return gson.fromJson(str, type);  
    }  
  
    /** 
     * json字符串转成对象 
     * @param str   
     * @param type  
     * @return  
     */  
    public static <T> T fromJson(String str, Class<T> type) {  
        Gson gson = new Gson();  
        return gson.fromJson(str, type);  
    }

    public static String changJson(String str){
        String str1 = null;
        if(str!=null){
            String part1 = str.substring(1, str.lastIndexOf("{"));
            part1 = part1.replaceAll("\\\\","");
            String part2 = str.substring(str.lastIndexOf("{"), str.indexOf("}"));
            part2 = part2.replaceAll("\\\\\\\\","");
            String part3 = str.substring(str.indexOf("}"), str.length()-1);
            part3 = part3.replaceAll("\\\\","");
            str1 = part1+part2+part3;
        }
        return str1;
    }
  
} 