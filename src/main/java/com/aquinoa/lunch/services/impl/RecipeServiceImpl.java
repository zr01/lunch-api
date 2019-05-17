package com.aquinoa.lunch.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.services.RecipeService;
import lombok.NonNull;

@Service
public class RecipeServiceImpl implements RecipeService {

  @Value("${lunch.service.base-url}")
  String lunchServiceBaseUrl;

  @Autowired
  private RestTemplate restTemplate;

  @Override
  public Recipes getRecipes(@NonNull String id) throws RestClientException, NullPointerException {
    return restTemplate.getForObject(lunchServiceBaseUrl + "/v2/" + id, Recipes.class);
  }

}
