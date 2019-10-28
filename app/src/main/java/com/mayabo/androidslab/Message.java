package com.mayabo.androidslab;

public class Message {
    private String message;
    private boolean isSent;
    private boolean isReceived;
    private long id;

    public Message() {
    }

    public Message(long id, String message, boolean isSent, boolean isRecieved){
        this.id = id;
        this.message = message;
        this.isSent = isSent;
        this.isReceived = isRecieved;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSent() {
        return this.isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public boolean isReceived() {
        return this.isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  " id: " + id +
                ", message: " + message + '\'' +
                ", isSent: " + isSent +
                ", isReceived: " + isReceived + "\n";
    }
}
