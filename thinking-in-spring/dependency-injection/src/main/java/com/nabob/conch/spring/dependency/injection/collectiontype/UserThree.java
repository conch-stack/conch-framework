package com.nabob.conch.spring.dependency.injection.collectiontype;

import com.nabob.conch.spring.dependency.injection.basictype.City;

import java.util.Arrays;
import java.util.List;

/**
 * @author Adam
 * @date 2020/4/14
 */
public class UserThree {

    private City[] cities;

    private List<City> cityList;

    public UserThree() {
    }

    public UserThree(City[] cities, List<City> cityList) {
        this.cities = cities;
        this.cityList = cityList;
    }

    public City[] getCities() {
        return cities;
    }

    public void setCities(City[] cities) {
        this.cities = cities;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return "UserThree{" +
                "cities=" + Arrays.toString(cities) +
                ", cityList=" + cityList +
                '}';
    }
}
