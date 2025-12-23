/**
 * Provides classes for protecting sensitive data from inadvertent disclosure.
 *
 * <p>This module contains two main packages:
 * <ul>
 *     <li>{@code com.maybeitssquid.sensitive} - Core framework for wrapping and rendering sensitive data</li>
 *     <li>{@code com.maybeitssquid.tin} - Taxpayer Identification Number implementations</li>
 * </ul>
 */
module com.maybeitssquid.sensitive {
    requires org.slf4j;

    exports com.maybeitssquid.sensitive;
    exports com.maybeitssquid.tin;
    exports com.maybeitssquid.tin.us;
}