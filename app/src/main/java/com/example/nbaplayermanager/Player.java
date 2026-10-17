package com.example.nbaplayermanager;


public class Player {

    private String id;
    private String name;
    private String team;
    private double points;

    public Player() {
    }

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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Player: " + name +
                "\nTeam: " + team +
                "\nPoints per game: " + points +
                "\nPlayer ID: " + id +
                "\n----------------------\n";
    }
}
