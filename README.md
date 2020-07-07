# lgdsync
This is an unidirectional file sync application to Google Drive. It backups your files which are placed in a specified directory immediately when you change them.

## Requirement
Lgdsync requires Java to run, and [Leiningen](https://github.com/technomancy/leiningen) to build.

Author's environment:

* Java - 14.0.1 OpenJDK
* Leiningen - 2.9.33

And you have to acquire a credentials JSON file from Google APIs console and place it in the `~/.config/lgdsync/default` directory.

1. Create a project from Google APIs console
1. Select the project which you created
1. Move to "APIs and Services"
1. Move to "Library" and enable Google Drive API
1. Move to "OAuth concent screen" and select Internal user type
1. "Credentials" -> "Create Credentials" -> "OAuth client ID"
1. Select application type "Desktop app"
1. Download a client secret JSON file and rename it as `credentials.json`

## Build

    $ lein uberjar

Then, you can get `./target/uberjar/lgdsync-*-standalone.jar`.

## Usage

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
