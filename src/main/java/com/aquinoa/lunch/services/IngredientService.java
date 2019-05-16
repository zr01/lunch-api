package com.aquinoa.lunch.services;

import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Ingredients;

public interface IngredientService {

  public Ingredients getIngredients(String id) throws RestClientException, NullPointerException;
}
