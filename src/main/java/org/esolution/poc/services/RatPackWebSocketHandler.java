package org.esolution.poc.services;

import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicLong;

public class RatPackWebSocketHandler implements WebSocketHandler<String> {
//    private final NextRTCServer server;
    private RatPackConnection connection;

    private static class RatPackConnection implements Closeable {
        private static final AtomicLong nextId = new AtomicLong(1);
        private final WebSocket socket;
        private String id;

        private RatPackConnection(WebSocket socket) {
            this.id = "0xx" + nextId.getAndIncrement() + "-0";
            this.socket = socket;
        }

        public String getId() {
            return id;
        }

        public boolean isOpen() {
            return socket.isOpen();
        }

        public void sendObject(Object object) {
//            socket.send(NextRTCServer.MessageEncoder.encode(object));
        }

        @Override
        public void close() {
            socket.close();
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

    public RatPackWebSocketHandler() {
    }

    @Override
    public String onOpen(WebSocket webSocket) throws Exception {
        connection = new RatPackConnection(webSocket);
//        server.register(connection);
        return null;
    }

    @Override
    public void onClose(WebSocketClose<String> webSocketClose) throws Exception {
//        server.unregister(connection, webSocketClose.getOpenResult());
    }

    @Override
    public void onMessage(WebSocketMessage<String> webSocketMessage) throws Exception {
//        server.handle(webSocketMessage.getText(), connection);
    }

}
