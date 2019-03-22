package org.esolution.poc.api;

import java.util.List;

import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.Repository;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubRxApi {
 
    @GET("users/{user}/repos")
    Observable<List<Repository>> listRepos(@Path("user") String user);
     
    @GET("repos/{user}/{repo}/contributors")
    Observable<List<Contributor>> listRepoContributors(
      @Path("user") String user,
      @Path("repo") String repo);   
}