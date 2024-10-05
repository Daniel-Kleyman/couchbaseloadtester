package kleyman;

import kleyman.testrunner.CouchbaseTestRunner;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CouchbaseTestRunner testRunner = new CouchbaseTestRunner();
        testRunner.runTests();
    }
}