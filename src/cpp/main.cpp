#include <QtGui/QApplication>
#include "TotalBackupTray.h"
#include <stdio.h>


int main(int argc, char** argv)
{
    QApplication app(argc, argv);
    TotalBackupTray totalbackuptray;
    //totalbackuptray.show();
    return app.exec();
}