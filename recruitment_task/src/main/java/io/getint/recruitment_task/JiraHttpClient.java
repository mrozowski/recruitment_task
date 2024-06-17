package io.getint.recruitment_task;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.exception.JiraApiConnectionException;
import io.getint.recruitment_task.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JiraHttpClient {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String searchTemplateUrl =
      "rest/api/2/search?jql=project=%s&fields=id,key,summary,description,priority,status,issuetype," +
          "project,assignee&maxResults=5";
  private static final String transitionIssueTemplateUrl = "/rest/api/2/issue/%s/transitions";
  private static final String commentsTemplateUrl = "/rest/api/2/issue/%s/comment";
  private static final String createIssueUrl = "rest/api/2/issue";

  private final String baseUrl;
  private final String user;
  private final String secret;

  List<JiraIssueResponse> fetchIssuesByProjectKey(String projectKey) {
    String searchStringQuery = String.format(searchTemplateUrl, projectKey);
    HttpGet httpRequest = new HttpGet(baseUrl + searchStringQuery);
    setBasicAuthentication(httpRequest);

    System.out.println("Fetching tickets from project: " + projectKey);
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpRequest)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        var responseBody = EntityUtils.toString(response.getEntity());
        var jiraFetchIssuesResponse = OBJECT_MAPPER.readValue(responseBody, JiraFetchIssuesResponse.class);
        return jiraFetchIssuesResponse.getIssues();
      } else {
        displayError(response, statusCode);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(baseUrl + searchStringQuery, ex);
    }
    return List.of();
  }

  Optional<String> createIssue(JiraCreateIssueRequest createIssueRequest) {
    String jsonRequestBody = OBJECT_MAPPER.valueToTree(createIssueRequest).toPrettyString();
    String createBulkIssuesUrl = baseUrl + createIssueUrl;
    HttpPost httpPost = createHttpPostRequest(jsonRequestBody, createBulkIssuesUrl);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpPost)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 201) {
        var responseBody = EntityUtils.toString(response.getEntity());
        var key = OBJECT_MAPPER.readTree(responseBody).get("key").asText();
        System.out.println("Tickets created successfully: " + key);
        return Optional.of(key);
      } else {
        displayError(response, statusCode);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(createBulkIssuesUrl, ex);
    }
    return Optional.empty();
  }

  void updateIssue(JiraUpdateStatusRequest updateIssueRequest, String issueKey) {
    String updateIssueUrl = String.format(transitionIssueTemplateUrl, issueKey);
    String jsonRequestBody = OBJECT_MAPPER.valueToTree(updateIssueRequest).toPrettyString();
    HttpPost httpPost = createHttpPostRequest(jsonRequestBody, baseUrl + updateIssueUrl);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpPost)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 204) {
        System.out.printf("Ticket [%s] status updated successfully%n", issueKey);
      } else {
        displayError(response, statusCode);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(baseUrl + updateIssueUrl, ex);
    }
  }

  List<JiraCommentResponse> fetchComments(String issueKey) {
    String updateIssueUrl = String.format(commentsTemplateUrl, issueKey);
    HttpGet httpRequest = new HttpGet(baseUrl + updateIssueUrl);
    setBasicAuthentication(httpRequest);

    System.out.printf("Fetching comments from ticket [%s]%n", issueKey);
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpRequest)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        var responseBody = EntityUtils.toString(response.getEntity());
        var jiraFetchCommentsRequest = OBJECT_MAPPER.readValue(responseBody, JiraFetchCommentsRequest.class);
        System.out.printf("Found [%s] comments in [%s]%n", jiraFetchCommentsRequest.comments.size(), issueKey);
        return jiraFetchCommentsRequest.comments;
      } else {
        displayError(response, statusCode);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(baseUrl + updateIssueUrl, ex);
    }
    return List.of();
  }

  void addComment(JiraCreateCommentRequest comment, String issueKey) {
    String jsonRequestBody = OBJECT_MAPPER.valueToTree(comment).toPrettyString();
    String addCommentUrl = String.format(commentsTemplateUrl, issueKey);
    HttpPost httpPost = createHttpPostRequest(jsonRequestBody, baseUrl + addCommentUrl);
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpPost)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 201) {
        System.out.println("Comment for ticket [" + issueKey + "] added successfully");
      } else {
        displayError(response, statusCode);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(baseUrl + addCommentUrl, ex);
    }
  }

  private HttpPost createHttpPostRequest(String requestBody, String url) {
    HttpPost httpPost = new HttpPost(url);
    setBasicAuthentication(httpPost);
    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

    httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
    return httpPost;
  }

  private void setBasicAuthentication(HttpRequestBase request) {
    String auth = user + ":" + secret;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
    String authHeader = "Basic " + new String(encodedAuth);

    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
  }

  private static void displayError(CloseableHttpResponse response, int statusCode) throws IOException {
    String responseBody = EntityUtils.toString(response.getEntity());
    System.out.println("Error code: " + statusCode);
    if (!responseBody.isEmpty()){
      JiraErrorResponse errorResponse = OBJECT_MAPPER.readValue(responseBody, JiraErrorResponse.class);
      errorResponse.getErrorMessages().forEach(message -> System.out.println("Error message: " + message));
      errorResponse.getErrors().forEach((name, value) -> System.out.printf("Error message: %s - %s%n", name, value));
    }
  }
}
