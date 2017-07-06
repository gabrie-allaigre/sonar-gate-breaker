# Sonar Quality Gate Breaker

Verify Quality gate after SonarQube pass and exit if Quality Gate is error.

**Download main jar** https://github.com/gabrie-allaigre/sonar-gate-breaker/releases/download/1.0.1/sonar-gate-breaker-all-1.0.1.jar

## Command line

```cmd
java -jar sonar-gate-breaker-all-1.0.1.jar
```

## With Gitlab-CI

Example with maven project

Create folder `deploy` and copy `sonar-gate-breaker-all-1.0.1.jar`

```yaml
test_sonar_job:
  stage: test
  only:
    - master
  script:
    - mvn --batch-mode verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.0.1:sonar -Dsonar.host.url=$SONAR_URL -Dsonar.login=$SONAR_LOGIN -PCI
    - java -jar deploy/sonar-gate-breaker-all-1.0.1.jar -u $SONAR_LOGIN
```

## Exit code

| key | description |
| --- | --- |
| 0 | Quality gate is SUCCESS or WARN |
| -1 | Quality gare is ERROR or WARN |

## Options

| key | type | description |
| --- | --- | --- |
| -t,--reportTaskPath | text | path of report-task.txt |
| -u,--username | text | Username for SonarQube |
| -p,--password | text | Password for SonarQube, if use token not fill |
| -r,--retry | integer | Number of retry (wait success analys) default 50 |
| -s,--sleep | long | Sleep between retry default 1000 millisecond |
| -f,--failOnWarn | none | Fail on warning default false |
| -n,--noprint | none |  No print report |
