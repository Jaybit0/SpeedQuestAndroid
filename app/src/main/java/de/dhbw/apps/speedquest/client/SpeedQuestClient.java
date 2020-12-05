package de.dhbw.apps.speedquest.client;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dhbw.apps.speedquest.client.packets.Packet;
import de.dhbw.apps.speedquest.client.packets.PacketGameStateChange;
import de.dhbw.apps.speedquest.client.packets.PacketInitialize;
import de.dhbw.apps.speedquest.client.packets.PacketPlayerUpdate;
import de.dhbw.apps.speedquest.client.packets.PacketTaskAssign;

public class SpeedQuestClient {

    private final Object lock = new Object();
    private boolean connecting = false;
    private boolean connected = false;
    private Map<String, Class<? extends Packet>> packetMapping = new HashMap<>();
    private Map<Class<? extends Packet>, List<PacketHandlerContext<? extends Packet>>> packetHandlers = new HashMap<>();
    private GameCache gameCache = new GameCache();
    private WebSocket currentSocket = null;

    public SpeedQuestClient() {
        init();
    }

    public boolean canConnect() {
        return !connecting && !connected;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public boolean isConnected() {
        return connected;
    }

    public GameCache getGameCache() {
        return gameCache;
    }

    public boolean tryConnect(@NonNull String ip, int port, @NonNull String username, @NonNull String key) {
        try {
            connect(ip, port, username, key);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void connect(@NonNull String ip, int port, @NonNull String username, @NonNull String key) throws IOException {
        synchronized (lock) {
            if (connecting || connected)
                throw new IOException("The client is already connecting / connected.");

            connecting = true;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("ws")
                .encodedAuthority(ip + ":" + port)
                .appendQueryParameter("name", username)
                .appendQueryParameter("gamekey", key);
        Log.d("SpeedQuest", "Connecting to: " + builder.build().toString() + "...");


        AsyncHttpClient.getDefaultInstance().websocket(builder.build().toString(), "ws", (ex, webSocket) -> {
            synchronized (lock) {
                connecting = false;
                connected = ex == null;
            }

            if (ex != null) {
                Log.e("SpeedQuest", ex.getMessage() != null ? ex.getMessage() : "", ex);
                return;
            }

            Log.d("SpeedQuest", "Successfully connected to: " + builder.build().toString());

            webSocket.setClosedCallback(e -> {
                synchronized (lock) {
                    connecting = false;
                    connected = false;
                    currentSocket = null;
                }
            });
            webSocket.setStringCallback(s -> {
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

                if (mapping == null)
                    // TODO: No mapping
                    return;

                final Packet mappedPacket = new Gson().fromJson(jObj, mapping);
                callPacketInUITask(mappedPacket);
            });

            synchronized (lock) {
                currentSocket = webSocket;
            }
        });
    }

    public <T extends Packet> boolean sendAsync(@NonNull T packet) {
        synchronized (lock) {
            if (isConnected() && currentSocket != null) {
                currentSocket.send(new Gson().toJson(packet));
                return true;
            }

            return false;
        }
    }

    public <T extends Packet> void unregisterPacketMappings(@NonNull Class<T> packetType) throws IllegalArgumentException {
        synchronized (lock) {
            packetHandlers.remove(packetType);
            packetMapping.remove(getPacketID(packetType));
        }
    }

    public <T extends Packet> void registerPacketHandler(@NonNull PacketHandler<T> packetHandler, @NonNull Class<T> packetType, @Nullable AppCompatActivity activity) throws IllegalArgumentException {
        registerPacket(packetType);

        synchronized (lock) {
            List<PacketHandlerContext<? extends Packet>> handlerContexts = packetHandlers.get(packetType);

            if (handlerContexts == null) {
                handlerContexts = new ArrayList<>();
                packetHandlers.put(packetType, handlerContexts);
            }

            handlerContexts.add(new PacketHandlerContext<T>(packetHandler, activity));
        }
    }

    public <T extends Packet> void registerPacket(@NonNull Class<T> packetType) throws IllegalArgumentException {
        String packetID = getPacketID(packetType);
        Class<? extends Packet> existingMapping = packetMapping.get(packetID);

        if (existingMapping != null && existingMapping != packetType)
            throw new IllegalArgumentException("Ambiguous packet-identifier. There is already another packet mapped to the given identifier.");

        packetMapping.put(packetID, packetType);
    }

    public String getPacketID(Class<? extends Packet> packetType) throws IllegalArgumentException {
        PacketID packetID = packetType.getAnnotation(PacketID.class);

        if (packetID == null)
            throw new IllegalArgumentException("Packet must contain a valid packet-identifier (@PacketID)");

        return packetID.value();
    }

    public void callPacketInUITask(@NonNull Packet packet) {
        synchronized (lock) {
            final List<PacketHandlerContext<? extends Packet>> handlerCtxs = packetHandlers.get(packet.getClass());

            if (handlerCtxs == null)
                return;

            for (PacketHandlerContext<? extends Packet> handlerCtx : handlerCtxs) {
                try {
                    final Method m = handlerCtx.handler.getClass().getMethod("handlePacket", Packet.class);

                    if (handlerCtx.activity == null) {
                        try {
                            m.invoke(handlerCtx.handler, packet);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    handlerCtx.activity.runOnUiThread(() -> {
                        try {
                            m.invoke(handlerCtx.handler, packet);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        registerPacketHandler(gameCache::init, PacketInitialize.class, null);
        registerPacketHandler(gameCache::updatePlayers, PacketPlayerUpdate.class, null);
        registerPacketHandler(gameCache::updateGameState, PacketGameStateChange.class, null);
        registerPacketHandler(gameCache::assignTask, PacketTaskAssign.class, null);
    }
}
