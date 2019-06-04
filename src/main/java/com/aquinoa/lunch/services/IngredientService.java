package com.aquinoa.lunch.services;

import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Ingredients;
import lombok.NonNull;

public interface IngredientService {

  /**
   * Interface must receive a non-null ID, NullPointerException is thrown as a valid check on the
   * argument.
   */
  public Ingredients getIngredients(@NonNull String id)
      throws RestClientException, NullPointerException;
}
