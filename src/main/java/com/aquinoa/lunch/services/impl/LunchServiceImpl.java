package com.aquinoa.lunch.services.impl;

import java.time.LocalDate;
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
import com.aquinoa.lunch.services.IngredientService;
import com.aquinoa.lunch.services.LunchService;
import com.aquinoa.lunch.services.RecipeService;

@Service
public class LunchServiceImpl implements LunchService {

  static final Logger l = LoggerFactory.getLogger(LunchServiceImpl.class);

  /**
   * Default IDs for the recipes and ingredients -- possible to be changed later The static strings
   * are mainly used for the keys being used to query the API. Can't assign final to these strings
   * it makes testing harder
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
  public Recipes getRecipesWithAllIngredients() throws RestClientException, NullPointerException {
    Recipes recipes = recipeService.getRecipes(RECIPES_ID);
    Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

    List<Recipe> filteredRecipes = recipes.getRecipes().stream()
        /**
         * Filter the recipe based on which ALL of its ingredients exist
         */
        .filter(recipe -> recipe.getIngredients().stream()
            .allMatch(i -> ingredients.getIngredients().stream()
                /**
                 * Matches the ingredient from the recipe to the ingredient list
                 */
                .anyMatch(ingredient -> ingredient.getTitle().equals(i))))
        .collect(Collectors.toList());
    return Recipes.builder().recipes(filteredRecipes).build();
  }

  /**
   * Filter recipes on the following category:<br/>
   * < useBy
   */
  @Override
  public Recipes getRecipesWithinUsedBy(LocalDate useBy)
      throws RestClientException, NullPointerException {
    Recipes recipes = recipeService.getRecipes(RECIPES_ID);
    Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

    List<Recipe> filteredRecipes = recipes.getRecipes().stream()
        /**
         * Filter the recipe based on which ALL of its ingredients exist
         */
        .filter(recipe -> recipe.getIngredients().stream()
            .allMatch(i -> ingredients.getIngredients().stream()
                /**
                 * Matches the ingredient from the recipe to the ingredient list and filters any
                 * ingredient past the use-by date
                 */
                .anyMatch(ingredient -> ingredient.getTitle().equals(i)
                    && ingredient.getUseBy().isAfter(useBy))))
        .collect(Collectors.toList());
    return Recipes.builder().recipes(filteredRecipes).build();
  }

  /**
   * Filter recipes on the following category:<br/>
   * >= bestBy and < useBy - will sort to the bottom of the list<br/>
   * else if < bestBy on top of the list
   */
  @Override
  public Recipes getRecipesWithinBestAndUsedBy(LocalDate date)
      throws RestClientException, NullPointerException {
    Recipes recipes = recipeService.getRecipes(RECIPES_ID);
    Ingredients ingredients = ingredientService.getIngredients(INGREDIENTS_ID);

    List<Recipe> filteredRecipes = recipes.getRecipes().stream()
        /**
         * Filter the recipe based on which ALL of its ingredients exist and are not yet past their
         * use-by date
         */
        .filter(recipe -> recipe.getIngredients().stream()
            .allMatch(i -> ingredients.getIngredients().stream()
                .anyMatch(ingredient -> ingredient.getTitle().equals(i)
                    && ingredient.getUseBy().isAfter(date))))
        /**
         * Sort the recipe list based on whose ingredients may expire first
         */
        .sorted((rLeft, rRight) -> {
          if (ingredients.getIngredients().stream()
              .filter(i -> rLeft.getIngredients().contains(i.getTitle()))
              .anyMatch(i -> i.getBestBefore().isAfter(date)))
            return -1;
          else if (ingredients.getIngredients().stream()
              .filter(i -> rRight.getIngredients().contains(i.getTitle()))
              .anyMatch(i -> i.getBestBefore().isAfter(date)))
            return 1;
          return 0;
        }).collect(Collectors.toList());

    return Recipes.builder().recipes(filteredRecipes).build();
  }
}
