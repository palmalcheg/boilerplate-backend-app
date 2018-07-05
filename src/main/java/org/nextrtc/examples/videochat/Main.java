package org.nextrtc.examples.videochat;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) throws Exception {
        RatpackServer.start(
                server -> server
                        .serverConfig(
                                config -> config
                                        .connectQueueSize(10)
                                        .threads(10)
                                        .baseDir(BaseDir.find())
                                        .ssl(buildSslContext())
                        )
                        .registryOf(registry -> registry
                                .add(new HttpHandler())
                        )
                        .handlers(
                                chain -> chain
                                        .get("signaling", HttpHandler.class)
                                        .files(f -> f.dir("app")
                                                .indexFiles("index.html"))
                        )
        );
    }

    private static SslContext buildSslContext() {
        try {
            SelfSignedCertificate localhost = new SelfSignedCertificate("localhost", new SecureRandom(), 1024);
            return SslContextBuilder.forServer(localhost.certificate(), localhost.privateKey()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
