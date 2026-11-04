package com.ftms.model;

import java.time.LocalDate;

public class Notice {

    private int noticeId;
    private Integer adminId;
    private String title;
    private String content;
    private LocalDate publishedDate;

    public Notice(int noticeId, Integer adminId, String title,
                  String content, LocalDate publishedDate) {
        this.noticeId = noticeId;
        this.adminId = adminId;
        this.title = title;
        this.content = content;
        this.publishedDate = publishedDate;
    }

    public int getNoticeId() {
        return noticeId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }
}