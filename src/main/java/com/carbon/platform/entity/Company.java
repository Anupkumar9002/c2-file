package com.carbon.platform.entity;

import com.carbon.platform.enums.ApprovalStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;
    private String gstNumber;
    private String panNumber;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private String adminComments;

    // Constructors
    public Company() {}

    public Company(User user, String companyName, String gstNumber, String panNumber, ApprovalStatus approvalStatus) {
        this.user = user;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.panNumber = panNumber;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }
}
