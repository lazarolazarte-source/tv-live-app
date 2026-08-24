#!/bin/sh
# Genera la keystore para firmar los APK de release de TV Live.
# Ejecutalo UNA sola vez, guardá el archivo .jks y las contraseñas en un
# lugar seguro (NO lo subas a un repo público) y completá keystore.properties.

keytool -genkeypair -v \
  -keystore tvlive-release.jks \
  -alias tvlive \
  -keyalg RSA -keysize 2048 -validity 10000

echo ""
echo "Listo. Ahora copiá keystore.properties.example a keystore.properties"
echo "y completá los datos con la contraseña que usaste arriba."
