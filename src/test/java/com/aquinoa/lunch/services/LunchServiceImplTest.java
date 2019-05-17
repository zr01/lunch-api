package com.aquinoa.lunch.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.aquinoa.lunch.daos.Ingredient;
import com.aquinoa.lunch.daos.Ingredients;
import com.aquinoa.lunch.daos.Recipe;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.exceptions.ServiceException;
import com.aquinoa.lunch.services.impl.LunchServiceImpl;

@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {MockitoTestExecutionListener.class})
public class LunchServiceImplTest extends AbstractJUnit4SpringContextTests {

  static final Logger l = LoggerFactory.getLogger(LunchServiceImplTest.class);

  @TestConfiguration
  static class TestConfig {
    @Bean
    LunchService lunchService() {
      return new LunchServiceImpl();
    }

    @Bean
    RestTemplate restTemplate() {
      return new RestTemplateBuilder().rootUri("http://localhost:9000").build();
    }
  }

  @Autowired
  LunchService lunchService;

  @MockBean
  RecipeService recipeService;

  @MockBean
  IngredientService ingredientService;

  @Before
  public void setup() {
    when(recipeService.getRecipes(eq("valid"))).thenReturn(Recipes.builder()
        .recipes(Arrays.asList(
            Recipe.builder().title("Test Recipe with 1 valid ingredient")
                .ingredients(Arrays.asList("valid-ingredient")).build(),

            Recipe.builder().title("Test Recipe with 2 ingredients but not valid")
                .ingredients(Arrays.asList("invalid-1", "invalid-2")).build()))
        .build());

    when(ingredientService.getIngredients(eq("valid"))).thenReturn(Ingredients.builder()
        .ingredients(Arrays.asList(Ingredient.builder().title("valid-ingredient").build()))
        .build());

    when(recipeService.getRecipes(eq("rest-error")))
        .thenThrow(new RestClientException("mocked-rest-error"));

    when(ingredientService.getIngredients(eq("rest-error")))
        .thenThrow(new RestClientException("mocked-rest-error"));

    when(recipeService.getRecipes(eq("test-use-by-valid"))).thenReturn(Recipes.builder()
        .recipes(Arrays.asList(
            Recipe.builder().title("Test Recipe with ingredient by use by")
                .ingredients(Arrays.asList("valid-use-by", "valid-use-by-2")).build(),
            Recipe.builder().title("Test Recipe with an invalid ingredient by use by")
                .ingredients(Arrays.asList("invalid-use-by", "valid-use-by-2")).build()))
        .build());

    long currentTime = System.currentTimeMillis();
    when(ingredientService.getIngredients("test-use-by-valid")).thenReturn(Ingredients.builder()
        .ingredients(Arrays.asList(
            Ingredient.builder().title("valid-use-by")
                .useBy(new Date(currentTime + (1000 * 60 * 60))).bestBefore(new Date(currentTime))
                .build(),
            Ingredient.builder().title("valid-use-by-2")
                .useBy(new Date(currentTime + (1000 * 60 * 60))).bestBefore(new Date(currentTime))
                .build(),
            Ingredient.builder().title("invalid-use-by")
                .useBy(new Date(currentTime - (1000 * 60 * 60)))
                .bestBefore(new Date(currentTime - (1000 * 60 * 60 * 2))).build()))
        .build());

    when(recipeService.getRecipes(eq("test-good-date-and-sort"))).thenReturn(Recipes.builder()
        .recipes(Arrays.asList(
            Recipe.builder().title("Bottom Recipe").ingredients(Arrays.asList("low-quality"))
                .build(),
            Recipe.builder().title("Top Recipe").ingredients(Arrays.asList("high-quality")).build(),
            Recipe.builder().title("Not in list").ingredients(Arrays.asList("no-quality")).build(),
            Recipe.builder().title("Bottom Recipe 2").ingredients(Arrays.asList("low-quality"))
                .build()))
        .build());

    when(ingredientService.getIngredients("test-good-date-and-sort"))
        .thenReturn(Ingredients.builder()
            .ingredients(Arrays.asList(
                Ingredient.builder().title("low-quality")
                    .useBy(new Date(currentTime + (1000 * 60 * 60)))
                    .bestBefore(new Date(currentTime - (1000 * 60 * 60 * 2))).build(),
                Ingredient.builder().title("high-quality")
                    .useBy(new Date(currentTime + (1000 * 60 * 60 * 2)))
                    .bestBefore(new Date(currentTime + (1000 * 60 * 60))).build(),
                Ingredient.builder().title("no-quality")
                    .useBy(new Date(currentTime - (1000 * 60 * 60 * 2)))
                    .bestBefore(new Date(currentTime - (1000 * 60 * 60))).build()))
            .build());

    /**
     * Currently missing the null ID test -- something is wrong with mockito not checking isNull
     * correctly on the argument matcher
     */
  }

  @Test
  public void testGetRecipesWithAllIngredientsSuccess() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "valid");
    Recipes recipes = lunchService.getRecipesWithAllIngredients();

    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipesWithAllIngredientsWithInvalidRecipes() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithAllIngredients();
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipesWithAllIngredientsWithInvalidIngredients() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithAllIngredients();
  }

  @Test
  public void testGetRecipesWithinUseBySuccess() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "test-use-by-valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "test-use-by-valid");
    long fiveMinutesAgo = System.currentTimeMillis() - (1000 * 60 * 5);
    Recipes recipes = lunchService.getRecipesWithinUsedBy(new Date(fiveMinutesAgo));
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipestWithinUseByInvalidRecipes() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithinUsedBy(new Date());
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipestWithinUseByInvalidIngredients() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithinUsedBy(new Date());
  }

  @Test
  public void testGetRecipesWithinBestAndUsedBySuccess() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "test-good-date-and-sort");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "test-good-date-and-sort");
    long fiveMinutesFromNow = System.currentTimeMillis() + (1000 * 60 * 5);
    Recipes recipes = lunchService.getRecipesWithinBestAndUsedBy(new Date(fiveMinutesFromNow));
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(3, recipes.getRecipes().size());
    assertEquals("Top Recipe", recipes.getRecipes().get(0).getTitle());
    assertEquals("Bottom Recipe 2",
        recipes.getRecipes().get(recipes.getRecipes().size() - 1).getTitle());
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipesWithinBestAndUsedByInvalidRecipes() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithinBestAndUsedBy(new Date());
  }

  @Test(expected = ServiceException.class)
  public void testGetRecipesWithinBestAndUsedByInvalidIngredients() throws ServiceException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithinBestAndUsedBy(new Date());
  }
}
