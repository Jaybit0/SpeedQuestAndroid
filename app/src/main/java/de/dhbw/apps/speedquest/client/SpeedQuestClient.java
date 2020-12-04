package de.dhbw.apps.speedquest.client;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.dhbw.apps.speedquest.client.packets.Packet;

public class SpeedQuestClient {

    private Map<String, Class<? extends Packet>> packetMapping = new HashMap<>();
    private Map<Class<? extends Packet>, PacketHandlerContext<? extends Packet>> packetHandlers = new HashMap<>();

    public SpeedQuestClient(Application app) {
    }

    public void connect(String ip, int port, String username, String key) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("ws")
                .encodedAuthority(ip + ":" + port)
                .appendQueryParameter("name", username)
                .appendQueryParameter("gamekey", key);
        Log.d("SpeedQuest", builder.build().toString());

        AsyncHttpClient.getDefaultInstance().websocket(builder.build().toString(), "ws", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    Log.d("SpeedQuest", ex.getMessage());
                    return;
                }
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("SpeedQuest", "Callback: " + s);
                        JsonElement element = JsonParser.parseString(s);

                        if (!element.isJsonObject())
                            // TODO: Wrong packet format
                            return;

                        JsonObject jObj = element.getAsJsonObject();

                        JsonElement packet = jObj.get("packet");

                        if (packet == null || !packet.isJsonPrimitive())
                            // TODO: Wrong packet format
                            return;

                        String packetID = packet.getAsString();

                        Class<? extends Packet> mapping = packetMapping.get(packetID);

                        Log.d("SpeedQuest", "Has packet mapping?");

                        if (mapping == null)
                            // TODO: No mapping
                            return;

                        Log.d("SpeedQuest", "Packet has mapping!");

                        final Packet mappedPacket = new Gson().fromJson(jObj, mapping);
                        final PacketHandlerContext<? extends Packet> handlerCtx = packetHandlers.get(mapping);

                        if (handlerCtx != null) {
                            try {
                                final Method m = handlerCtx.handler.getClass().getMethod("handlePacket", mapping);
                                handlerCtx.activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            m.invoke(handlerCtx.handler, mappedPacket);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public <T extends Packet> void registerPacketHandler(PacketHandler<T> packetHandler, Class<T> packetType, AppCompatActivity activity) throws IllegalArgumentException {
        registerPacket(packetType);
        packetHandlers.put(packetType, new PacketHandlerContext<T>(packetHandler, activity));
    }

    public <T extends Packet> void registerPacket(Class<T> packetType) throws IllegalArgumentException {
        PacketID packetID = packetType.getAnnotation(PacketID.class);

        if (packetID == null)
            throw new IllegalArgumentException("Packet must contain a valid packet-identifier (@PacketID)");

        Class<? extends Packet> existingMapping = packetMapping.get(packetID.identifier());

        if (existingMapping != null && existingMapping != packetType)
            throw new IllegalArgumentException("Ambiguous packet-identifier. There is already another packet mapped to the given identifier.");

        packetMapping.put(packetID.identifier(), packetType);
    }

}
