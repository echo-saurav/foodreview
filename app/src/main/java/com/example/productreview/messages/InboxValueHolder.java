package com.example.productreview.messages;

public class InboxValueHolder {
    String imageUrl,userName,chatId,uid;

    public InboxValueHolder(String imageUrl, String userName, String chatId, String uid) {
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.chatId = chatId;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getChatId() {
        return chatId;
    }
}
