package com.talanlabs.sonargatebreaker;

import com.talanlabs.jargs.CmdLineParser;

public class CheckMain {

    private static final String DEFAULT_REPORT_TASK_PATH = "target/sonar/report-task.txt";
    private static final int DEFAULT_RETRY = 50;
    private static final long DEFAULT_SLEEP = 1000 *2;
    private static final boolean DEFAULT_FAIL_ON_WARN = false;

    private static void printUsage() {
        System.out.println("Usage: CheckMain [{-r,--reportTaskPath} a_report]\na_report : Sonar report-task.txt (default target/sonar/report-task.txt)\n");
    }

    public static void main(String[] args) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option reportTaskPathOption = parser.addStringOption('t', "reportTaskPath");
        CmdLineParser.Option usernameOption = parser.addStringOption('u', "username");
        CmdLineParser.Option passwordOption = parser.addStringOption('p', "password");
        CmdLineParser.Option retryOption = parser.addIntegerOption('r', "retry");
        CmdLineParser.Option sleepOption = parser.addLongOption('s', "sleep");
        CmdLineParser.Option failOnWarnOption = parser.addBooleanOption('f', "failOnWarn");

        try {
            parser.parse(args);
        } catch (CmdLineParser.OptionException e) {
            printUsage();
            return;
        }

        Checker checker = new Checker();

        checker.setReportTaskPath((String) parser.getOptionValue(reportTaskPathOption, DEFAULT_REPORT_TASK_PATH));
        checker.setUsername((String) parser.getOptionValue(usernameOption, null));
        checker.setPassword((String) parser.getOptionValue(passwordOption, null));
        checker.setNbRetry((Integer) parser.getOptionValue(retryOption, DEFAULT_RETRY));
        checker.setSleep((Long) parser.getOptionValue(sleepOption, DEFAULT_SLEEP));
        checker.setFailOnWarn((Boolean) parser.getOptionValue(failOnWarnOption, DEFAULT_FAIL_ON_WARN));

        if (!checker.check()) {
            System.exit(-1);
        }
    }
}
