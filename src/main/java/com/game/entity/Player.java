package com.game.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
public class Player {

    private static final Date MIN_BIRTHDAY = Date.from(Instant.parse("1999-12-31T00:00:00.00Z"));
    private static final Date MAX_BIRTHDAY = Date.from(Instant.parse("3001-01-01T00:00:00.00Z"));
    private static final int MIN_EXPERIENCE = 0;
    private static final int MAX_EXPERIENCE = 10000000;
    private static final int MAX_NAME_LENGTH = 12;
    private static final int MAX_TITLE_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String title;

    @Enumerated(EnumType.STRING)
    private Race race;

    @Enumerated(EnumType.STRING)
    private Profession profession;

    private Integer experience;
    private Integer level;
    private Integer untilNextLevel;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    private Boolean banned;

    public void update (Player newPlayer) {
        if (newPlayer.name != null) {
            name = newPlayer.name;
        }
        if (newPlayer.title != null) {
            title = newPlayer.title;
        }
        if (newPlayer.race != null) {
            race = newPlayer.race;
        }
        if (newPlayer.profession != null) {
            profession = newPlayer.profession;
        }
        if (newPlayer.experience != null) {
            experience = newPlayer.experience;
        }
        if (newPlayer.birthday != null) {
            birthday = newPlayer.birthday;
        }
        if (newPlayer.banned != null) {
            banned = newPlayer.banned;
        }
        fillCalculatedParams();
    }

    public void fillCalculatedParams() {
        level =  (int)(Math.sqrt(2500 + 200 * experience) - 50)/100;
        untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
    }

    public boolean checkPlayerDataAllFilled() {
        return checkName() && checkTitle() && checkExperience() && checkBirthday();
    }

    public boolean checkPlayerDataOnlyFilled() {
        boolean result = true;
        if (name != null) {
            result = checkName();
        }
        if (title != null) {
            result = result && checkTitle();
        }
        if (experience != null) {
            result = result && checkExperience();
        }
        if (birthday != null) {
            result = result && checkBirthday();
        }
        return result;
    }

    private boolean checkName() {
        return name != null && !name.equals("") && name.length() <= MAX_NAME_LENGTH;
    }

    private boolean checkTitle() {
        return title.length() <= MAX_TITLE_LENGTH;
    }

    private boolean checkExperience() {
        return experience >= MIN_EXPERIENCE && experience <= MAX_EXPERIENCE;
    }

    private boolean checkBirthday() {
        return birthday.after(MIN_BIRTHDAY) && birthday.before(MAX_BIRTHDAY);
    }

    public boolean containsNotNullField() {
        return name != null || title != null || race != null || profession != null ||
                experience != null || birthday != null || banned != null;
    }
}

