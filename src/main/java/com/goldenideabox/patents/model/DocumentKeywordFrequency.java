package com.goldenideabox.patents.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DocumentKeywordFrequency implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.documentId
     *
     * @mbg.generated
     */
    private Integer documentid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.keyword
     *
     * @mbg.generated
     */
    private String keyword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.num
     *
     * @mbg.generated
     */
    private Integer num;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.firstLine
     *
     * @mbg.generated
     */
    private Integer firstline;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.tf
     *
     * @mbg.generated
     */
    private double tf;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.idf
     *
     * @mbg.generated
     */
    private double idf;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column document_keyword_frequency.tfidf
     *
     * @mbg.generated
     */
    private double tfidf;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table document_keyword_frequency
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.id
     *
     * @return the value of document_keyword_frequency.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.id
     *
     * @param id the value for document_keyword_frequency.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.documentId
     *
     * @return the value of document_keyword_frequency.documentId
     *
     * @mbg.generated
     */
    public Integer getDocumentid() {
        return documentid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.documentId
     *
     * @param documentid the value for document_keyword_frequency.documentId
     *
     * @mbg.generated
     */
    public void setDocumentid(Integer documentid) {
        this.documentid = documentid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.keyword
     *
     * @return the value of document_keyword_frequency.keyword
     *
     * @mbg.generated
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.keyword
     *
     * @param keyword the value for document_keyword_frequency.keyword
     *
     * @mbg.generated
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.num
     *
     * @return the value of document_keyword_frequency.num
     *
     * @mbg.generated
     */
    public Integer getNum() {
        return num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.num
     *
     * @param num the value for document_keyword_frequency.num
     *
     * @mbg.generated
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.firstLine
     *
     * @return the value of document_keyword_frequency.firstLine
     *
     * @mbg.generated
     */
    public Integer getFirstline() {
        return firstline;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.firstLine
     *
     * @param firstline the value for document_keyword_frequency.firstLine
     *
     * @mbg.generated
     */
    public void setFirstline(Integer firstline) {
        this.firstline = firstline;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.tf
     *
     * @return the value of document_keyword_frequency.tf
     *
     * @mbg.generated
     */
    public double getTf() {
        return tf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.tf
     *
     * @param tf the value for document_keyword_frequency.tf
     *
     * @mbg.generated
     */
    public void setTf(double tf) {
        this.tf = tf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.idf
     *
     * @return the value of document_keyword_frequency.idf
     *
     * @mbg.generated
     */
    public double getIdf() {
        return idf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.idf
     *
     * @param idf the value for document_keyword_frequency.idf
     *
     * @mbg.generated
     */
    public void setIdf(double idf) {
        this.idf = idf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column document_keyword_frequency.tfidf
     *
     * @return the value of document_keyword_frequency.tfidf
     *
     * @mbg.generated
     */
    public double getTfidf() {
        return tfidf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column document_keyword_frequency.tfidf
     *
     * @param tfidf the value for document_keyword_frequency.tfidf
     *
     * @mbg.generated
     */
    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }
}