package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueResponse {

  private String id = "";
  private String key = "";
  private Fields fields;

  public String getStatusName(){
    return fields.status.getName();
  }

  public Optional<String> getAssigneeId(){
    return Optional.ofNullable(fields.assignee).map(assignee -> assignee.accountId);
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Fields {
    private String summary;
    private String description;

    @JsonProperty("issuetype")
    private IssueType issueType;
    private Priority priority;
    private Status status;
    private Project project;
    private Assignee assignee;

    @Override
    public String toString() {
      return "Fields{" +
          "summary='" + summary + '\'' +
          ", description='" + description + '\'' +
          ", issueType='" + issueType.name + '\'' +
          ", priority='" + priority.name + '\'' +
          ", status='" + status.name + '\'' +
          ", project='" + project.key + '\'' +
          '}';
    }
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Status {
    private String name;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Priority {
    private String name;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class IssueType {
    private String name;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Project {
    private String key;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Assignee {
    private String accountId;
  }

  @Override
  public String toString() {
    return "JiraIssue{" +
        "id='" + id + '\'' +
        ", key='" + key + '\'' +
        ", fields=" + fields +
        '}';
  }
}
