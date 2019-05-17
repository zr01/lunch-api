package com.aquinoa.lunch.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.aquinoa.lunch.daos.Recipes;
import com.aquinoa.lunch.exceptions.ServiceException;
import com.aquinoa.lunch.services.LunchService;

@RestController
public class LunchController {

  static final Logger l = LoggerFactory.getLogger(LunchController.class);

  @Autowired
  LunchService lunchService;

  @GetMapping("/lunch")
  public Recipes getLunch(@RequestParam(name = "date", required = false) String date) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date queryDate = date != null ? sdf.parse(date) : sdf.parse(sdf.format(new Date()));
      return lunchService.getRecipesWithinBestAndUsedBy(queryDate);

    } catch (ServiceException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    } catch (ParseException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid date, please use yyyy-MM-dd for your date as its format");
    }
  }
}
