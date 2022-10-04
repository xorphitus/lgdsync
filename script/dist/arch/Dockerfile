FROM archlinux:latest

RUN pacman -Sy && \
    pacman -S --noconfirm --needed base-devel jre-openjdk

RUN useradd -r -s /bin/false xorphitus && \
    mkdir /app && \
    chown xorphitus /app

WORKDIR /app

USER xorphitus
ADD PKGBUILD .
RUN makepkg

USER root
RUN pacman -U --noconfirm lgdsync-*.pkg.tar.zst

USER xorphitus
RUN set -e && \
    lgdsync --help | grep Usage
