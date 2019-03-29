package org.esolution.poc.api;

import java.util.List;

import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.Repository;
import org.esolution.poc.models.SearchResults;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubRxApi {
	
	@GET("repositories")
    Observable<List<Repository>> listPublicRepos();
	
	@GET("users")
    Observable<List<Contributor>> listAllUsers(@Query("since") int currentPage, @Query("per_page")int perPage);
 
    @GET("users/{user}/repos")
    Observable<List<Repository>> listRepos(@Path("user") String user);
     
    @GET("repos/{user}/{repo}/contributors")
    Observable<List<Contributor>> listRepoContributors(
      @Path("user") String user,
      @Path("repo") String repo);

    @GET("search/users")
	Observable<Response<SearchResults>> userSearch(@Query("q") String query, @Query("page") int page);
}