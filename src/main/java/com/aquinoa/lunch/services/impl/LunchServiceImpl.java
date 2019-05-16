package com.aquinoa.lunch.services.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Ingredients;
import com.aquinoa.lunch.daos.Recipe;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.exceptions.ServiceException;
import com.aquinoa.lunch.services.IngredientService;
import com.aquinoa.lunch.services.LunchService;
import com.aquinoa.lunch.services.RecipeService;

@Service
public class LunchServiceImpl implements LunchService {

  static final Logger l = LoggerFactory.getLogger(LunchServiceImpl.class);

  /**
   * Default IDs for the recipes and ingredients -- possible to be changed later
   */
  static String RECIPES_ID = "5c85f7a1340000e50f89bd6c";
  static String INGREDIENTS_ID = "5cdd037d300000da25e23402";

  @Autowired
  RecipeService recipeService;

  @Autowired
  IngredientService ingredientService;

  /**
   * Filtered recipes based on available ingredients
   */
  @Override
  public Recipes getRecipesWithAllIngredients() throws ServiceException {
    try {
      Recipes recipes = recipeService.getRecipes(RECIPES_ID);
      Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

      List<Recipe> filteredRecipes = recipes.getRecipes().stream()
          .filter(recipe -> recipe.getIngredients().stream()
              .allMatch(i -> ingredients.getIngredients().stream()
                  .anyMatch(ingredient -> ingredient.getTitle().equals(i))))
          .collect(Collectors.toList());
      return Recipes.builder().recipes(filteredRecipes).build();
    } catch (RestClientException e) {
      l.error("REST call error:", e);
      throw new ServiceException(e.getMessage());
    } catch (NullPointerException e) {
      l.error("Invalid ID for recipe/ingredient with error: ", e);
      throw new ServiceException(e.getMessage());
    }
  }

  /**
   * Filter recipes on the following category:<br/>
   * < useBy
   */
  @Override
  public Recipes getRecipesWithinUsedBy(Date useBy) throws ServiceException {
    try {
      Recipes recipes = recipeService.getRecipes(RECIPES_ID);
      Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

      List<Recipe> filteredRecipes = recipes.getRecipes().stream()
          .filter(recipe -> recipe.getIngredients().stream()
              .allMatch(i -> ingredients.getIngredients().stream()
                  .anyMatch(ingredient -> ingredient.getTitle().equals(i)
                      && ingredient.getUseBy().getTime() > useBy.getTime())))
          .collect(Collectors.toList());
      return Recipes.builder().recipes(filteredRecipes).build();
    } catch (RestClientException e) {
      l.error("REST call error: ", e);
      throw new ServiceException(e.getMessage());
    } catch (NullPointerException e) {
      l.error("Invalid ID for recipe/ingredient with error: ", e);
      throw new ServiceException(e.getMessage());
    }
  }

  /**
   * Filter recipes on the following category:<br/>
   * >= bestBy and < useBy - will sort to the bottom of the list<br/>
   * else if < bestBy on top of the list
   */
  @Override
  public Recipes getRecipesWithinBestAndUsedBy(Date date) throws ServiceException {
    try {
      Recipes recipes = recipeService.getRecipes(RECIPES_ID);
      Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

      List<Recipe> filteredRecipes = recipes.getRecipes().stream()
          .filter(recipe -> recipe.getIngredients().stream()
              .allMatch(i -> ingredients.getIngredients().stream()
                  .anyMatch(ingredient -> ingredient.getTitle().equals(i)
                      && ingredient.getUseBy().getTime() > date.getTime())))
          .collect(Collectors.toList());
      
      /**
       * We've filtered out anything that is < useBy<br/>
       * We sort the list on which of the items are still before their best by date
       */
      filteredRecipes.sort((rLeft, rRight) -> {
        if (ingredients.getIngredients().stream()
            .filter(i -> rLeft.getIngredients().contains(i.getTitle()))
            .anyMatch(i -> i.getBestBefore().getTime() > date.getTime()))
          return -1;
        else if (ingredients.getIngredients().stream()
            .filter(i -> rRight.getIngredients().contains(i.getTitle()))
            .anyMatch(i -> i.getBestBefore().getTime() > date.getTime()))
          return 1;
        return 0;
      });
      
      return Recipes.builder().recipes(filteredRecipes).build();
    } catch (RestClientException e) {
      l.error("REST call error: ", e);
      throw new ServiceException(e.getMessage());
    } catch (NullPointerException e) {
      l.error("Invalid ID for recipe/ingredient with error: ", e);
      throw new ServiceException(e.getMessage());
    }
  }
}