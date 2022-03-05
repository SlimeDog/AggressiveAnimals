package dev.ratas.aggressiveanimals.config;

import java.util.List;

public interface IConfigLoadIssueResolver {

    String getLoadType();

    boolean hasIssues();

    void logIssue(String name, String description);

    void logIssue(String name, String description, Throwable throwable);

    void logIssue(IConfigLoadIssueResolver.IssueDescription issue);

    List<IConfigLoadIssueResolver.IssueDescription> getIssues();

    public record IssueDescription(String name, String description, Throwable throwable) {

        public boolean hasThrowable() {
            return this.throwable != null;
        }

    }

}
