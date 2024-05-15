package com.runtime.pivot.agent;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JSONFileUtils {
    public static final String PATH = "D:\\temp";
    public static String write(Object object,String path){
        if (StrUtil.isEmpty(path)) {
            path=PATH;
        }
        String id = IdUtil.fastSimpleUUID();
        String rs = path + FileUtil.FILE_SEPARATOR + id + ".json";
        File touch = FileUtil.touch(rs);
        FileUtil.writeString(JSONUtil.toJsonStr(object),touch, StandardCharsets.UTF_8);
        return rs;
    }
    public static <E> E read(String path,Class<E> eClass){
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
