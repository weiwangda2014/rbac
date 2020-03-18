/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.entity;

import com.wandun.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 区域Entity
 *
 * @author WanDun
 * @version 2013-05-15
 */
public class Area extends TreeEntity<Area,String> {

    private static final long serialVersionUID = 1L;
    private String code;    // 区域编码
    private String type;    // 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）

    public Area() {
        super();
    }

    public Area(String id) {
        super(id);
    }

    @Length(min = 1, max = 1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}