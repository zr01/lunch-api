package com.aquinoa.lunch.daos;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {

  @EqualsAndHashCode.Include
  private String title;
  
  @JsonProperty("best-before")
  private LocalDate bestBefore;
  
  @JsonProperty("use-by")
  private LocalDate useBy;
}
