package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraFetchIssuesResponse {

  public List<JiraIssueResponse> issues = new ArrayList<>();
}
