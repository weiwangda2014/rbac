/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.wandun.common.persistence;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wandun.common.utils.Reflections;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据Entity类
 *
 * @author jeeplus
 * @version 2014-05-16
 */

@JsonIgnoreProperties(value = {"handler"})
public class TreeEntity<T extends TreeEntity<T, ID>, ID extends Serializable> extends DataEntity<T, ID> {


    private static final long serialVersionUID = 1L;

    private T parent;    // 父级编号
    private List<T> children = new ArrayList<>();//子节点
    private String path; // 所有父级编号
    private String name;    // 机构名称
    private Integer sort;        // 排序

    public TreeEntity() {
        super();
        this.sort = 30;
    }

    public TreeEntity(ID id) {
        super(id);
        this.sort = 30;
    }

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 是否存在子节点
     *
     * @return
     */
    public boolean getHasChildren() {
        if (CollectionUtils.isNotEmpty(this.children)) {
            return true;
        }
        return false;
    }

    public String getParentId() {
        String id = null;
        if (parent != null) {
            id = (String) Reflections.getFieldValue(parent, "id");
        }
        return StringUtils.isNotBlank(id) ? id : "0";
    }
}
