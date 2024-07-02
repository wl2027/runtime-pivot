package com.runtime.pivot.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JSONFileTool {
    public static final String PATH = ".";
    public static String write(Object object,String path){
        if (StrUtil.isEmpty(path)) {
            path=PATH;
        }
        //String dateString = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String className = object.getClass().getName();
//        String filePath = path + File.separatorChar + className.replace('.', File.separatorChar) + "@"+dateString+"@"+System.identityHashCode(object)+".json";
        String filePath = path + File.separatorChar + className.replace('.', File.separatorChar) +"@"+System.identityHashCode(object)+".json";
        File touch = FileUtil.touch(filePath);
        FileUtil.writeString(JSONUtil.formatJsonStr(JSONUtil.toJsonStr(object)),touch, StandardCharsets.UTF_8);
        return touch.getPath();
    }
    public static <E> E readObject(String path,Class<E> eClass){
        FileReader fileReader = new FileReader(path);
        String jsonString = fileReader.readString();
        return JSONUtil.toBean(jsonString,eClass);
    }
    public static <E> List<E> readList(String path,Class<E> eClass){
        FileReader fileReader = new FileReader(path);
        String jsonString = fileReader.readString();
        return JSONUtil.toList(jsonString,eClass);
    }

    public static void main(String[] args) {
        String write = write(new ArrayList<String>(){{add("aaa");}}, "");
        List read = readList(write, List.class);
        System.out.println(read);
    }

}
