package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Data
@AllArgsConstructor
public class JiraCreateIssueRequest {

  private Fields fields;

  @Data
  @Builder
  @JsonInclude(Include.NON_NULL)
  public static class Fields {

    private String summary;
    private String description;
    private IssueType issuetype;
    private Priority priority;
    private Project project;
    private Assignee assignee;
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

  @Data
  @AllArgsConstructor(staticName = "of")
  public static class Assignee {
    private String id;
  }
}
