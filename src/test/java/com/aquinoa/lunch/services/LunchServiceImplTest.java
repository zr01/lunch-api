package com.aquinoa.lunch.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

    // long currentTime = System.currentTimeMillis();
    LocalDate currentTime = LocalDate.now();
    when(ingredientService.getIngredients("test-use-by-valid")).thenReturn(Ingredients.builder()
        .ingredients(Arrays.asList(
            Ingredient.builder().title("valid-use-by")
                .useBy(LocalDate.now().plus(1, ChronoUnit.DAYS)).bestBefore(currentTime).build(),
            Ingredient.builder().title("valid-use-by-2")
                .useBy(LocalDate.now().plus(1, ChronoUnit.DAYS)).bestBefore(currentTime).build(),
            Ingredient.builder().title("invalid-use-by")
                .useBy(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .bestBefore(LocalDate.now().minus(2, ChronoUnit.DAYS)).build()))
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
                    .useBy(LocalDate.now().plus(1, ChronoUnit.DAYS))
                    .bestBefore(LocalDate.now().minus(2, ChronoUnit.DAYS)).build(),
                Ingredient.builder().title("high-quality")
                    .useBy(LocalDate.now().plus(2, ChronoUnit.DAYS))
                    .bestBefore(LocalDate.now().plus(1, ChronoUnit.DAYS)).build(),
                Ingredient.builder().title("no-quality")
                    .useBy(LocalDate.now().minus(2, ChronoUnit.DAYS))
                    .bestBefore(LocalDate.now().minus(1, ChronoUnit.DAYS)).build()))
            .build());

    /**
     * Currently missing the null ID test -- something is wrong with mockito not checking isNull
     * correctly on the argument matcher
     */
  }

  @Test
  public void testGetRecipesWithAllIngredientsSuccess()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "valid");
    Recipes recipes = lunchService.getRecipesWithAllIngredients();

    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipesWithAllIngredientsWithInvalidRecipes()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithAllIngredients();
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipesWithAllIngredientsWithInvalidIngredients()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithAllIngredients();
  }

  @Test
  public void testGetRecipesWithinUseBySuccess() throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "test-use-by-valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "test-use-by-valid");
    Recipes recipes =
        lunchService.getRecipesWithinUsedBy(LocalDate.now().minus(1, ChronoUnit.DAYS));
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipestWithinUseByInvalidRecipes()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithinUsedBy(LocalDate.now());
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipestWithinUseByInvalidIngredients()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithinUsedBy(LocalDate.now());
  }

  @Test
  public void testGetRecipesWithinBestAndUsedBySuccess()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "test-good-date-and-sort");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "test-good-date-and-sort");
    Recipes recipes = lunchService.getRecipesWithinBestAndUsedBy(LocalDate.now());
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(3, recipes.getRecipes().size());
    assertEquals("Top Recipe", recipes.getRecipes().get(0).getTitle());
    assertEquals("Bottom Recipe 2",
        recipes.getRecipes().get(recipes.getRecipes().size() - 1).getTitle());
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipesWithinBestAndUsedByInvalidRecipes()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "rest-error");
    lunchService.getRecipesWithinBestAndUsedBy(LocalDate.now());
  }

  @Test(expected = RestClientException.class)
  public void testGetRecipesWithinBestAndUsedByInvalidIngredients()
      throws RestClientException, NullPointerException {
    ReflectionTestUtils.setField(lunchService, "RECIPES_ID", "valid");
    ReflectionTestUtils.setField(lunchService, "INGREDIENTS_ID", "rest-error");
    lunchService.getRecipesWithinBestAndUsedBy(LocalDate.now());
  }
}
