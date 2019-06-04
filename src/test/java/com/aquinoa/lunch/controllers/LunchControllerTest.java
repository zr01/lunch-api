package com.aquinoa.lunch.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
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
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Recipe;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.services.LunchService;

@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {MockitoTestExecutionListener.class})
public class LunchControllerTest extends AbstractJUnit4SpringContextTests {

  static final Logger l = LoggerFactory.getLogger(LunchControllerTest.class);

  @TestConfiguration
  static class TestConfig {
    @Bean
    LunchController lunchController() {
      return new LunchController();
    }
  }

  @MockBean
  LunchService lunchService;

  @Autowired
  LunchController lunchController;

  @Before
  public void setup() throws RestClientException, NullPointerException {
    Recipes valid = Recipes.builder().recipes(Arrays.asList(Recipe.builder().title("valid-recipe")
        .ingredients(Arrays.asList("valid-ingredient")).build())).build();

    when(lunchService.getRecipesWithAllIngredients()).thenReturn(valid);

    when(lunchService.getRecipesWithinBestAndUsedBy(eq(LocalDate.of(2019, 1, 1))))
        .thenReturn(valid);

    when(lunchService.getRecipesWithinBestAndUsedBy(eq(LocalDate.of(2019, 1, 2))))
        .thenThrow(new NullPointerException("mocked-service-error"));

    when(lunchService.getRecipesWithinBestAndUsedBy(eq(LocalDate.now())))
        .thenReturn(Recipes.builder()
            .recipes(Arrays.asList(
                Recipe.builder().title("top-recipe").ingredients(Arrays.asList("top-ingredient"))
                    .build(),
                Recipe.builder().title("low-recipe").ingredients(Arrays.asList("low-ingredient"))
                    .build()))
            .build());
  }

  @Test
  public void testGetLunchForAllAvailableIngredients()
      throws RestClientException, NullPointerException {
    Recipes recipes = lunchController.getLunch(null);
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(2, recipes.getRecipes().size());
  }

  @Test
  public void testGetLunchWithUseBy() throws RestClientException, NullPointerException {
    Recipes recipes = lunchController.getLunch(LocalDate.of(2019, 1, 1));
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }

  @Test(expected = NullPointerException.class)
  public void testGetLunchServiceException() throws RestClientException, NullPointerException {
    lunchController.getLunch(LocalDate.of(2019, 1, 2));
  }
}
