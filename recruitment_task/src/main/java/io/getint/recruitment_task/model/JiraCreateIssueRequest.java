package io.getint.recruitment_task.model;

import lombok.*;

@Data
@AllArgsConstructor
public class JiraCreateIssueRequest {

  private Fields fields;

  @Data
  @Builder
  public static class Fields {

    private String summary;
    private String description;
    private IssueType issuetype;
    private Priority priority;
    private Project project;
  }


  @Data
  @AllArgsConstructor(staticName = "of")
  public static class Priority {
    private String name;
  }

  @Data
  @AllArgsConstructor(staticName = "of")
  public static class IssueType {
    private String name;
  }

  @Data
  @AllArgsConstructor(staticName = "of")
  public static class Project {
    private String key;
  }
}
