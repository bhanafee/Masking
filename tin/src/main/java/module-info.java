/**
 * Taxpayer Identification Number implementations using the sensitive data framework.
 */
module com.maybeitssquid.tin {
    requires transitive com.maybeitssquid.sensitive;

    exports com.maybeitssquid.tin;
    exports com.maybeitssquid.tin.us;
}
