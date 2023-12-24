package com.example.gclo.Models;

public class UserModel {
    private static String name, confirmPassword, email, id, imageUrl, password, username, post, address, phoneNumber;
    private String gender;

    public UserModel() {
    }

    // Constructor
    public UserModel(String name, String username, String email, String password, String id, String imageUrl) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    // Constructor
    public UserModel(String id, String username) {
        this.id = id;
        this.username = username;
    }

    // Constructor to store current user data
//    public UserModel(String email, String username, String post, String address, String phoneNumber, String gender) {
//        this.email = email;
//        this.username = username;
//        this.post = post;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//        this.gender = gender;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPost() {
        return post;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
