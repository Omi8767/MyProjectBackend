package com.project.backend.entity;

import jakarta.persistence.*;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compid;
    private String compname;
    @Column(columnDefinition = "LONGTEXT")
    private String logoUrl;

    public Long getCompid() {
        return compid;
    }

    public void setCompid(Long compid) {
        this.compid = compid;
    }

    public String getCompname() {
        return compname;
    }

    public void setCompname(String compname) {
        this.compname = compname;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
