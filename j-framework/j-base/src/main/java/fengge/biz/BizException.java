package fengge.biz;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author max.hu   @date 2025/3/6
 **/
public class BizException extends RuntimeException {
    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private Map<String, Object> data;

    public BizException(String code) {
        this.code = code;
    }
}
