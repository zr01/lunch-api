package com.aquinoa.lunch.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.aquinoa.lunch.daos.Ingredients;
import com.aquinoa.lunch.services.IngredientService;
import lombok.NonNull;

@Service
public class IngredientServiceImpl implements IngredientService {

  @Value("${lunch.service.base-url}")
  String lunchServiceBaseUrl;
  
  @Autowired
  private RestTemplate restTemplate;
  
  @Override
  public Ingredients getIngredients(@NonNull String id) throws RestClientException {
    return restTemplate.getForObject(lunchServiceBaseUrl + "/v2/" + id, Ingredients.class);
  }

}
