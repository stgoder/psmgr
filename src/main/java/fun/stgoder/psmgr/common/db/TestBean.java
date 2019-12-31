package fun.stgoder.psmgr.common.db;

import fun.stgoder.psmgr.common.db.annotation.col;
import fun.stgoder.psmgr.common.db.annotation.tbl;

import java.util.Date;

@tbl("test_bean")
public class TestBean {
    @col(pk = true, nn = true, len = 24)
    private String id;
    @col(nn = true)
    private String name;
    @col(len = 255)
    private String text;
    @col(nn = true)
    private boolean del;
    @col(value = "create_time", nn = true)
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
