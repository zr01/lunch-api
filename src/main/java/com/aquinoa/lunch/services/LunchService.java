package com.aquinoa.lunch.services;

import java.time.LocalDate;
import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Recipes;

public interface LunchService {

  public Recipes getRecipesWithAllIngredients() throws RestClientException, NullPointerException;

  public Recipes getRecipesWithinUsedBy(LocalDate date)
      throws RestClientException, NullPointerException;

  public Recipes getRecipesWithinBestAndUsedBy(LocalDate date)
      throws RestClientException, NullPointerException;
}
