# Maintainer: xorphitus <xorphitus [@] fastmail [dot] com>

pkgname=lgdsync
pkgver={{ version }}
pkgrel=1
pkgdesc='Unidirectional and lightweight Google Drive file synchronization'
arch=('x86_64')
url='https://github.com/xorphitus/lgdsync'
license=('EPL')
depends=('java-runtime>=8')

source_x86_64=("https://github.com/xorphitus/${pkgname}/releases/download/v${pkgver}/${pkgname}-v${pkgver}-standalone.jar")

sha256sums_x86_64=('{{ sha256 }}')

package() {
  jar="${pkgname}-v${pkgver}-standalone.jar"
  install -Dm755 "${srcdir}/${jar}" "${pkgdir}/opt/lgdsync/${jar}"

  mkdir -p "${srcdir}/bin"
  echo '#!/usr/bin/env bash' > "${srcdir}/bin/lgdsync"
  echo "java -jar /opt/lgdsync/${jar}" '"$@"' >> "${srcdir}/bin/lgdsync"

  install -Dm755 "${srcdir}/bin/lgdsync" "${pkgdir}/usr/bin/lgdsync"
}
