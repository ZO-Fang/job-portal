package com.springjobportal.job.portal.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="job_seeker_profile")
public class JobSeekerProfile {

    @Id
    private Integer userAccountId;

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @MapsId
    private Users userId;
    //joinColumn意思是，在 job_seeker_profile 表中，有一个名为 user_account_id 的列，它存储着对应 users 表的主键值
    //mapsid的意思是，JobSeekerProfile 的主键 userAccountId 的值 = Users 对象的主键值
    //另外，虽然叫userId，但它指向整个user对象，包含了id在内的一切。但在数据库的表格里，它指向的是id

    private String firstName;
    private String lastName;
    private String city;
    private String state;
    private String country;
    //private String workAuthorization;
    private String employmentType;
    private String profession;
    private String resume;
    private String portfolio;

    @Column(nullable = true, length = 64)
    private String profilePhoto;



    @OneToMany(targetEntity=Skills.class, cascade = CascadeType.ALL, mappedBy = "jobSeekerProfile")
    private List<Skills> skills;

    public JobSeekerProfile() {
    }

    public JobSeekerProfile(Users userId){
        this.userId = userId;
    }

    public JobSeekerProfile(Integer userAccountId, Users userId, String firstName, String lastName, String city, String state, String country, String employmentType, String profession, String resume, String profilePhoto, String portfolio, List<Skills> skills) {
        this.userAccountId = userAccountId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.employmentType = employmentType;
        this.profession = profession;
        this.resume = resume;
        this.portfolio = portfolio;
        this.profilePhoto = profilePhoto;
        this.skills = skills;
    }


    public List<Skills> getSkills() {
        return skills;
    }

    public void setSkills(List<Skills> skills) {
        this.skills = skills;
    }


    public Integer getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Integer userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

//    @Transient
//    public String getPhotosImagePath(){
//        if (profilePhoto == null || userAccountId == null) return null;
//
//        return "photos/candidate/" + userAccountId + "/" + profilePhoto;
//    }String key = "users/" + userId + "/" + fileName;

    @Transient
    public String getPhotosImagePath() {
        if (profilePhoto == null) return null;

        //return "/photos/candidate/" + userAccountId + "/" + profilePhoto;
        //return "users/" + userId + "/" + profilePhoto;

        return "https://zf-jobportal-s3-1.s3.ap-southeast-2.amazonaws.com/users/" + userAccountId + "/" + profilePhoto;
    }


    @Override
    public String toString() {
        return "JobSeekerProfile{" +
                "profilePhoto='" + profilePhoto + '\'' +
                ", resume='" + resume + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", profession='" + profession + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", userId=" + userId +
                ", userAccountId=" + userAccountId +
                '}';
    }
}
