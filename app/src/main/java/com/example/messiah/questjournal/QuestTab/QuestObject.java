package com.example.messiah.questjournal.QuestTab;

import java.util.Date;

/**
 * Created by Raul on 3/4/2016.
 */
public class QuestObject {
    private String title;
    private int difficulty;
    private String description;
    private String questID;
    private String deadline;

    public QuestObject(){}

    public QuestObject(String title, int difficulty, String description, String deadline, String questID) {
        this.title = title;
        this.difficulty = difficulty;
        this.description = description;
        this.deadline = deadline;
        this.questID = questID;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestID(){return questID;}

    public int getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }
}