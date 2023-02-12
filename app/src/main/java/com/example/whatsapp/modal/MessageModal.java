package com.example.whatsapp.modal;

public class MessageModal {
    String mesaase;
    String senderID;
    long timestamp;

    public MessageModal() {
    }

    public MessageModal(String mesaase, String senderID, long timestamp) {
        this.mesaase = mesaase;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    public String getMesaase() {
        return mesaase;
    }

    public void setMesaase(String mesaase) {
        this.mesaase = mesaase;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
