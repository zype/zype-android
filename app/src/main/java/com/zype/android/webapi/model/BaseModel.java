package com.zype.android.webapi.model;

import java.util.List;
import java.util.Map;

public class BaseModel {

//    private static final String SUCCESS = "success";z
//    public static final String OK = "OK";

//    private String status;

    private Map<String, List<String>> message;

    private int code;

    /**
     * @return The status
     */
//    public String getStatus() {
//        return status;
//    }

    /**
     * @param status The status
     */
//    public void setStatus(String status) {
//        this.status = status;
//    }

    /**
     * @return The message
     */
    public String getMessage() {
        String errorMsg = "";
        if (message != null ){
            for (Map.Entry<String, List<String>> entry : message.entrySet()) {
                List<String> list = entry.getValue();
                if (list == null || list.isEmpty()) {
                    continue;
                }
                // get first error
                errorMsg  = list.get(0);
                break;
            }
        }
        return errorMsg;
    }

    /**
     * @param message The message
     */
    public void setMessage(Map<String, List<String>> message) {
        this.message = message;
    }

//    public boolean isSuccess() {
//        return TextUtils.equals(SUCCESS, status) || TextUtils.equals(OK, status);
//    }

//    public ErrorCode getErrorCode() {
//        return ErrorCode.fromInt(code);
//    }
}
