# lgdsync
This is an unidirectional file sync application to Google Drive. It backups your files which are placed in a specified directory immediately when you change them.

## Usage
First, download `credentials.json` from Google API console and place it in the `~/.config/lgdsync/default` directory.

Then, type the following command to start sync your files to Google Drive.

    $ java -jar lgdsync-0.1.0-standalone.jar ${from} ${to}

* `${from}`
  * Specify the path which you want to sync
* `${to}`
  * Specify the folder name in Google Drive which you want to use to the backup
  * You don't have to create this folder before start lgdsync. Lgdsync creates the folder automatically

## Todo

* Restore from Google Drive
* Delete files
* Support a directory (nested files)
* Support multiple profiles
  * It enables you to use multiple Google accounts simultaneously
    * e.g. Use your private account and office account

## License
Copyright © 2020 xorphitus

See the LICENSE file.
