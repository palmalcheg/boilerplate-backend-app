package org.esolution.poc.api;

import java.util.Optional;

import io.netty.util.internal.StringUtil;
import io.reactivex.schedulers.Schedulers;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.path.PathTokens;
import ratpack.util.MultiValueMap;

public abstract class Handlers {

	public static Handler gitHubUserRepos() {
		return  (ctx) -> {
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
	}

	public static Handler gitUsersPageable() {
		return (ctx) -> {
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
	}

	public static Handler gitHubPublicRepos() {
		return (ctx) -> {
    		GitHubRxService gitHubOps = ctx.get(GitHubRxService.class);
    		GitHubRxApi api = gitHubOps.api();
    		api.listPublicRepos()
    		  .subscribeOn(Schedulers.computation())
    		  .map(Jackson::json)
    		.subscribe(ctx::render);
    	};
	}

	public static Handler gitHubUserSearch() {
		return (ctx) -> {
    		GitHubRxService gitHubOps = ctx.get(GitHubRxService.class);
            final MultiValueMap<String, String> query = ctx.getRequest().getQueryParams();
    		String queryStr = query.get("q");
    		gitHubOps.findUsers(queryStr, 0)
    		    .map(Jackson::json)
    		  .subscribe(ctx::render);
    	};
	}

}
