package com.goldenideabox.patents.dao;


import com.goldenideabox.patents.model.GooglePatent;
import java.util.List;

public interface GooglePatentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table google_patent
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table google_patent
     *
     * @mbg.generated
     */
    int insert(GooglePatent record);



    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table google_patent
     *
     * @mbg.generated
     */
    GooglePatent selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table google_patent
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(GooglePatent record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table google_patent
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(GooglePatent record);

    List<GooglePatent> getGooglePatentByQueryID(Integer id);
}