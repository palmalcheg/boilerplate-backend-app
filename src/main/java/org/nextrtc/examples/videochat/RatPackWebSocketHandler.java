package org.nextrtc.examples.videochat;

import org.nextrtc.signalingserver.api.NextRTCServer;
import org.nextrtc.signalingserver.domain.Connection;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;

import java.util.concurrent.atomic.AtomicLong;

public class RatPackWebSocketHandler implements WebSocketHandler<String> {
    private final NextRTCServer server;
    private RatPackConnection connection;

    private static class RatPackConnection implements Connection {
        private static final AtomicLong nextId = new AtomicLong(1);
        private final WebSocket socket;
        private String id;

        private RatPackConnection(WebSocket socket) {
            this.id = "0xx" + nextId.getAndIncrement() + "-0";
            this.socket = socket;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isOpen() {
            return socket.isOpen();
        }

        @Override
        public void sendObject(Object object) {
            socket.send(NextRTCServer.MessageEncoder.encode(object));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RatPackConnection that = (RatPackConnection) o;

            return id != null ? id.equals(that.id) : that.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    public RatPackWebSocketHandler(NextRTCServer server) {
        this.server = server;
    }

    @Override
    public String onOpen(WebSocket webSocket) throws Exception {
        connection = new RatPackConnection(webSocket);
        server.register(connection);
        return null;
    }

    @Override
    public void onClose(WebSocketClose<String> webSocketClose) throws Exception {
        server.unregister(connection, webSocketClose.getOpenResult());
    }

    @Override
    public void onMessage(WebSocketMessage<String> webSocketMessage) throws Exception {

        server.handle(webSocketMessage.getText(), connection);
    }

}
