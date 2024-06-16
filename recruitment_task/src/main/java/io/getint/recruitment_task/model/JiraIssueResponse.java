package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueResponse {

  public String id = "";
  public String key = "";
  public Fields fields;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Fields {
    public String summary;
    public String description;

    @JsonProperty("issuetype")
    public IssueType issueType;
    public Priority priority;
    public Status status;
    public Project project;

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

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Status {
    public String name;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Priority {
    public String name;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class IssueType {
    public String name;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Project {
    public String key;
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
