package org.esolution.poc.api;

import io.reactivex.Observable;
import org.esolution.poc.models.Contributor;
import org.esolution.poc.models.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitHubRxServiceTest {
    @Mock
    private GitHubRxApi gitHubRxApi;
    @InjectMocks
    private GitHubRxService service;

    @Test
    public void getTopContributorsIfUserHasNoRepos() {
        when(gitHubRxApi.listRepos(anyString())).thenReturn(Observable.just(emptyList()));

        assertThat(service.getTopContributors(anyString()).toList().blockingGet(), equalTo(emptyList()));
    }

    @Test
    public void getTopContributors() {
        String username = "test_user";
        List<Repository> repos = asList(
                new Repository("First repo", ""),
                new Repository("Second repo", ""));

        List<Contributor> firstRepoContributors = asList(
                new Contributor("John", 300),
                new Contributor("Michael", 50),
                new Contributor("Harry", 500));

        List<Contributor> secondRepoContributors = asList(
                new Contributor("Harry", 500),
                new Contributor("Robert", 100));

        when(gitHubRxApi.listRepos(username)).thenReturn(Observable.just(repos));
        when(gitHubRxApi.listRepoContributors(username, "First repo"))
                .thenReturn(Observable.just(firstRepoContributors));
        when(gitHubRxApi.listRepoContributors(username, "Second repo"))
                .thenReturn(Observable.just(secondRepoContributors));

        assertThat(service.getTopContributors(username).toList().blockingGet(), equalTo(asList("Harry", "John")));
    }
}