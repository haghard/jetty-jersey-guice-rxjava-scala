package ru.server.services;

import rx.Observable;


public interface WebService {

  Observable<? extends String> stream();

}
