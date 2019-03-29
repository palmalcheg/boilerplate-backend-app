package org.esolution.poc.api;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.SearchResults;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
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
	
	public Observable<SearchResults> findUsers(String q, int currentPage) {
		  return getPageAndNext(p -> gitHubApi.userSearch(q, p), currentPage)
				     .filter(r -> r.body() != null)
				     .map(r -> r.body());
	}
	
	private Observable<Response<SearchResults>> getPageAndNext(Function<Integer, Observable<Response<SearchResults>>> source, Integer page) {
		  return source.apply(page)
			  .subscribeOn(Schedulers.io()) 	   
		      .concatMap(response -> {
		    	  extractNavigation(response);
		    	  SearchResults search = response.body();
		          // Terminal case.	
		          if (search == null || !search.hasNext() || Objects.equals(search.getNext(), page) ) {
		            return Observable.just(response);
		          }
		          return Observable.just(response)
		              .concatWith(getPageAndNext(source, search.getNext()));
		        }
		      ); 
		}
	
	Pattern pageNum = Pattern.compile("page=(\\d)+");

	private void extractNavigation(Response<SearchResults> response) {
		Headers headers = response.headers();
		String link = headers.get("Link");
		if (link ==null)
			return;
		Map<String,Integer> m = Stream.of(link.split(","))
		   .collect(Collectors.toMap(this::getLinkKey, this::getPgNum));
		SearchResults results = response.body();
		if (results != null) {
			results.setNavigation(m);
		}
	}

	private String getLinkKey(String part) {
		int lastIndexOf = part.indexOf("rel=\"");
		return part.substring(lastIndexOf+4).replace("\"", "");
	}
	
	private Integer getPgNum(String link) {
		if (link != null) {
			Matcher pageMatcher = pageNum.matcher(link);
			while (pageMatcher.find()) {		
				String val = pageMatcher.group().replace("page=", "");
				return Integer.valueOf(val);
			}	
		}
		return -1;
	}
}