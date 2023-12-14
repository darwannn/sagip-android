package com.example.sagip;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketManager {
    private static Socket socket;

    public static Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket("https://sagip.onrender.com");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    public static void connectSocket() {
        Socket socket = getSocket();
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public static void disconnectSocket() {
        Socket socket = getSocket();
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }

    public static void emitLocationEvent(String residentUserId,double latitude, double longitude, String assistanceReqId) {
        Socket socket = getSocket();
        if (socket != null && socket.connected()) {
            try {
                JSONObject jsonBodySocket = new JSONObject();
                jsonBodySocket.put("receiver", residentUserId);
                jsonBodySocket.put("event", "location");
                        JSONObject contentJson = new JSONObject();
                contentJson.put("assistanceReqId", assistanceReqId);
                        contentJson.put("latitude", latitude);
                        contentJson.put("longitude", longitude);

                jsonBodySocket.put("content", contentJson);
                socket.emit("location", jsonBodySocket);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
