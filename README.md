# TotalBackup

 ![](./resources/TB_logo.png)

TotalBackup is a powerful tool to create backups. It offers a wide range of configuration options from hardlink backups to automated backups with complex timing rules.

TotalBackup is released under GPLv3.

## Disclaimer
This software is currently under development and may have serious bugs. Use at your own risk.

## Features
- Cross-platform support
- Run TotalBackup in the background while it is accessible via a tray icon (see known bugs).
- Create hardlink backups to minimize space consumption for files which have not changed since the last backup
- Accurately configuration of automatic backup times:
  - Backup on certain weekdays
  - Backup on certain days within month
  - Backup in an interval every x {Minutes, Hours, Days, Months}
- Automatically delete old backup sets:
  - Keep last x backup sets
  - Keep different amounts of backup sets for different time periods (up to five)
- and more


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
