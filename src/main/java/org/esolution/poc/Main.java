package org.esolution.poc;

import java.security.SecureRandom;

import org.esolution.poc.api.GitHubRxService;
import org.esolution.poc.api.Handlers;
import org.esolution.poc.services.HttpHandler;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public final class Main {
	
	private Main () {} 
	
    public static void main(String[] args) throws Exception {
    	
        RatpackServer.start(
                server -> server
                        .serverConfig(
                                config -> config
                                        .connectQueueSize(10)
                                        .threads(10)
                                        .baseDir(BaseDir.find())
//                                        .ssl(buildSslContext())
                        )
                        .registryOf(registry -> registry
                                .add(new HttpHandler())
                                .add(new GitHubRxService())
                        )
                        .handlers(
                                chain -> chain
                                        .get("github", Handlers.gitHubPublicRepos())
                                        .get("github/users/search", Handlers.gitHubUserSearch())
                                        .get("github/users/:user?:[\\d*[a-zA-Z]+\\d*]+/repos", Handlers.gitHubUserRepos())
                                        .get("github/users/:start?:[\\d]+/:per_page?:[\\d]+?", Handlers.gitUsersPageable())
                                       
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
