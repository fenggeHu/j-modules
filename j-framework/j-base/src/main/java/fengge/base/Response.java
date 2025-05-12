package fengge.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: api返回统一格式 - 与前端交互的结构
 * 对于普通查询返回格式，查询采用offset+limit格式、返回采用data: [] 格式
 *
 * @author max.hu  @date 2022/8/9
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    /**
     * 业务自己状态码，“0”表示业务成功、其余表示失败
     */
    private String code;
    /**
     * 请求响应体，可以是对象、数组、以及空
     */
    private T data;
    /**
     * 对当前请求结果的描述信息，可为空串
     */
    private String msg;

    // 构造对象
    public static <T> Response success(T data) {
        return Response.builder().code("0").data(data).build();
    }

    public static <T> Response of(T data, String code, String msg) {
        return Response.builder().code(code).data(data).msg(msg).build();
    }

    public static Response of(String code) {
        return Response.builder().code(code).build();
    }

    public static Response of(String code, String msg) {
        return Response.builder().code(code).msg(msg).build();
    }
}
