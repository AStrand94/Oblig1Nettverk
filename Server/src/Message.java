import java.util.Date;

/**
 * Created by brandhaug on 25.01.2017.
 */
public class Message {
    private static int id = 1;
    private int messageId;
    private Date date;
    private String message;
    private int senderId;
    private int receiverId;
    private boolean read;

    public Message(String message, int senderId, int receiverId){
        this.messageId = id++;
        this.date = new Date();
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.read = false;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Message.id = id;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
