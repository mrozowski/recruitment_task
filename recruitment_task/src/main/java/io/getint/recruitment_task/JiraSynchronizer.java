package io.getint.recruitment_task;

import io.getint.recruitment_task.model.*;
import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class JiraSynchronizer {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
  private static final DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

  private final JiraHttpClient jiraHttpClient;
  private final String sourceProject;
  private final String targetProject;

  /**
   * Search for 5 tickets in one project, and move them
   * to the other project within same Jira instance.
   * When moving tickets, please move following fields:
   * - summary (title)
   * - description
   * - priority
   * Bonus points for syncing comments.
   */
  public void moveTasksToOtherProject() {
    List<JiraIssueResponse> jiraIssues = jiraHttpClient.fetchIssuesByProjectKey(sourceProject);
    if(jiraIssues.isEmpty()){
      System.out.printf("No tickets found in [%s] project%n", sourceProject);
      return;
    }

    System.out.printf("Found %d tickets. Start moving tickets to [%s] project%n", jiraIssues.size(), targetProject);
    for (var jiraIssue : jiraIssues) {
      JiraCreateIssueRequest createIssueRequest = maptoJiraCreateIssueRequest(targetProject, jiraIssue);
      String status = jiraIssue.getStatusName();
      List<JiraCommentResponse> comments = jiraHttpClient.fetchComments(jiraIssue.getKey());

      System.out.printf("Recreating ticket [%s] in [%s] project%n", jiraIssue.getKey(), targetProject);
      jiraHttpClient.createIssue(createIssueRequest)
          .ifPresent(issueKey -> updateIssueStatusAndComments(issueKey, status, comments));
    }
    System.out.println("Moving tickets completed successfully");
  }

  private void updateIssueStatusAndComments(String issueKey, String statusName, List<JiraCommentResponse> comments) {
    if (!statusName.equals("To Do")) {
      jiraHttpClient.updateIssue(JiraUpdateStatusRequest.withStatusName(statusName), issueKey);
    }
    comments.forEach(comment -> addComment(comment, issueKey));
  }

  private void addComment(JiraCommentResponse comment, String issueKey) {
    var author = comment.getAuthor().getDisplayName();
    var createdDate = OffsetDateTime.parse(comment.getCreated(), FORMATTER);
    var body = String.format("%s _<- (%s at %s)_", comment.getBody(), author, createdDate.format(SIMPLE_DATE_TIME_FORMATTER));
    var commentRequest = new JiraCreateCommentRequest(body, comment.getVisibility());
    jiraHttpClient.addComment(commentRequest, issueKey);
  }

  private static JiraCreateIssueRequest maptoJiraCreateIssueRequest(String targetProject, JiraIssueResponse issue) {
    var fieldsBuilder = JiraCreateIssueRequest.Fields.builder()
        .summary(issue.getFields().getSummary())
        .description(issue.getFields().getDescription())
        .issuetype(JiraCreateIssueRequest.IssueType.of(issue.getFields().getIssueType().getName()))
        .project(JiraCreateIssueRequest.Project.of(targetProject))
        .priority(JiraCreateIssueRequest.Priority.of(issue.getFields().getPriority().getName()));

    issue.getAssigneeId()
        .ifPresent(assigneeId -> fieldsBuilder.assignee(JiraCreateIssueRequest.Assignee.of(assigneeId)));
    return new JiraCreateIssueRequest(fieldsBuilder.build());
  }
}
