package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraErrorResponse {

  private List<String> errorMessages = new ArrayList<>();
  private Map<String, String> errors = new HashMap<>();
}
