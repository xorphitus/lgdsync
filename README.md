# lgdsync
![](https://github.com/xorphitus/lgdsync/workflows/CI/badge.svg)

This is an unidirectional file sync application to Google Drive for multi-platform. It backups your files which are placed in a specified directory immediately when you change them.

## Requirement

Lgdsync requires Java to run.

Author's environment:

* Java - 18.0.2 OpenJDK

## Installation

### Arch Linux

You can get a PKGBUILD at the GitHub release page.

```
$ sudo pacman -S --needed base-devel
$ cd /path/to/where_you_placed_PKGBUILD
$ makepkg -si
```

It enables you to use `lgdsync` command.

### Other Platforms

See the GitHub release page and download a jar file.
You can invoke lgdsync by `java -jar /path/to/lgdsync-standalone.jar` instead of typing `lgdsync`.
There are two ways to omit this tedious typing.

The first one is to make a wrapper script like the following.

```bash
#!/usr/bin/bash
java -jar /path/to/lgdsync-standalone.jar $@
```

The other one is embed shebang (for *nix system).

```
$ echo '#!/usr/bin/java' > lgdsync
$ cat /path/to/lgdsync-standalone.jar >> lgdsync
$ chmod +x lgdsync
```

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

    $ lgdsync ${source} ${destination}

* `${source}`
  * Specify the path which you want to sync
* `${destination}`
  * Specify the folder name in Google Drive which you want to use to the backup
  * You don't have to create this folder before start lgdsync. Lgdsync creates the folder automatically

### Profile

You can pass a profile name with `-p` or `--profile` option. It enables you to use more than one Google account. If you don't pass the option, the profile name will automatically be `default`.

## Build by Yourself

Lgdsync requires Clojure CLI to build.

Author's environment:

* Clojure CLI - 1.11.1

    $ clojure -T:build uber

Then, you can get `.target/lgdsync-${version}-standalone-standalon.jar`.

## License
Copyright © 2020 xorphitus

See the LICENSE file.
