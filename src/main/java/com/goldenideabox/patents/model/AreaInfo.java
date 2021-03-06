package com.goldenideabox.patents.model;

import java.io.Serializable;

public class AreaInfo implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column area_info.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column area_info.pid
     *
     * @mbg.generated
     */
    private Integer pid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column area_info.deep
     *
     * @mbg.generated
     */
    private Integer deep;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column area_info.ext_name
     *
     * @mbg.generated
     */
    private String extName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table area_info
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column area_info.id
     *
     * @return the value of area_info.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column area_info.id
     *
     * @param id the value for area_info.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column area_info.pid
     *
     * @return the value of area_info.pid
     *
     * @mbg.generated
     */
    public Integer getPid() {
        return pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column area_info.pid
     *
     * @param pid the value for area_info.pid
     *
     * @mbg.generated
     */
    public void setPid(Integer pid) {
        this.pid = pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column area_info.deep
     *
     * @return the value of area_info.deep
     *
     * @mbg.generated
     */
    public Integer getDeep() {
        return deep;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column area_info.deep
     *
     * @param deep the value for area_info.deep
     *
     * @mbg.generated
     */
    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column area_info.ext_name
     *
     * @return the value of area_info.ext_name
     *
     * @mbg.generated
     */
    public String getExtName() {
        return extName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column area_info.ext_name
     *
     * @param extName the value for area_info.ext_name
     *
     * @mbg.generated
     */
    public void setExtName(String extName) {
        this.extName = extName == null ? null : extName.trim();
    }
}