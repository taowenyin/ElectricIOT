package siso.edu.cn.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;

public class ResultEntity {

    public static final int SUCCESS = 0;
    public static final int SAVE_DATA_ERROR = 1;
    public static final int ACCOUNT_ERROR = 2;
    public static final int NOT_FIND_ERROR = 3;
    public static final int DELETE_ERROR = 4;
    public static final int DATA_IS_EXIST = 5;

    @JsonProperty("code")
    private int code = 0;
    @JsonProperty("msg")
    private String msg = "";
    @JsonProperty("data")
    private JsonNode data = null;


    public ResultEntity(int code, String msg, JsonNode data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultEntity() {

    }

    /**
     * 根据错误代码获取消息内容
     * @param code 错误代码
     * @return String 错误信息
     */
    public String getCodeMsg(int code) {
        String msg = StringUtils.EMPTY;

        switch (code) {
            case SUCCESS:
                msg = "操作成功";
                break;
            case SAVE_DATA_ERROR:
                msg = "保存数据错误";
                break;
            case ACCOUNT_ERROR:
                msg = "登录错误";
                break;
            case NOT_FIND_ERROR:
                msg = "没有找到数据";
                break;
            case DELETE_ERROR:
                msg = "删除数据错误";
                break;
            case DATA_IS_EXIST:
                msg = "数据已经存在";
                break;
        }

        return msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
