# lgdsync
This is an unidirectional file sync application to Google Drive. It backups your files which are placed in a specified directory immediately when you change them.

## Requirement
Lgdsync requires Java to run, and [Leiningen](https://github.com/technomancy/leiningen) to build.

Author's environment:

* Java - 14.0.1 OpenJDK
* Leiningen - 2.9.33

## Build

    $ lein uberjar

Then, you can get `./target/uberjar/lgdsync-*-standalone.jar`.

## Usage
First, download `credentials.json` from Google API console and place it in the `~/.config/lgdsync/default` directory.

Then, type the following command to start sync your files to Google Drive.

    $ java -jar /path/to/lgdsync-${version}-standalone.jar ${from} ${to}

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
* Use inotify to watch file changes
  * Current watch mechanism is not perfect. It may miss file changes

## License
Copyright © 2020 xorphitus

See the LICENSE file.
