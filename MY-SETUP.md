# My setup: Sony KD-65X82L

A personal configuration of [CleanYourTV](https://github.com/Gathin23/cleanyourtv) for a **Sony BRAVIA KD-65X82L** (2023, Google TV 12, MediaTek). This branch is a fork of the original project, which was created by Gathin23 and is MIT-licensed. Everything here is built on top of it.

- **TV:** Sony BRAVIA KD-65X82L, device `BRAVIA_VH22`, India
- **Build:** `BRAVIA_VH22_M_PA-user 12 STT2.230505.001.S136`
- **Platform:** Google TV 12 (API 31)

## What I changed

| Change | Where |
|---|---|
| Debloat list for this TV (copied from `examples/sony-kd-65x82l`, with the setup wizard split into its own file) | `my-debloat.txt`, `setupwraith.txt` |
| 6-app dock: Netflix, Prime Video, YouTube, JioHotstar, ZEE5, SonyLIV | `launcher/app/src/main/res/values/config.xml` |
| Sony Live TV app hidden from the grid and used for the Inputs menu | `config.xml` (`com.sony.dtv.tvx`) |
| Remote gear (Settings) button restored | `config.xml` (`settings_key_scancodes` 646) |
| Welcome splash screen on every boot and wake-up | `SplashActivity.java`, `MainActivity.java`, `AndroidManifest.xml` |
| Fonts for the splash | `launcher/app/src/main/assets/fonts/` |

### Dock packages

| App | Package |
|---|---|
| Netflix | `com.netflix.ninja` |
| Prime Video | `com.amazon.amazonvideo.livingroom` |
| YouTube | `com.google.android.youtube.tv` |
| JioHotstar | `in.startv.hotstar` |
| ZEE5 | `com.graymatrix.did` |
| SonyLIV | `com.sonyliv` |

## The welcome splash

A black screen that fades in **Crafted by** (script) over **MITHRAN** (serif capitals), a thin divider and **WELCOME HOME**, for about 3.8 seconds. Any remote key skips it.

- **When it shows:** on the first Home after the TV boots, and on Home after every screen wake-up from standby. `MainActivity` registers a screen-off receiver, and `showSplashOncePerBoot()` starts the splash when it sees a new boot (`Settings.Global.BOOT_COUNT`) or a wake-up.
- **Fonts:** [Great Vibes](https://fonts.google.com/specimen/Great+Vibes) (`script.ttf`) and [Cinzel](https://fonts.google.com/specimen/Cinzel) (`title.ttf`), both under the SIL Open Font License. The licence texts are next to the fonts. If a font file is missing, the splash falls back to system fonts.
- **Change the text or timing:** edit `SplashActivity.java`. The text lines are in `onCreate`, and the duration is the `3800` (milliseconds) passed to `postDelayed`.

## Setup on Windows (PowerShell)

Needs: Python 3, JDK 17 (JDK 23 and newer breaks the Gradle build), Android SDK platform 34 and build-tools 34.0.0, and **ADB debugging** switched on in the TV's developer options.

```powershell
git clone https://github.com/Mithran-81807/cleanyourtv
cd cleanyourtv
git checkout mithran-sony-x82l

py -m venv tools\venv
tools\venv\Scripts\python.exe -m pip install -r tools\requirements.txt

$env:TV_HOST = "<tv ip>"
$env:JAVA_HOME = "<path to JDK 17>"
$env:ANDROID_HOME = "<path to Android SDK>"

# 1. Check the connection
tools\venv\Scripts\python.exe -I tools\tv.py --probe

# 2. Debloat (the setup wizard goes second; it only matters once the launcher replaces Home)
powershell -ExecutionPolicy Bypass -File tools\disable.ps1 -TvHost $env:TV_HOST my-debloat.txt
powershell -ExecutionPolicy Bypass -File tools\disable.ps1 -TvHost $env:TV_HOST setupwraith.txt

# 3. Build and install the launcher
powershell -ExecutionPolicy Bypass -File launcher\build-and-install.ps1 -TvHost $env:TV_HOST -HomeKeyService
```

Before disabling the setup wizard, check that it does not own the saved Wi-Fi network, because disabling it deletes a network it owns:

```powershell
tools\venv\Scripts\python.exe -I tools\tv.py 'dumpsys wifi | grep -o ''cname=[^ ]*'''
```

On this TV every entry was `android.uid.system`, so it was safe.

## Undo

Everything is *disabled*, not uninstalled. The record needed to undo it is written to `~\.cleanyourtv\` before anything changes, so **keep that folder**.

```powershell
powershell -ExecutionPolicy Bypass -File tools\restore.ps1 -TvHost $env:TV_HOST my-debloat.txt
powershell -ExecutionPolicy Bypass -File tools\restore.ps1 -TvHost $env:TV_HOST setupwraith.txt
```

If Home gets stuck, bring back the stock launcher:

```powershell
tools\venv\Scripts\python.exe -I tools\tv.py 'pm enable --user 0 com.google.android.apps.tv.launcherx'
```

## After a firmware update

An update can re-enable packages and bring back the stock launcher. Turn ADB on, then:

```powershell
tools\venv\Scripts\python.exe -I tools\check.py my-debloat.txt --fix
```

The launcher may need to be rebuilt afterwards.

## Notes

- Turn ADB debugging **off** on the TV when you are not changing anything. Network ADB is not encrypted.
- Never commit the recovery files from `~\.cleanyourtv\`, backups, or anything containing your TV's IP address.
- Three Indian preload packages in the original list (`com.amazon.minitv.androidtv.app`, `com.jio.media.jiotvplus`, `com.jio.media.stb.ondemand`) are not installed on this unit, so those lines print "Unknown package". That is harmless.

## Credits

- [CleanYourTV](https://github.com/Gathin23/cleanyourtv) by Gathin23 and contributors (MIT licence): the tools, the launcher and the Sony KD-65X82L list this setup builds on.
- Fonts: Great Vibes and Cinzel, SIL Open Font License.
