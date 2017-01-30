package com.zype.android.webapi.model.video;

/**
 * @author vasya
 * @version 1
 * date 6/29/15
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @Expose
    private Integer current;
    @Expose
    private Integer previous;
    @Expose
    private Integer next;
    @SerializedName("per_page")
    @Expose
    private Integer perPage;
    @Expose
    private Integer pages;

    /**
     *
     * @return
     * The current
     */
    public Integer getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     * The current
     */
    public void setCurrent(Integer current) {
        this.current = current;
    }

    /**
     *
     * @return
     * The previous
     */
    public Integer getPrevious() {
        return previous;
    }

    /**
     *
     * @param previous
     * The previous
     */
    public void setPrevious(Integer previous) {
        this.previous = previous;
    }

    /**
     *
     * @return
     * The next
     */
    public Integer getNext() {
        return next;
    }

    /**
     *
     * @param next
     * The next
     */
    public void setNext(Integer next) {
        this.next = next;
    }

    /**
     *
     * @return
     * The perPage
     */
    public Integer getPerPage() {
        return perPage;
    }

    /**
     *
     * @param perPage
     * The per_page
     */
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    /**
     *
     * @return
     * The pages
     */
    public Integer getPages() {
        return pages;
    }

    /**
     *
     * @param pages
     * The pages
     */
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public static boolean hasNextPage(Pagination pagination) {

        if (pagination.getNext() == null) {
            return false;
        }
        int nextPage = pagination.getNext();
        int currentPage = pagination.getCurrent();
        return nextPage > currentPage;

    }

    public static int getNextPage(Pagination pagination) {
        return pagination.getNext();
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "current=" + current +
                ", previous=" + previous +
                ", next=" + next +
                ", perPage=" + perPage +
                ", pages=" + pages +
                '}';
    }
}