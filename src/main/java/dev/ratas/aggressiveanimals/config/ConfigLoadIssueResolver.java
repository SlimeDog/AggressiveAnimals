package dev.ratas.aggressiveanimals.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigLoadIssueResolver {
    private static final String LOAD_NAME = "startup";
    private static final String RELOAD_NAME = "reload";
    private final String loadType;
    private final List<IssueDescription> issues = new ArrayList<>();

    public ConfigLoadIssueResolver(String loadType) {
        this.loadType = loadType;
    }

    public String getLoadType() {
        return loadType;
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public void logIssue(String name, String description) {
        logIssue(name, description, null);
    }

    public void logIssue(String name, String description, Throwable throwable) {
        logIssue(new IssueDescription(name, description, throwable));
    }

    public void logIssue(IssueDescription issue) {
        issues.add(issue);
    }

    public List<IssueDescription> getIssues() {
        return issues;
    }

    public String asString() {
        return String.valueOf(issues);
    }

    public static ConfigLoadIssueResolver atLoad() {
        return new ConfigLoadIssueResolver(LOAD_NAME);
    }

    public static ConfigLoadIssueResolver atReload() {
        return new ConfigLoadIssueResolver(RELOAD_NAME);
    }

    public record IssueDescription(String name, String description, Throwable throwable) {

        public boolean hasThrowable() {
            return this.throwable != null;
        }

    }

}
