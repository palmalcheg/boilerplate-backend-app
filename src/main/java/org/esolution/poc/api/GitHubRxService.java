package org.esolution.poc.api;

import java.util.function.Function;

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
				  .flatMapIterable(r -> r.body().items());
	}
	
	Observable<Response<SearchResults>> getPageAndNext(Function<Integer, Observable<Response<SearchResults>>> source, int page) {
		  return source.apply(page)
		      .concatMap(response -> {
		    	  ajustResults(response);
		          // Terminal case.
		          if (response.body().nextPage() == null) {
		            return Observable.just(response);
		          }
		          return Observable.just(response)
		              .concatWith(getPageAndNext(source, response.body().nextPage()));
		        }
		      ); 
		}

	private void ajustResults(Response<SearchResults> response) {
		SearchResults res = response.body();
		Headers headers = response.headers();
		String link = headers.get("Link");
	}
}