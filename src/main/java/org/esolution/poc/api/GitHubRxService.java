package org.esolution.poc.api;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.SearchResults;

import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubRxService {

	final private GitHubRxApi gitHubApi;

	public GitHubRxService() {
		final Retrofit retrofit = new Retrofit.Builder()
				                  .baseUrl("https://api.github.com/")
				                  .addConverterFactory(GsonConverterFactory.create())
				                  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				                .build();

		gitHubApi = retrofit.create(GitHubRxApi.class);
	}
	
	public GitHubRxService(GitHubRxApi api) {
		gitHubApi = api;
	}

	public GitHubRxApi api() {
		return gitHubApi;
	}

	public Observable<String> getTopContributors(String userName) {
		return gitHubApi.listRepos(userName)
				.flatMapIterable(x -> x)
				.flatMap(repo -> gitHubApi.listRepoContributors(userName, repo.getName()))
				.flatMapIterable(x -> x)
				.filter(c -> c.getContributions() > 100)
				.sorted((a, b) -> b.getContributions() - a.getContributions())
				.map(Contributor::getName)
				.distinct();
	}
	
	public Observable<?> findUsers(String q, int currentPage) {
		  return getPageAndNext(p -> gitHubApi.userSearch(q, currentPage), currentPage)
				  .flatMapIterable(r -> r.body().getItems());
	}
	
	Observable<Response<SearchResults>> getPageAndNext(Function<Integer, Observable<Response<SearchResults>>> source, Integer page) {
		  return source.apply(page)
		      .concatMap(response -> {
		    	  int next = getNext(response);
		          // Terminal case.
		    	  
		          if (next == -1) {
		            return Observable.just(response);
		          }
		          return Observable.just(response)
		              .concatWith(getPageAndNext(source, response.body().getNext()));
		        }
		      ); 
		}
	
	Pattern pageNum =Pattern.compile("page=(\\d)+");

	private int getNext(Response<SearchResults> response) {
		Headers headers = response.headers();
		String link = headers.get("Link");
		int  left = -1;
		if (link != null) {
			Matcher pageMatcher = pageNum.matcher(link);
			while (pageMatcher.find()) {		
				String val = pageMatcher.group().replace("page=", "");
				Integer right = Integer.valueOf(val);
				left = Math.max(left, right);
			}	
		}
		return left;
	}
}