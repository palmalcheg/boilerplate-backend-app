package org.esolution.poc;

import java.security.SecureRandom;
import java.util.Optional;

import org.esolution.poc.api.GitHubRxApi;
import org.esolution.poc.api.GitHubRxService;
import org.esolution.poc.services.HttpHandler;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.StringUtil;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.path.PathTokens;
import ratpack.server.BaseDir;
import ratpack.server.RatpackServer;

public final class Main {
	
	private Main () {}
	
    public static void main(String[] args) throws Exception {
    	
    	Handler gitHubUserRepos = (ctx) -> {
    		GitHubRxService gitHubOps = ctx.get(GitHubRxService.class);
    		GitHubRxApi api = gitHubOps.api();
    		String userName = ctx.getPathTokens().get("user");
    		if (StringUtil.isNullOrEmpty(userName)) {
    			ctx.render("Null user");
    			return;
    		}
    		api.listRepos(userName)
    		  .map(Jackson::json)
    		  .subscribe(ctx::render);
    	};
    	
    	Handler gitUsersPageable = (ctx) -> {
    		GitHubRxService gitHubOps = ctx.get(GitHubRxService.class);
    		GitHubRxApi api = gitHubOps.api();
    		final PathTokens pathTokens = ctx.getPathTokens();
    		
    		int currentPage = Optional.of("start")
					      .map(pathTokens::get)
			              .map(Integer::valueOf)
					      .orElse(0);
    		
    		int onPage = Optional.of("per_page")
				      .map(pathTokens::get)
		              .map(Integer::valueOf)
				      .orElse(0);
    		   ; 	
    		api.listAllUsers(currentPage, onPage)
    		  .map(Jackson::json)
    		  .subscribe(ctx::render);
    	};
    	
    	Handler gitHubPublicRepos = (ctx) -> {
    		GitHubRxService gitHubOps = ctx.get(GitHubRxService.class);
    		GitHubRxApi api = gitHubOps.api();
    		api.listPublicRepos()
    		.map(Jackson::json)
    		.subscribe(ctx::render);
    	};
    	
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
                                        .get("github", gitHubPublicRepos)
                                        .get("github/users/:user?:[\\d*[a-zA-Z]+\\d*]+/repos", gitHubUserRepos)
                                        .get("github/users/:start?:[\\d]+/:per_page?:[\\d]+?", gitUsersPageable)
                                       
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
