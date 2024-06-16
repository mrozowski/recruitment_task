package io.getint.recruitment_task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.getint.recruitment_task.exception.JiraApiConnectionException;
import io.getint.recruitment_task.model.JiraCreateIssueRequest;
import io.getint.recruitment_task.model.JiraFetchIssuesResponse;
import io.getint.recruitment_task.model.JiraIssueResponse;
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

@RequiredArgsConstructor
public class JiraHttpClient {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String searchTemplate =
      "rest/api/2/search?jql=project=%s&fields=id,key,summary,description,priority,status,issuetype," +
          "project&maxResults=1";
  private static final String createBulkIssues = "rest/api/2/issue/bulk";

  private final String baseUrl;
  private final String user;
  private final String secret;

  List<JiraIssueResponse> fetchIssuesByProjectKey(String projectKey) {
    var searchStringQuery = String.format(searchTemplate, projectKey);
    var httpRequest = new HttpGet(baseUrl + searchStringQuery);
    setBasicAuthentication(httpRequest);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpRequest)) {
      var statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        var responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        var jiraFetchIssuesResponse = OBJECT_MAPPER.readValue(responseBody, JiraFetchIssuesResponse.class);
        return jiraFetchIssuesResponse.issues;
      } else {
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("Error: " + statusCode);
        System.out.println("Response Body: " + responseBody);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(baseUrl + searchStringQuery, ex);
    }
    return List.of();
  }

  public void createIssues(List<JiraCreateIssueRequest> issuesRequest) {
    String jsonRequestBody = createIssueRequestJson(issuesRequest);
    String createBulkIssuesUrl = baseUrl + createBulkIssues;
    HttpPost httpPost = createHttpPostRequest(jsonRequestBody, createBulkIssuesUrl);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpPost)) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 201) {
        System.out.println("Tickets created successfully");
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("Response Body: " + responseBody);
      } else {
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("Error: " + statusCode);
        System.out.println("Response Body: " + responseBody);
      }
    } catch (IOException ex) {
      throw new JiraApiConnectionException(createBulkIssuesUrl, ex);
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

  private static String createIssueRequestJson(List<JiraCreateIssueRequest> issuesRequest) {
    ObjectNode payload = JsonNodeFactory.instance.objectNode();
    ArrayNode issueUpdates = payload.putArray("issueUpdates");
    for (var issue : issuesRequest) {
      issueUpdates.add(OBJECT_MAPPER.valueToTree(issue));
    }
    return payload.toPrettyString();
  }

  private void setBasicAuthentication(HttpRequestBase request) {
    // Encode credentials for basic auth
    String auth = user + ":" + secret;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
    String authHeader = "Basic " + new String(encodedAuth);

    // Set the Authorization header
    request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
  }
}
