package org.esolution.poc.api;

import java.util.List;

import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.Repository;

import io.reactivex.Observable;
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
}