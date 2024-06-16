package io.getint.recruitment_task.exception;

public class JiraApiConnectionException extends RuntimeException {

  public JiraApiConnectionException(String url, Throwable cause) {
    super(String.format("Connection to Jira API failed: %s", url), cause);
  }
}
