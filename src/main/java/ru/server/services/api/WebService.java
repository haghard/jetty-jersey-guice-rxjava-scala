package ru.server.services.api;

import rx.Observable;

/**
 * Some Service
 *
 *
 */
public interface WebService {

  Observable<? extends String> htmlStream();

}
