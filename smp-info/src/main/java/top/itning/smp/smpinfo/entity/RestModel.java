package top.itning.smp.smpinfo.entity;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Rest 返回消息
 *
 * @author itning
 */
public class RestModel<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    private RestModel(HttpStatus status, String msg, T data) {
        this(status.value(), msg, data);
    }

    private RestModel(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> RestModel ok(T data) {
        return new RestModel<>(HttpStatus.OK, "查询成功", data);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
