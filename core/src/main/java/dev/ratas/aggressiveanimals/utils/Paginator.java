package dev.ratas.aggressiveanimals.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

public class Paginator<T> {
    private final List<T> list;
    private final int page;
    private final int perPage;
    private final int pageStart;
    private final int pageEnd;

    public Paginator(List<T> list, int page, int perPage) {
        Validate.isTrue(page > 0, "Page needs to be positive");
        this.list = list;
        this.page = page;
        this.perPage = perPage;
        Validate.isTrue(validPage(), "Does not have page");
        Validate.isTrue(perPage > 0, "Need to have a positive number of items per page");
        pageStart = getPageStart(this.page, this.perPage);
        pageEnd = Math.min(getPageEnd(this.page, this.perPage), this.list.size());
    }

    private boolean validPage() {
        if (page == 1) {
            return true;
        }
        int pageStart = getPageStart(page, this.perPage);
        if (pageStart > this.list.size()) {
            return false;
        }
        return true;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getPageStart() {
        return pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public List<T> getOnPage() {
        List<T> list = new ArrayList<>();
        for (int i = pageStart; i < pageEnd; i++) {
            list.add(this.list.get(i));
        }
        return list;
    }

    @Override
    public String toString() {
        return String.format("[Paginator: page %d with %d on page of %d length array, current %d-%d]", page, perPage,
                this.list.size(), pageStart, pageEnd);
    }

    /**
     * Get page start (starting from 0). This method does not consider whether or
     * not the page exists.
     *
     * @param page
     * @param perPage
     * @return
     */
    public static int getPageStart(int page, int perPage) {
        return (page - 1) * perPage;
    }

    /**
     * Get the page end (exclusive, starting from 0). This method does not consider
     * whether or not the page exists.
     *
     * @param page
     * @param perPage
     * @return
     */
    public static int getPageEnd(int page, int perPage) {
        return getPageStart(page + 1, perPage);
    }

    public static boolean isValidPage(List<?> list, int page, int perPage) {
        try {
            new Paginator<>(list, page, perPage);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
