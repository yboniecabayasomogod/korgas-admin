package com.example.korgasadmin;

public class ReadWritePetrolStationData {

    //    they both all string but i separate because its to long to declare all
    public String name, contact, address, latitude, longitude, websiteLink, mapLink;
    public String gasolinePrice, dieselPrice, kerosenePrice;
    public String petrolStationPicture;
    public String postSaySomething, postSomethingPicture;

    public ReadWritePetrolStationData(String name, String contact, String address, String latitude, String longitude, String websiteLink, String mapLink, String gasolinePrice, String dieselPrice, String kerosenePrice, String petrolStationPicture, String postSaySomething, String postSomethingPicture) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.websiteLink = websiteLink;
        this.mapLink = mapLink;
        this.gasolinePrice = gasolinePrice;
        this.dieselPrice = dieselPrice;
        this.kerosenePrice = kerosenePrice;
        this.petrolStationPicture = petrolStationPicture;
        this.postSaySomething = postSaySomething;
        this.postSomethingPicture = postSomethingPicture;
    }

    public ReadWritePetrolStationData() {
    }
}
