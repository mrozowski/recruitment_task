package io.getint.recruitment_task;

import org.junit.Test;

public class JiraSynchronizerTests {

    private static final String BASE_URL = "https://xxxxxxxx.atlassian.net/";
    private static final String SOURCE_PROJECT = "KAN";
    private static final String TARGET_PROJECT = "REC";
    private static final String USER_EMAIL = "xxxxxx@xxx.com";
    private static final String TOKEN = "xxxxxxxx";

    @Test
    public void shouldSyncTasks() throws Exception {
        var client = new JiraHttpClient(BASE_URL, USER_EMAIL, TOKEN);
        var jiraSynchronizer = new JiraSynchronizer(client, SOURCE_PROJECT, TARGET_PROJECT);

        jiraSynchronizer.moveTasksToOtherProject();
    }
}
