package dev.ratas.aggressiveanimals.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.Validate;

public class Paginator<T> implements Iterable<T> {
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

    /**
     * Gets the page described by this paginator.
     *
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * Checks if the page is partial. If the page is partial, fewer items are shown
     * on it than is described by getPerPage.
     *
     * @return true if the page is partial, false otherwise
     */
    public boolean isPartial() {
        return getPageEnd() != getPageEnd(page, perPage);
    }

    /**
     * Gets the number of items shown on the page in this paginator. This may be
     * different than the number of items on getOnPage() for the last page if teh
     * page is incomplete (the list does not exactly divide into the number of items
     * per page).
     *
     * @return the number of items on a page
     */
    public int getPerPage() {
        return perPage;
    }

    /**
     * Gets the raw (starting from 0) page start index.
     *
     * @return the page start index
     */
    public int getPageStart() {
        return pageStart;
    }

    /**
     * Gets the raw (excluded) page end index.
     *
     * @return page end index
     */
    public int getPageEnd() {
        return pageEnd;
    }

    /**
     * Gets the list of items on this page. This is calculated on request.
     *
     * @return list of items on the page
     */
    public List<T> getOnPage() {
        List<T> list = new ArrayList<>();
        for (int i = pageStart; i < pageEnd; i++) {
            list.add(this.list.get(i));
        }
        return list;
    }

    @Override
    public Iterator<T> iterator() {
        return getOnPage().iterator();
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

    /**
     * Checks if page is valid for a given list (of any type). A page is not valid
     * if there are not enough items in the list for the first item to be shown on
     * that page. The only exception is page 1 wchich will always be valid unless
     * the number of items per page is invalid (non-positive).
     * 
     * @param list    the list of items
     * @param page    the page in question
     * @param perPage number of items per page
     * @return true if page exists, false otherwise
     */
    public static boolean isValidPage(List<?> list, int page, int perPage) {
        try {
            new Paginator<>(list, page, perPage);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Paginates the given page of the given list by doing the work as specified by
     * the worker.
     *
     * If the intput (page, per page) is incorrect, an exception is thrown.
     *
     * @param <T>     the type of items in the list
     * @param list    the list of items to paginate
     * @param page    the page to work on
     * @param perPage number of items per page
     * @param worker  the worker for items on the page
     * @throws IllegalArgumentException if page and/or perPage is incorrect
     */
    public static <T> void paginate(List<T> list, int page, int perPage, Consumer<T> worker) {
        Paginator<T> paginator = new Paginator<>(list, page, perPage);
        for (T t : paginator.getOnPage()) {
            worker.accept(t);
        }
    }

}
