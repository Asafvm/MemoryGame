package il.co.afeka.com.memorygame.scoreboard;

import android.support.annotation.NonNull;

public class UserItem implements Comparable<UserItem> {
    private String id;
    private String age;
    private String name;
    private String score;
    public UserItem(){

    }
    public UserItem(String age, String name, String score, String id) {
        this.age = age;
        this.name = name;
        this.score = score;
        this.id = id;
    }


    @Override
    public int compareTo(@NonNull UserItem o) {
        return score.compareTo(o.getScore());
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
}
