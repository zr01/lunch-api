package com.aquinoa.lunch.services;

import java.util.Date;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.exceptions.ServiceException;

public interface LunchService {

  public Recipes getRecipesWithAllIngredients() throws ServiceException;
  public Recipes getRecipesWithinUsedBy(Date date) throws ServiceException;
  public Recipes getRecipesWithinBestAndUsedBy(Date date) throws ServiceException;
}
