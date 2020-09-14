package com.mt.system.websocket.u3d;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MtU3dMsgContainerUtil {

    //记录连接对象(公司-连接对象）
    private static ConcurrentHashMap<String, Session> mtU3dMsgSessionMap=new ConcurrentHashMap();
    //类型终端，PC、APP、H5
    private static List<String> mtU3dMsgTypeList= new ArrayList<String>(){{
        add("PC");
        add("APP");
        add("H5");
    }};

    /*--静态get函数--*/
    public static ConcurrentHashMap<String,Session> getMtU3dMsgSessionMap(){
        return mtU3dMsgSessionMap;
    }
    public static List<String> getMtU3dMsgTypeList(){
        return mtU3dMsgTypeList;
    }
}
