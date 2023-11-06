package com.parkingmanager.parkingmanager;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Document(collection = "parkings")
public class Parking {
    @Id
    private String id;

    private String ipRoutera3;
    private String brKamera;

    public String getIpRoutera3() {
        return ipRoutera3;
    }

    public void setBrRoutera3(String ipRoutera3) {
        this.ipRoutera3 = ipRoutera3;
    }

    public String getBrKamera() {
        return brKamera;
    }

    public void setBrKamere(String brKamera) {
        this.brKamera = brKamera;
    }

    public String getIpRoutera2() {
        return ipRoutera2;
    }

    public void setIpRoutera2(String ipRoutera2) {
        this.ipRoutera2 = ipRoutera2;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getSmartRi() {
        return smartRi;
    }

    public void setSmartRi(String smartRi) {
        this.smartRi = smartRi;
    }

    public void setHoodId(String hoodId) {
        this.hoodId = hoodId;
    }

    private String ipRoutera2;

    private String flow;
    private String smartRi;
    private String name;
    private String hoodId;

    private String lastUpdateTime;

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getBrojParkinga() {
        return brojParkinga;
    }

    public void setBrojParkinga(String brojParkinga) {
        this.brojParkinga = brojParkinga;
    }

    private String brojParkinga;

    private String cijenaParkinga;

    public String getHoodId() {
        return hoodId;
    }

    public void setHood(String hoodId) {
        this.hoodId = hoodId;
    }

    public Parking(String name, String hoodId , String cijenaParkinga, String brojParkinga) {
        this.name = name;
        this.hoodId=hoodId;
        this.cijenaParkinga = cijenaParkinga;
        this.brojParkinga=brojParkinga;

    }

    private String ipRoutera1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Parking{" +
                "id='" + id + '\'' +
                ", brKamera='" + brKamera + '\'' +
                ", ipRoutera2='" + ipRoutera2 + '\'' +
                ", ipRoutera3='" + ipRoutera3 + '\'' +
                ", flow='" + flow + '\'' +
                ", smartRi='" + smartRi + '\'' +
                ", name='" + name + '\'' +
                ", hoodId='" + hoodId + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", brojParkinga='" + brojParkinga + '\'' +
                ", cijenaParkinga='" + cijenaParkinga + '\'' +
                ", ipRoutera1='" + ipRoutera1 + '\'' +
                ", brojZauzetih='" + brojZauzetih + '\'' +
                '}';
    }


    private String brojZauzetih;

    public String getBrojZauzetih()

    {
        return brojZauzetih;
    }

    public void setBrojZauzetih(String brojZauzetih) {
        this.brojZauzetih=brojZauzetih;
    }

    String cityId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCijenaParkinga() {
        return cijenaParkinga;
    }

    public void setCijenaParkinga(String cijenaParkinga) {
        this.cijenaParkinga = cijenaParkinga;
    }



    public String getIpRoutera1() {
        return ipRoutera1;
    }

    public void setIpRoutera1(String ipRoutera1) {
        this.ipRoutera1 = ipRoutera1;
    }

    public String getVrijemeNaplate() {
        return vrijemeNaplate;
    }

    public void setVrijemeNaplate(String vrijemeNaplate) {
        this.vrijemeNaplate = vrijemeNaplate;
    }

    public String getNaciniPlacanja() {
        return naciniPlacanja;
    }

    public void setNaciniPlacanja(String naciniPlacanja) {
        this.naciniPlacanja = naciniPlacanja;
    }

    public String getKordinate() {
        return kordinate;
    }

    public void setKordinate(String kordinate) {
        this.kordinate = kordinate;
    }

    private String vrijemeNaplate;
    private String naciniPlacanja;
    private String kordinate;


}
