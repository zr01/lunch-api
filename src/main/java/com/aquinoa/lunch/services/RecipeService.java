package com.aquinoa.lunch.services;

import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Recipes;

public interface RecipeService {

  public Recipes getRecipes(String id) throws RestClientException, NullPointerException;
}
