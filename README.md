# TV Live — App Android (Kotlin + Jetpack Compose)

App moderna y configurable para ver canales de **YouTube en vivo oficiales** y,
opcionalmente, **listas M3U/IPTV propias** que vos agregues.

## Qué incluye
- 🏠 **Home**: grid de canales con logo, buscador, filtro por categoría/país y favoritos.
- ▶️ **YouTube en vivo**: reproducidos con el reproductor embebido oficial de YouTube
  (`youtube.com/embed/live_stream?channel=...`). No se descarga ni redistribuye contenido.
- 📡 **Listas M3U externas**: pantalla para agregar la URL de tus propias listas
  M3U/M3U8 (parseo de `#EXTINF`, logos, categorías) y reproducción vía ExoPlayer/Media3 (HLS).
- 🌗 Tema claro/oscuro Material 3, tarjetas animadas, diseño moderno.
- 💾 Persistencia local (DataStore) de listas agregadas y favoritos — nada se envía a un servidor propio.

## Cómo compilarla

### Opción A — Android Studio (local)
1. Abrí la carpeta `TVLiveApp` con **Android Studio** (Koala o superior).
2. Dejá que Gradle sincronice (usa Kotlin 1.9.24, AGP 8.5.2, compileSdk 34, minSdk 24).
3. `Run ▶` sobre un emulador o dispositivo físico, o `Build → Build APK(s)` para generar el archivo instalable.

### Opción B — GitHub Actions (sin instalar nada, te da un APK descargable)
El repo ya trae dos workflows en `.github/workflows/`:

- **`build.yml`**: compila un **APK de debug** automáticamente en cada push a
  `main`. Andá a la pestaña **Actions** del repo → abrí la ejecución →
  descargá el artifact `TVLive-debug-apk`. Sirve para probar rápido, pero
  no está firmado para distribución.
- **`build-release.yml`**: compila un **APK de release firmado**, listo para
  compartir o subir a una tienda. Se dispara manualmente (`Actions →
  Build Signed Release APK → Run workflow`) y requiere que configures antes
  la firma (ver siguiente sección).

## Firmar la app (release)

Un APK de release necesita estar firmado con tu propia keystore. Pasos:

1. **Generá la keystore una sola vez** (necesitás tener el JDK instalado, trae el comando `keytool`):
   ```
   ./generate-keystore.sh
   ```
   Te va a pedir una contraseña y algunos datos (nombre, organización, etc.).
   Esto crea `tvlive-release.jks` — **guardalo en un lugar seguro, si lo
   perdés no vas a poder actualizar la app con la misma firma nunca más**.

2. **Para compilar localmente en Android Studio:**
   - Copiá `keystore.properties.example` a `keystore.properties`.
   - Completá `storePassword`, `keyAlias` y `keyPassword` con los datos que usaste al generar la keystore.
   - `keystore.properties` y `*.jks` ya están en `.gitignore`: nunca se suben al repo.
   - `Build → Generate Signed Bundle / APK` o simplemente `./gradlew assembleRelease`.

3. **Para compilar el release firmado con GitHub Actions** (sin tener Android Studio):
   - Convertí la keystore a base64: `base64 -i tvlive-release.jks -o keystore.b64` (Mac/Linux) o el equivalente en Windows.
   - En tu repo de GitHub: `Settings → Secrets and variables → Actions → New repository secret`, y creá:
     - `KEYSTORE_BASE64` (contenido del archivo `.b64`)
     - `KEYSTORE_PASSWORD`
     - `KEY_ALIAS` (por ejemplo `tvlive`)
     - `KEY_PASSWORD`
   - Andá a la pestaña **Actions → Build Signed Release APK → Run workflow**.
   - Al terminar, descargá el artifact `TVLive-release-apk`: ese `.apk` ya está firmado y listo para instalar o compartir.

## Configuración importante antes de publicarla

### Canales de YouTube integrados
En `data/BuiltInChannels.kt` están los canales por defecto, con los
`channelId` de sus canales oficiales de YouTube: **Canal 26, TN (Todo
Noticias), C5N** (Argentina), **24 Horas TVN** (Chile), además de DW News,
France 24 Español y NASA Live. GloboNews (Brasil) quedó como
`REEMPLAZAR_CHANNEL_ID` porque no se pudo confirmar con certeza cuál es el
canal oficial actual — conviene verificarlo en `youtube.com/@globonews`
antes de usarlo. Te recomiendo igual volver a confirmar todos los IDs antes
de publicar la app, ya que un canal puede cambiar de handle/ID con el
tiempo. Para una versión más robusta, se recomienda resolver el canal en
vivo dinámicamente contra la **YouTube Data API v3** (`search.list` con
`eventType=live`) en vez de depender de una URL fija.

### Listas M3U
La app **no incluye ni recomienda ninguna lista M3U de terceros**. Es un
reproductor genérico (igual que VLC o Kodi): el usuario carga la URL de su
propia lista (por ejemplo, un servicio IPTV que ya tenga contratado, o listas
públicas 100% libres de derechos) y la app la parsea y reproduce. Quien use
la app es responsable del contenido y de los derechos de las listas que
agregue.

### Series y películas
No se incluyó ninguna fuente de series/películas de terceros porque, en la
gran mayoría de los casos, ese contenido está protegido por copyright y
distribuirlo sin autorización es ilegal. Si en el futuro querés sumar
contenido on-demand, la vía correcta es integrar APIs oficiales de
plataformas con las que tengas acuerdo/licencia (por ejemplo, contenido
propio, YouTube oficial, o proveedores de streaming con SDK público).

## Estructura del proyecto
```
app/src/main/java/com/tulaza/tvlive/
├── MainActivity.kt              # Navegación (Home / Player / Add M3U / Settings)
├── data/
│   ├── Models.kt                 # Channel, M3uPlaylist
│   ├── BuiltInChannels.kt        # Canales YouTube por defecto
│   ├── M3uParser.kt              # Parser genérico de listas M3U
│   └── PreferencesManager.kt     # DataStore: listas, favoritos, tema
├── viewmodel/MainViewModel.kt
├── player/
│   ├── YoutubePlayerView.kt      # WebView con reproductor oficial de YouTube
│   └── ExoStreamPlayer.kt        # ExoPlayer/Media3 para streams M3U/HLS
└── ui/
    ├── theme/                    # Colores, tipografía, tema claro/oscuro
    ├── components/ChannelCard.kt
    └── screens/                  # HomeScreen, PlayerScreen, AddPlaylistScreen, SettingsScreen
```
