package com.aquinoa.lunch.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.services.impl.RecipeServiceImpl;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {MockitoTestExecutionListener.class})
public class RecipesServiceImplTest extends AbstractJUnit4SpringContextTests {

  static final Logger l = LoggerFactory.getLogger(RecipesServiceImplTest.class);

  @TestConfiguration
  static class TestConfig {
    @Bean
    RecipeService recipeService() {
      return new RecipeServiceImpl();
    }

    @Bean
    RestTemplate restTemplate() {
      return new RestTemplateBuilder().rootUri("http://localhost:9000").build();
    }
  }

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9000);

  @Autowired
  RecipeService recipeService;
  
  @Before
  public void setup() {
    ReflectionTestUtils.setField(recipeService, "lunchServiceBaseUrl", "http://localhost:9000");
  }

  @Test
  public void testServiceIsNotNull() {
    assertNotNull(recipeService);
  }

  @Test
  public void testGetRecipeSuccess() {
    Recipes recipes = recipeService.getRecipes("5c85f7a1340000e50f89bd6c");
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());
    assertTrue(recipes.getRecipes().size() > 0);
  }

  @Test(expected = NullPointerException.class)
  public void testGetRecipeIdIsNull() {
    recipeService.getRecipes(null);
  }

  @Test(expected = HttpClientErrorException.class)
  public void testGetRecipeRestException() {
    recipeService.getRecipes("invalidid");
  }
}
