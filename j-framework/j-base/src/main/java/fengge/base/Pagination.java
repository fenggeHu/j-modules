package fengge.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 分页返回的data对象
 * 如果存在分页业务需求，分页返回格式采用如下格式返回
 * @author max.hu  @date 2022/8/9
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination<T> {
    /**
     * 数据总量
     */
    private long total;
    /**
     * 当前页码
     */
    private int current;
    /**
     * 每页数据条数
     */
    private int pageSize;
    /**
     * 当前页的数据
     */
    private List<T> list;

    /**
     * 计算pageCount
     */
    public long getPageCount() {
        if (total <= 0 || pageSize <= 0) return 0;
        return total / pageSize + 1;
    }

    // 构造对象
    public static Pagination of() {
        return Pagination.builder().build();
    }

    public static Pagination of(long total, int current, int pageSize, List list) {
        return Pagination.builder().total(total).current(current).pageSize(pageSize).list(list).build();
    }
}
