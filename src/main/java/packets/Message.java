/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packets;

import java.io.Serializable;

public class Message implements Serializable {
    private final String sender;
    private final String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    // Getters and setters (omitted for brevity)

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
