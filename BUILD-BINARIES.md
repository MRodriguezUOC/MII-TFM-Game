https://libgdx.com/wiki/deployment/deploying-your-application
Para Windows/Linux/Mac:
./gradlew lwjgl3:dist

El archivo se genera en lwjgl3/build/libs
Pero eso es un jar, en la siguiente guía muestra otras opciones:
https://libgdx.com/wiki/deployment/bundling-a-jre
Mac M1 lwjgl3:packageMacM1
Mac OSX lwjgl3:packageMacX64
Linux lwjgl3:packageLinuxX64
Windows lwjgl3:packageWinX64
This creates a zip file in lwjgl3/build/construo/dist

Para Android:
./gradlew android:assembleRelease (necesita firmarse)
./gradlew android:assembleDebug


https://libgdx.com/dev/natives/
To compile the macOS and iOS natives, run:
./gradlew jnigen jnigenBuildMacOsX64 jnigenBuildMacOsXARM64 jnigenBuildIOS

To compile the Windows, Linux and Android natives, run:
./gradlew jnigen jnigenBuild

You can also run each individual platforms tasks to build natives for just that platform, for example just the Android natives, just run
./gradlew jnigen jnigenBuildAndroid 

You can get the list of available tasks by running 
./gradlew tasks.