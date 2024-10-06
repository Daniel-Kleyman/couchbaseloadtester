package kleyman;

import kleyman.testrunner.CouchbaseTestRunner;

public class Main {
    public static void main(String[] args) {
        CouchbaseTestRunner testRunner = new CouchbaseTestRunner();
        testRunner.runTests();
    }
}