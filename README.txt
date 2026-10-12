BUILD (Minecraft 26.2, Mojang-Mappings, kein Yarn mehr)
1. JDK 25 installieren (Pflicht ab MC 26.1).
2. In diesem Ordner:  gradle wrapper --gradle-version 9.5.1   (einmalig, braucht Gradle)
   Alternativ: Template von https://fabricmc.net/develop/template/ erzeugen und src/ + fabric.mod.json einkopieren.
3. ./gradlew build   (Windows: gradlew.bat build)
4. Ergebnis: build/libs/crystalmacro-1.0.0.jar -> .minecraft/mods
   Benoetigt: Fabric Loader >= 0.19.3 und Fabric API fuer 26.2.

NUTZUNG
Obsidian/Bedrock anvisieren, End Crystal in der Hand, Taste R halten
(aenderbar unter Optionen > Steuerung > "Crystal Macro").

OHNE INSTALLATION (GitHub baut die .jar)
1. Kostenloses Konto auf github.com, dann "New repository" (z.B. crystalmacro).
2. Alle Dateien dieses Ordners hochladen (Add file > Upload files), inkl. dem Ordner .github.
3. Reiter "Actions" > "Build" abwarten (ca. 2-3 Minuten).
4. Auf den fertigen Lauf klicken > unten bei "Artifacts" > crystalmacro-jar herunterladen.
5. Die .jar aus dem ZIP in .minecraft/mods legen (plus Fabric Loader + Fabric API fuer 26.2).
