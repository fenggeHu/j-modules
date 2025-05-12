package fengge.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Description: 简单的计算分页
 * @author max.hu  @date 2021-11-01
 **/
public class Pager implements Serializable {
    private static final long serialVersionUID = 1L;
    @Getter
    @Setter
//    @ApiModelProperty("当前页码")
    private int current;
    @Setter
    @Getter
//    @ApiModelProperty("pageSize")
    private int pageSize;
    @Getter
//    @ApiModelProperty(value = "起始行", hidden = true)
    private int startRow;
    @Getter
//    @ApiModelProperty(value = "结束行", hidden = true)
    private int endRow;

    public Pager(int current, int pageSize) {
        this.current = current;
        this.pageSize = pageSize;
        this.calculateStartAndEndRow();
    }

    public void calculateStartAndEndRow() {
        this.startRow = this.current > 0 ? (this.current - 1) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.current > 0 ? 1 : 0);
    }

    public static Pager defaultPager() {
        return new Pager(1, 20);
    }

    public static Pager getPager(int currentPage, int pageSize) {
        return new Pager(currentPage, pageSize);
    }

}
