package com.aquinoa.lunch.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import com.aquinoa.lunch.daos.Recipe;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.exceptions.ServiceException;
import com.aquinoa.lunch.services.LunchService;

@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {MockitoTestExecutionListener.class})
public class LunchControllerTest extends AbstractJUnit4SpringContextTests{

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
  public void setup() throws ServiceException, ParseException {
    Recipes valid = Recipes.builder()
        .recipes(Arrays.asList(Recipe.builder()
            .title("valid-recipe")
            .ingredients(Arrays.asList("valid-ingredient"))
            .build()))
        .build();
    
    when(lunchService.getRecipesWithAllIngredients())
      .thenReturn(valid);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    when(lunchService.getRecipesWithinUsedBy(eq(sdf.parse("2019-01-01"))))
      .thenReturn(valid);
    
    when(lunchService.getRecipesWithinUsedBy(eq(sdf.parse("2019-01-02"))))
      .thenThrow(new ServiceException("mocked-service-error"));
    
    when(lunchService.getRecipesWithinBestAndUsedBy(any(Date.class)))
      .thenReturn(Recipes.builder()
          .recipes(Arrays.asList(Recipe.builder()
              .title("top-recipe")
              .ingredients(Arrays.asList("top-ingredient"))
              .build(),
              Recipe.builder()
              .title("low-recipe")
              .ingredients(Arrays.asList("low-ingredient"))
              .build()))
          .build());
  }
  
  @Test
  public void testGetLunchForAllAvailableIngredients() {
    Recipes recipes = lunchController.getLunch(false, false, null);
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }
  
  @Test
  public void testGetLunchWithUseBy() {
    Recipes recipes = lunchController.getLunch(true, false, "2019-01-01");
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(1, recipes.getRecipes().size());
  }
  
  @Test
  public void testGetLunchWithGoodDateAndSorted() {
    Recipes recipes = lunchController.getLunch(true, true, "2019-01-01");
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertEquals(2, recipes.getRecipes().size());
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchInvalidQuery() {
    lunchController.getLunch(false, true, null);
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchInvalidUseBy() {
    lunchController.getLunch(true, false, null);
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchInvalidUseByDate() {
    lunchController.getLunch(true, false, "invalid");
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchInvalidGoodDateAndSorted() {
    lunchController.getLunch(true, true, null);
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchInvalidGoodDateAndSortedDateParse() {
    lunchController.getLunch(true, true, "invalid");
  }
  
  @Test(expected = ResponseStatusException.class)
  public void testGetLunchServiceException() {
    lunchController.getLunch(true, false, "2019-01-02");
  }
}
