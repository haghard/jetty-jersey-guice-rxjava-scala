package ru.server.services;

import rx.Observable;

/**
 * Some Service
 *
 *
 */
public interface WebService {

  Observable<? extends String> stream();

}
