package org.esolution.poc.config;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.subjects.PublishSubject;

@Component(modules = BusModule.class)
@Singleton
public interface BusComponent {

    @Named(BusModule.PROVIDER_TOP_SUBJECT)
    PublishSubject<String> getTopSubject();

    @Named(BusModule.PROVIDER_BOTTOM_SUBJECT)
    PublishSubject<String> getBottomSubject();
}