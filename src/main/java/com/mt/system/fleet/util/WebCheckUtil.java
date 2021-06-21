package com.mt.system.fleet.util;

import java.util.List;

public class WebCheckUtil {

    public static boolean illParams(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                return true;
            }
            if (o instanceof String && "".equals(((String)o).trim())) {
                return true;
            }
            if (o instanceof Integer && (int)o < 1) {
                return true;
            }
            if (o instanceof Long && (long)o < 1) {
                return true;
            }
            if (o instanceof List && ((List)o).isEmpty()) {
                return true;
            }
            // if (o instanceof MultipartFile && ((MultipartFile) o).isEmpty()) {
            // return true;
            // }
        }
        return false;
    }

}
