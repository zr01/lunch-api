package com.aquinoa.lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import com.aquinoa.lunch.daos.Recipes;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LunchApplicationTests extends AbstractJUnit4SpringContextTests {

  static final Logger l = LoggerFactory.getLogger(LunchApplicationTests.class);

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9000);

  @Autowired
  TestRestTemplate testRestTemplate;

  /**
   * We injected the mock data from mocky.io into src/test/resources/mappings.<br/>
   * Refer to recipes.json and ingredients.json for the response content of the mocked API call.
   */
  @Test
  public void queryAvailableLunchWithoutGivenDate() {
    l.debug("#### queryAvailableLunchWithoutGivenDate");
    Recipes recipes = testRestTemplate.getForObject("/lunch", Recipes.class);
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());

    /**
     * We cannot guarantee the count of the recipes due to the controller using the current date
     * when querying like this
     */
    l.debug("Recipes -> {}", recipes);
  }

  /**
   * The mocked data tells us there are only 1 recipe that should still be good on 2019-05-31.
   */
  @Test
  public void queryAvailableLunchBeforeEndOfMay() {
    l.debug("#### queryAvailableLunchBeforeEndOfMay");
    Recipes recipes = testRestTemplate.getForObject("/lunch?date=2019-05-31", Recipes.class);
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());

    l.debug("Recipes -> {}", recipes);
    assertEquals(1, recipes.getRecipes().size());
  }

  /**
   * The mocked data tells us there are only 3 recipes that should still be good on 2019-05-14.
   */
  @Test
  public void queryAvailbleLunchOnMidMay() {
    l.debug("#### queryAvailbleLunchOnMidMay");
    Recipes recipes = testRestTemplate.getForObject("/lunch?date=2019-05-14", Recipes.class);
    assertNotNull(recipes);
    assertNotNull(recipes.getRecipes());

    l.debug("Recipes -> {}", recipes);
    assertEquals(3, recipes.getRecipes().size());

    Set<String> goodRecipes =
        new HashSet<>(Arrays.asList("Ham and Cheese Toastie", "Hotdog", "Salad"));
    assertTrue(
        recipes.getRecipes().stream().allMatch(recipe -> goodRecipes.contains(recipe.getTitle())));
  }

  /**
   * Send an invalid date to the query
   */
  @Test
  public void queryInvalidDate() {
    l.debug("#### queryInvalidDate");
    ResponseEntity<Recipes> result =
        testRestTemplate.getForEntity("/lunch?date=invalid", Recipes.class);
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
  }
}
