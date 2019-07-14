 # TotalBackup

 ![](./resources/TB_logo.png)

TotalBackup is a powerful tool to create backups. It offers a wide range of configuration options from hardlink backups to automated backups with complex timing rules.


## Usage
TotalBackup comes as Java executable (jar) including a nice JavaFX based GUI.
```
java -jar totalbackup.jar
```

To force the usage of the Qt-Tray:
```
java -jar totalbackup.jar force_qt
```

## Requirements
- Java >=8 (unfortunately, using openJDK can cause problems, which is related to the JavaFX prerequisite)
- JavaFX
- Gson >= 2.8.5 (included in the jar)

Note: To be able to minimize TotalBackup into the system tray, Javas SystemTray has to be supported. Alternatively the included Qt/C++ based tray-app can be used.

## Known Bugs
- Java (AWT) Tray is not able to show notifications using KDE
- Java (AWT) Tray is not working at all under Gnome 3
