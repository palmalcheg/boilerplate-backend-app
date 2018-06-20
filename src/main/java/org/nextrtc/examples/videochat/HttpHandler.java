package org.nextrtc.examples.videochat;

import org.nextrtc.signalingserver.api.NextRTCServer;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.websocket.WebSockets;

public class HttpHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(HttpHandler.class);

    private static final NextRTCServer server = NextRTCServer.create(configuration -> {
        configuration.nextRTCProperties().setPingPeriod(1);

        configuration.signalResolver().addCustomSignal(Signal.fromString("upperCase"), (msg) -> {
            log.info("Upper case is comming: " + msg);
            configuration.messageSender().send(InternalMessage.create()
                    .to(msg.getFrom())
                    .signal(Signal.fromString("upperCase"))
                    .content(msg.getContent() == null ? "" : msg.getContent().toUpperCase())
                    .build());
        });

        configuration.eventDispatcher().addListener(new CustomHandler());
        return configuration;
    });

    @Override
    public void handle(Context context) throws Exception {
        WebSockets.websocket(context, new RatPackWebSocketHandler(server));
    }
}
