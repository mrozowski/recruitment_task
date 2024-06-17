package io.getint.recruitment_task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JiraCreateCommentRequest {

  private String body;
  private JiraCommentVisibility visibility;
}
