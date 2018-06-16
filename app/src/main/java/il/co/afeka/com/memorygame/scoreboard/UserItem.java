package il.co.afeka.com.memorygame.scoreboard;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class UserItem implements Comparable<UserItem> {
    private String id;
    private String age;
    private String name;
    private String score;
    private double lng;
    private double lat;
    public UserItem() {

    }

    public UserItem(String age, String name, String score, String id) {
        this.age = age;
        this.name = name;
        this.score = score;
        this.id = id;
        this.lat = -1;
        this.lng = -1;

    }


    @Override
    public int compareTo(@NonNull UserItem o) {
        return (Integer.valueOf(this.score)>Integer.valueOf(o.getScore()) ? -1 : 1);
    }


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
