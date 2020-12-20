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
import java.util.UUID;

import de.dhbw.apps.speedquest.SpeedQuestApplication;
import de.dhbw.apps.speedquest.client.packets.Packet;
import de.dhbw.apps.speedquest.client.packets.internal.PacketOnConnectResult;
import de.dhbw.apps.speedquest.client.packets.internal.PacketQuit;

public class SpeedQuestClient {

    private final Object lock = new Object();

    private boolean connecting = false;
    private boolean connected = false;
    private Map<String, Class<? extends Packet>> packetMapping = new HashMap<>();
    private Map<Class<? extends Packet>, List<PacketHandlerContext<? extends Packet>>> packetHandlers = new HashMap<>();
    private GameCache gameCache;
    private WebSocket currentSocket = null;

    public SpeedQuestClient(SpeedQuestApplication app) {

        init();
        gameCache = new GameCache(app);
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

    public boolean tryConnect(@NonNull String protocol, @NonNull String ip, int port, @NonNull String username, @NonNull String key) {
        try {
            connect(protocol, ip, port, username, key);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void connect(@NonNull String protocol, @NonNull String ip, int port, @NonNull String username, @NonNull String key) throws IOException {
        synchronized (lock) {
            if (connecting || connected)
                throw new IOException("The client is already connecting / connected.");

            connecting = true;
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(protocol)
                .encodedAuthority(ip + ":" + port)
                .appendQueryParameter("name", username)
                .appendQueryParameter("gamekey", key);
        Log.d("SpeedQuest", "Connecting to: " + builder.build().toString() + "...");


        AsyncHttpClient.getDefaultInstance().websocket(builder.build().toString(), protocol, (ex, webSocket) -> {
            synchronized (lock) {
                connecting = false;
                connected = ex == null;
            }

            if (ex != null) {
                Log.e("SpeedQuest", ex.getMessage() != null ? ex.getMessage() : "", ex);
                callPacketInUITask(new PacketOnConnectResult(ex));
                return;
            }

            callPacketInUITask(new PacketOnConnectResult(null));

            Log.d("SpeedQuest", "Successfully connected to: " + builder.build().toString());

            webSocket.setClosedCallback(e -> {
                synchronized (lock) {
                    connecting = false;
                    connected = false;
                    currentSocket = null;
                    callPacketInUITask(new PacketQuit());
                }
            });
            webSocket.setStringCallback(s -> {
                Log.d("SpeedQuest", "Callback: " + s);
                JsonElement element = JsonParser.parseString(s);

                if (!element.isJsonObject())
                    return;

                JsonObject jObj = element.getAsJsonObject();

                JsonElement packet = jObj.get("packet");

                if (packet == null || !packet.isJsonPrimitive())
                    return;

                String packetID = packet.getAsString();

                Class<? extends Packet> mapping = packetMapping.get(packetID);

                if (mapping == null)
                    return;

                final Packet mappedPacket = new Gson().fromJson(jObj, mapping);
                callPacketInUITask(mappedPacket);
            });

            synchronized (lock) {
                currentSocket = webSocket;
            }
        });
    }

    public void disconnect() {
        synchronized (lock) {
            if (!canConnect()) {
                currentSocket.end();
            }
        }
    }

    public <T extends Packet> boolean sendAsync(@NonNull T packet) {
        synchronized (lock) {
            Log.d("SpeedQuest", "Sending: " + new Gson().toJson(packet));
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

    public void unregisterMappingsOfActivity(@NonNull AppCompatActivity activity) {
        synchronized (lock) {
            for (Map.Entry<Class<? extends Packet>, List<PacketHandlerContext<? extends Packet>>> entry : packetHandlers.entrySet())
                entry.getValue().removeIf(handler -> handler.activity == activity);
        }
    }

    public void unregisterMappingsOfID(@NonNull UUID id) {
        synchronized (lock) {
            for (Map.Entry<Class<? extends Packet>, List<PacketHandlerContext<? extends Packet>>> entry : packetHandlers.entrySet())
                entry.getValue().removeIf(handler -> handler.id.equals(id));
        }
    }

    public <T extends Packet> void registerPacketHandler(@NonNull PacketHandler<T> packetHandler, @NonNull Class<T> packetType, @Nullable AppCompatActivity activity, @Nullable UUID id) throws IllegalArgumentException {
        registerPacket(packetType);

        synchronized (lock) {
            List<PacketHandlerContext<? extends Packet>> handlerContexts = packetHandlers.get(packetType);

            if (handlerContexts == null) {
                handlerContexts = new ArrayList<>();
                packetHandlers.put(packetType, handlerContexts);
            }

            handlerContexts.add(new PacketHandlerContext<>(id == null ? UUID.randomUUID() : id, packetHandler, activity));
        }
    }

    public <T extends Packet> void registerPacketHandler(@NonNull PacketHandler<T> packetHandler, @NonNull Class<T> packetType, @Nullable AppCompatActivity activity) throws IllegalArgumentException {
        registerPacketHandler(packetHandler, packetType, activity, null);
    }

    public <T extends Packet> void registerPacketHandler(@NonNull PacketHandler<T> packetHandler, @NonNull Class<T> packetType) throws IllegalArgumentException {
        registerPacketHandler(packetHandler, packetType, null, null);
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
            Log.d("SpeedQuest", "Calling packet: " + packet.getClass().getName());
            final List<PacketHandlerContext<? extends Packet>> handlerCtxs = packetHandlers.get(packet.getClass());

            if (handlerCtxs == null)
                return;

            for (PacketHandlerContext<? extends Packet> handlerCtx : handlerCtxs) {
                try {
                    final Method m = handlerCtx.handler.getClass().getMethod("handlePacket", Packet.class, SpeedQuestClient.class);

                    if (handlerCtx.activity == null) {
                        try {
                            m.invoke(handlerCtx.handler, packet, this);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Log.e("SpeedQuest", e.getMessage() != null ? e.getMessage() : "", e);
                        }
                        continue;
                    }

                    handlerCtx.activity.runOnUiThread(() -> {
                        try {
                            m.invoke(handlerCtx.handler, packet, this);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Log.e("SpeedQuest", e.getMessage() != null ? e.getMessage() : "", e);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    Log.e("SpeedQuest", e.getMessage() != null ? e.getMessage() : "", e);
                }
            }
        }
    }

    public void onQuit(PacketQuit packet, SpeedQuestClient client) {
        synchronized (lock) {
            connecting = false;
            connected = false;
            currentSocket = null;
        }
    }

    private void init() {
        registerPacketHandler(this::onQuit, PacketQuit.class);
    }
}
