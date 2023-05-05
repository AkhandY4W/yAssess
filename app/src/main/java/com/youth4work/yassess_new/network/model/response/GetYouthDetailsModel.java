package com.youth4work.yassess_new.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetYouthDetailsModel {
    @SerializedName("profileDetail")
    private ProfileDetail profileDetail;

    public ProfileDetail getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(ProfileDetail profileDetail) {
        this.profileDetail = profileDetail;
    }

    public class ProfileDetail{

            @SerializedName("userName")
            private String userName;
            @SerializedName("name")
            private String name;
            @SerializedName("pic")
            private String pic;
            @SerializedName("headLine")
            private String headLine;
            @SerializedName("sex")
            private String sex;
            @SerializedName("aadhar")
            private String aadhar;
            @SerializedName("location")
            private String location;
            @SerializedName("country")
            private String country;
            @SerializedName("banner")
            private String banner;
            @SerializedName("locationId")
            private int locationId;
            @SerializedName("course")
            private String course;
            @SerializedName("specialization")
            private String specialization;
            @SerializedName("completed")
            private int completed;
            @SerializedName("companyName")
            private String companyName;
            @SerializedName("preferedLocation")
            private ArrayList<String> preferedLocation;
            @SerializedName("talents")
            private ArrayList<String> talents;

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getHeadLine() {
                return headLine;
            }

            public void setHeadLine(String headLine) {
                this.headLine = headLine;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getAadhar() {
                return aadhar;
            }

            public void setAadhar(String aadhar) {
                this.aadhar = aadhar;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getBanner() {
                return banner;
            }

            public void setBanner(String banner) {
                this.banner = banner;
            }

            public int getLocationId() {
                return locationId;
            }

            public void setLocationId(int locationId) {
                this.locationId = locationId;
            }

            public String getCourse() {
                return course;
            }

            public void setCourse(String course) {
                this.course = course;
            }

            public String getSpecialization() {
                return specialization;
            }

            public void setSpecialization(String specialization) {
                this.specialization = specialization;
            }

            public int getCompleted() {
                return completed;
            }

            public void setCompleted(int completed) {
                this.completed = completed;
            }

            public String getCompanyName() {
                return companyName;
            }

            public void setCompanyName(String companyName) {
                this.companyName = companyName;
            }

            public ArrayList<String> getPreferedLocation() {
                return preferedLocation;
            }

            public void setPreferedLocation(ArrayList<String> preferedLocation) {
                this.preferedLocation = preferedLocation;
            }

            public ArrayList<String> getTalents() {
                return talents;
            }

            public void setTalents(ArrayList<String> talents) {
                this.talents = talents;
            }
        }
}
