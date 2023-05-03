![Maven Central](https://img.shields.io/maven-central/v/com.fathzer/jdbbackup-core)
![License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jdbbackup_jdbbackup-core&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jdbbackup_jdbbackup-core)
[![javadoc](https://javadoc.io/badge2/com.fathzer/jdbbackup-core/javadoc.svg)](https://javadoc.io/doc/com.fathzer/jdbbackup-core)

# jdbbackup-sftp
A [JDBBackup](https://github.com/jdbbackup/jdbbackup-core) destination manager that saves backups to a sftp server.

## Destination format
sftp://*user*:*pwd*\[@*host*\[:*port*\]\]\[/*path*\]/*filename*

Default values of optional parts:
- *host*: 127.0.0.1.  
- *port*: 22.
- *path*: user home directory.

Please note that user and password must be UTF-8 URL encoded.

All the patterns detailed [here](https://github.com/jdbbackup/jdbbackup-core) are supported.
