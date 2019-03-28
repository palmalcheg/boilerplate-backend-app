package org.esolution.poc;


import org.esolution.poc.api.GitHubRxService;
import org.esolution.poc.api.Handlers;

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
                        )
                        .registryOf(registry -> registry
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
  

}
