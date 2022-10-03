# lgdsync
![](https://github.com/xorphitus/lgdsync/workflows/CI/badge.svg)

This is an unidirectional file sync application to Google Drive for multi-platform. It backups your files which are placed in a specified directory immediately when you change them.

## Installation
### Requirement
Lgdsync requires Java to run.

Author's environment:

* Java - 18.0.2 OpenJDK

### Download
See the GitHub release page and download a jar file.

### Google API Setting

You have to acquire a credentials JSON file from Google APIs console and place it in the `~/.config/lgdsync/${profile_name}` directory.

1. Create a project from Google APIs console
1. Select the project which you created
1. Move to "APIs and Services"
1. Move to "Library" and enable Google Drive API
1. Move to "OAuth concent screen" and select Internal user type
1. "Credentials" -> "Create Credentials" -> "OAuth client ID"
1. Select application type "Desktop app"
1. Download a client secret JSON file and rename it as `credentials.json`

## Usage

Type the following command to start sync your files to Google Drive.

    $ java -jar /path/to/lgdsync.jar ${from} ${to}

* `${from}`
  * Specify the path which you want to sync
* `${to}`
  * Specify the folder name in Google Drive which you want to use to the backup
  * You don't have to create this folder before start lgdsync. Lgdsync creates the folder automatically

### Profile

You can pass a profile name with `-p` or `--profile` option. It enables you to use more than one Google account. If you don't pass the option, the profile name will automatically be `default`.

### How to Omit `java -jar`

You can omit `java -jar` by embedding shebang. See the "Build by Yourself" section to get an executable or embed shebang manually by following what the build script does.

## Build by Yourself

Lgdsync requires Clojure CLI to build.

Author's environment:

* Clojure CLI - 1.11.1

    $ ./build.sh

Then, you can get `./target/lgdsync` as an executable file. It's available for Linux and macOS.
If you are a Windows user, `.target/lgdsync-${version}-standalone.jar` is available.

## License
Copyright © 2020 xorphitus

See the LICENSE file.
