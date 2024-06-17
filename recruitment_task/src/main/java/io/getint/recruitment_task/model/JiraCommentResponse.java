package io.getint.recruitment_task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraCommentResponse {

  private String body = "";
  private String created;
  private JiraCommentVisibility visibility;
  private Author author;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Author {
    private String displayName = "";
  }
}
