package com.aquinoa.lunch.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.aquinoa.lunch.daos.Ingredients;
import com.aquinoa.lunch.services.impl.IngredientServiceImpl;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@TestExecutionListeners(listeners = {MockitoTestExecutionListener.class})
public class IngredientsServiceImplTest extends AbstractJUnit4SpringContextTests {

  static final Logger l = LoggerFactory.getLogger(IngredientsServiceImplTest.class);
  
  @TestConfiguration
  static class TestConfig {
    @Bean
    IngredientService ingredientService () {
      return new IngredientServiceImpl();
    }
    
    @Bean
    RestTemplate restTemplate() {
      return new RestTemplateBuilder()
          .rootUri("http://localhost:9000")
          .build();
    }
  }

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9000);

  @Autowired
  IngredientService ingredientService;
  
  @Test
  public void testServiceIsNotNull() {
    assertNotNull(ingredientService);
  }
  
  @Test
  public void testGetIngredientsSuccess() {
    Ingredients ingredients = ingredientService.getIngredients("5cdd037d300000da25e23402");
    assertNotNull(ingredients);
    assertNotNull(ingredients.getIngredients());
    assertTrue(ingredients.getIngredients().size() > 0);
  }
  
  @Test(expected = NullPointerException.class)
  public void testGetIngredientsIdIsNull() {
    ingredientService.getIngredients(null);
  }

  @Test(expected = HttpClientErrorException.class)
  public void testGetIngredientsRestException() {
    ingredientService.getIngredients("invalidingredients");
  }
}
