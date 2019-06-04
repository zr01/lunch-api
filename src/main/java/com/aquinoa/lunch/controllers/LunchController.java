package com.aquinoa.lunch.controllers;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.services.LunchService;

@RestController
public class LunchController {

  static final Logger l = LoggerFactory.getLogger(LunchController.class);

  @Autowired
  LunchService lunchService;

  @GetMapping("/lunch")
  public Recipes getLunch(@RequestParam(name = "date",
      required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) 
    throws RestClientException, NullPointerException {
    l.debug("Date: {}", date);
    return lunchService.getRecipesWithinBestAndUsedBy(date == null ? LocalDate.now() : date);
  }
}
