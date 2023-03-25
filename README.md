# jdbbackup-sftp
A JDBBackup destination manager that saves backups to a sftp server.

## Destination format
sftp://user:pwd\[@host\[:port\]\]\[/path\]/filename

Default values of optional parts:
- *host*: 127.0.0.1.  
- *port*: 22.
- *path*: user home directory.

Please note that user and password must be UTF-8 URL encoded.

All the patterns detailed [here](https://github.com/jdbbackup/jdbbackup-core) are supported.