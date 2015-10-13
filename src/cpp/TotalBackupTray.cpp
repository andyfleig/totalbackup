/*
 * Copyright 2014, 2015 Andreas Fleig (andy DOT fleig AT gmail DOT com)
 * 
 * All rights reserved.
 * 
 * This file is part of TotalBackup.
 *
 * TotalBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TotalBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TotalBackup.  If not, see <http://www.gnu.org/licenses/>.
 */
#include "TotalBackupTray.h"
#include <QSystemTrayIcon>
#include <QCoreApplication>

#include <QObject>
#include <QtGui/QLabel>
#include <QtGui/QMenu>
#include <QtGui/QMenuBar>
#include <QtGui/QAction>
#include <QSignalMapper>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdio.h>
#include <thread>
#include <string>
 
TotalBackupTray::TotalBackupTray()
{
  static QSystemTrayIcon* trayIcon;
  trayIcon = new QSystemTrayIcon(this);
  
  QIcon icon("./resources/TB_logo.png");
  trayIcon->setIcon(icon);
  
  QMenu* trayIconMenu = new QMenu(this);
  
  QAction* quitAction = new QAction(tr("&quit"), this);
  QAction* showAction = new QAction(tr("&show"), this);
  connect(quitAction, SIGNAL(triggered()), this, SLOT(quitTB()));
  connect(showAction, SIGNAL(triggered()), this, SLOT(showTB()));
  
  
  trayIconMenu->addAction(showAction);
  trayIconMenu->addSeparator();
  trayIconMenu->addAction(quitAction);
  
  
  trayIcon->setContextMenu(trayIconMenu);
  trayIcon->show();
  
  std::thread thread1(serverLoop, trayIcon);
  thread1.detach();
}

void TotalBackupTray::serverLoop(QSystemTrayIcon* trayIcon) {
  
  while (true) {
    int serverSock;
    serverSock = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSock < 0) {
      printf ("%s \n", "error: could not create server-socket");
    }
    struct sockaddr_in client;
    
    client.sin_family = AF_INET;
    client.sin_addr.s_addr = inet_addr("127.0.0.1");
    client.sin_port = htons(1235);
    
    printf ("%s \n", "start binding...");
    if (::bind(serverSock, (struct sockaddr*)&client, sizeof(client)) < 0) {
      printf ("%s \n", "error: could not bind");
    }
    printf ("%s \n", "binded");
    ::listen(serverSock, 1);
    socklen_t clientlen;
    clientlen = sizeof(client);
    printf ("%s \n", "ready to accept");
    int partnerSock = ::accept(serverSock, (struct sockaddr*)&client, &clientlen);
    if (partnerSock < 0 ) {
      printf ("%s \n", "error: could not accept");
    }
    printf ("%s \n", "accepted");
    
    int recvBufferLength = 1023;
    char recvBuffer[1023];
    // Empfangen:
    int readBytes;
    std::string strIn;
    bool loop = true;
    readBytes = ::recvfrom(partnerSock, recvBuffer, recvBufferLength, 0, NULL, NULL);
    
    // erste drei Zeichen geben die Anzahl der Zeichen an:
    int numberOfSigns1 = recvBuffer[0] - '0';
    int numberOfSigns2 = recvBuffer[1] - '0';
    int numberOfSigns3 = recvBuffer[2] - '0';
    int numberOfSigns = numberOfSigns1 * 100 + numberOfSigns2 * 10 + numberOfSigns3;
    // Erhaltenen Nachricht in char[] stecken:
    char result[numberOfSigns];
    for (int i = 3; i < numberOfSigns + 3; i++) {
      result[i - 3] = recvBuffer[i];
    }
    trayIcon->showMessage("TotalBackup" , result, QSystemTrayIcon::Information, 5000);
    
    ::close(partnerSock);
    ::close(serverSock);
  }
}

void TotalBackupTray::startSocket() {
  // Netzwerksocket:
  // Socket aufbauen:
  sock = socket(AF_INET, SOCK_STREAM, 0);
  if (sock < 0) {
    printf ("%s \n", "error: could not create client-socket");
  }
  // Zum Server verbinden:
  struct sockaddr_in server;
  memset(&server, 0, sizeof(server));
  unsigned long addr = inet_addr("127.0.0.1");
  memcpy((char*)&server.sin_addr, &addr, sizeof(addr));
  
  server.sin_family = AF_INET;
  server.sin_port = htons(1234);
  
  if (::connect(sock, (struct sockaddr*)&server, sizeof(server)) < 0) {
    printf ("%s \n", "error: could not connect");
  }
}

void TotalBackupTray::quitTB() {
  startSocket();
  int32_t msg = htonl(0);
  ssize_t s = send(sock, &msg, sizeof(msg), 0);
  if (s < 0) {
    printf ("%s \n", "error: could not send");
  }
  if (::close(sock) < 0) {
    printf ("%s \n", "error: could not close socket");
  }
  QCoreApplication::exit(0);
}

void TotalBackupTray::showTB() {
  startSocket();
  int32_t msg =htonl(1);
  ssize_t s = send(sock, &msg, sizeof(msg), 0);
  if (s < 0) {
    printf ("%s \n", "error: could not send");
  }
  if (::close(sock) < 0) {
    printf ("%s \n", "error: could not close socket");
  }
}

TotalBackupTray::~TotalBackupTray()
{}

#include "TotalBackupTray.moc"
