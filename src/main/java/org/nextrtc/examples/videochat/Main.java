package org.nextrtc.examples.videochat;

import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public class Main {
    public static void main(String[] args) throws Exception {
        RatpackServer.start(
                server -> server
                        .serverConfig(
                                config -> config
                                        .connectQueueSize(10)
                                        .threads(10)
                                        .baseDir(BaseDir.find())
                        )
                        .registryOf(registry -> registry
                                .add(new HttpHandler())
                        )
                        .handlers(
                                chain -> chain
                                        .get("signaling", HttpHandler.class)
                                        .files(f -> f.indexFiles("index.html"))
                        )
        );
    }
}
