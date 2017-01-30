package com.zype.android.webapi.model.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vasya
 * @version 1
 *          date 6/30/15
 */
public class CategoryData {

    @SerializedName("_id")
    @Expose
    private String Id;
    @SerializedName("_keywords")
    @Expose
    private List<String> Keywords = new ArrayList<>();
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("site_id")
    @Expose
    private String siteId;
    @Expose
    private String title;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private List<String> values = new ArrayList<>();

    /**
     *
     * @return
     * The Id
     */
    public String getId() {
        return Id;
    }

    /**
     *
     * @param Id
     * The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     *
     * @return
     * The Keywords
     */
    public List<String> getKeywords() {
        return Keywords;
    }

    /**
     *
     * @param Keywords
     * The _keywords
     */
    public void setKeywords(List<String> Keywords) {
        this.Keywords = Keywords;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The deletedAt
     */
    public String getDeletedAt() {
        return deletedAt;
    }

    /**
     *
     * @param deletedAt
     * The deleted_at
     */
    public void setDeletedAt(String  deletedAt) {
        this.deletedAt = deletedAt;
    }

    /**
     *
     * @return
     * The siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     *
     * @param siteId
     * The site_id
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     *
     * @param values
     * The values
     */
    public void setValues(List<String> values) {
        this.values = values;
    }
}
