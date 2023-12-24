package com.example.gclo.Models;

public class PersondetailModel {

    private String name,id,latitude,longitude,distance,zoneIn,zoneOut, email, username, post,gender;

    public PersondetailModel(String name, String id, String latitude, String longitude, String distance, String zoneIn, String zoneOut) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.zoneIn = zoneIn;
        this.zoneOut = zoneOut;
    }




    public PersondetailModel() {

    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    //    id,name,username,email, gender,lat,long,in,out,dis
    public PersondetailModel(String id, String name, String username, String email, String gender,
    String latitude, String longitude,  String zoneIn, String distance) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoneIn = zoneIn;
//        this.zoneOut = zoneOut;
        this.distance = distance;
    }

    public PersondetailModel(String id,String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;

    }

    public PersondetailModel(String id,String name, String username, String email, String post,String gender) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.post = post;
        this.gender=gender;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getZoneIn() {
        return zoneIn;
    }

    public void setZoneIn(String zoneIn) {
        this.zoneIn = zoneIn;
    }

    public String getZoneOut() {
        return zoneOut;
    }

    public void setZoneOut(String zoneOut) {
        this.zoneOut = zoneOut;
    }
}
