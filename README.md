# Sonar Quality Gate Breaker

Verify Quality gate after SonarQube pass.

Command line

```cmd
java -jar sonar-gate-breaker-all-1.0.0-SNAPSHOT.jar
```

Exit

| key | description |
| --- | --- |
| 0 | Quality gate is SUCCESS or WARN |
| -1 | Quality gare is ERROR or WARN |

Options

| key | type | description |
| --- | --- | --- |
| -t,--reportTaskPath | text | path of report-task.txt |
| -u,--username | text | Username for SonarQube |
| -p,--password | text | Password for SonarQube, if use token not fill |
| -r,--retry | integer | Number of retry (wait success analys) default 50 |
| -s,--sleep | long | Sleep between retry default 1000 millisecond |
| -f,--failOnWarn | none | Fail on warning default false |
| -n,--noprint | none |  No print report |
