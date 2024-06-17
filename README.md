# Intro
Task is about syncing Jira tickets from one project to another within one Jira instance.
Please create Jira instance (its free) where you can create 2 projects that are going to be synchronized.
https://www.atlassian.com/try/cloud/signup

# Task
1. Fork this repository and send link to it to radek@getint.io after task completion
2. Implement `JiraSynchronizer.moveTasksToOtherProject` method. Search for 5 tickets in one project, and move them (recreate them) to other project within same Jira instance.
When syncing tickets please include following fields:
- summary (title)
- description
- priority
Bonus points for syncing status and comments.
3. Please complete task within 7 days from the day you received it
4. Pleaase use provided library (apache) for HTTP communication
  
API endpoints exposed by Jira you can find here:
https://developer.atlassian.com/cloud/jira/software/rest/


## Solution

### How to run
For simplicity, I prepared unit test in `JiraSynchronizerTests` class. 

The test requires providing a few values to connect to your Jira instance.
* `BASE_URL`: Url to your Jira instance
* `SOURCE_PROJECT`: Source project Key name
* `TARGET_PROJECT`: Target project Key name
* `USER_EMAIL`: User email
* `TOKEN`: Generated token for given user. To generate token visit _account_ -> _manage your account_ -> _security_ -> _Api tokens_

The app recreates tickets in target project with following fields:
1. summary
2. description
3. priority
4. assignee
4. status
5. comments



