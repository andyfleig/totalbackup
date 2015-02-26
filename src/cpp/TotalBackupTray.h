#ifndef TotalBackupTray_H
#define TotalBackupTray_H

#include <QtGui/QMainWindow>

class TotalBackupTray : public QMainWindow
{
    Q_OBJECT
public:
    TotalBackupTray();
    virtual ~TotalBackupTray();
    int sock;
    void startSocket();
private slots:
  void quitTB();
  void showTB();
};

#endif // TotalBackupTray_H
