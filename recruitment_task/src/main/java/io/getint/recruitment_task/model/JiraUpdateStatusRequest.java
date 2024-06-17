package io.getint.recruitment_task.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraUpdateStatusRequest {

  private Transition transition;

  public static JiraUpdateStatusRequest withStatusName(String statusName) {
    return new JiraUpdateStatusRequest(Transition.of(mapToTransitionId(statusName)));
  }

  private static String mapToTransitionId(String statusName) {
    switch (statusName) {
      case "In Progress":
        return "21";
      case "Done":
        return "31";
      default:
        return "11";
    }
  }

  @Data
  @AllArgsConstructor(staticName = "of")
  public static class Transition {
    private String id;
  }
}
