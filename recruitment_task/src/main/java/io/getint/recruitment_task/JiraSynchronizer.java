package io.getint.recruitment_task;

import io.getint.recruitment_task.model.JiraCreateIssueRequest;
import io.getint.recruitment_task.model.JiraIssueResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JiraSynchronizer {

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
    public void moveTasksToOtherProject() throws Exception {
        List<JiraIssueResponse> jiraIssues = jiraHttpClient.fetchIssuesByProjectKey(sourceProject);

        List<JiraCreateIssueRequest> issuesRequest = createIssuesRequest(jiraIssues, targetProject);
        jiraHttpClient.createIssues(issuesRequest);
    }

    List<JiraCreateIssueRequest> createIssuesRequest(List<JiraIssueResponse> jiraIssues, String targetProject){
        return jiraIssues.stream()
            .map(e -> maptoJiraCreateIssueRequest(targetProject, e))
            .collect(Collectors.toList());
    }

    private static JiraCreateIssueRequest maptoJiraCreateIssueRequest(String targetProject, JiraIssueResponse issue) {
        return new JiraCreateIssueRequest(
            JiraCreateIssueRequest.Fields.builder()
                .summary(issue.fields.summary)
                .description(issue.fields.description)
                .issuetype(JiraCreateIssueRequest.IssueType.of(issue.fields.issueType.name))
                .project(JiraCreateIssueRequest.Project.of(targetProject))
                .priority(JiraCreateIssueRequest.Priority.of(issue.fields.priority.name))
                .build()
        );
    }
}
