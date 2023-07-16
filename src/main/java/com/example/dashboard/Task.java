package com.example.dashboard;

public class Task {
    public String taskName;
    public int taskID;
    public int taskCounter;
    public Task(String taskName, int taskID, int taskCounter) {
        this.taskName = taskName;
        this.taskID = taskID;
        this.taskCounter = taskCounter;
    }
}