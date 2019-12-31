package fun.stgoder.psmgr.common.db;

import java.util.List;

public class Page<T> {

    private long total;

    private List<T> datas;

    private int pageSize;

    private int page;

    private int pages;

    public Page() {
    }

    public Page(int page, int pageSize, long total, List<T> datas) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.datas = datas;

        int ttl = new Long(total).intValue();

        this.pages = (ttl % pageSize == 0) ? ttl / pageSize : ttl / pageSize + 1;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "page: " + page + ", pageSize: " + pageSize + ", pages: " + pages + ", total: " + total;
    }

}
