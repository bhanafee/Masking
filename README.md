# Sensitive data masking

Applications that incorporate sensitive data such as Social Security and credit card numbers may inadvertently disclose
that data into locations such as logs and traces, or through careless UI usage. Often, the disclosure occurs though
an implicit `toString()` invocation. One way to mitigate this risk is to ensure that the objects holding the
sensitive data do not reveal it by default.