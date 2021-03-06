package com.goldenideabox.patents.dao;


import com.goldenideabox.patents.model.Document;
import java.util.List;

public interface DocumentMapper {


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table document
     *
     * @mbg.generated
     */
    int insert(Document record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table document
     *
     * @mbg.generated
     */
    Document selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table document
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Document record);


    List<Document> getAllDocument();

    int updateParseStatusToAll(Integer state);

    List<Document> getUnParseDocuments();
}