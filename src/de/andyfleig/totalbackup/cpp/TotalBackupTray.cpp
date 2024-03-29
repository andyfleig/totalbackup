/*
 * Copyright 2014 - 2019 Andreas Fleig (github AT andyfleig DOT de)
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
  
  QIcon icon("./resources/TB_logo_sq.png");
  trayIcon->setIcon(icon);
  
  QMenu* trayIconMenu = new QMenu(this);
  
  QAction* quitAction = new QAction(tr("&quit"), this);
  QAction* showAction = new QAction(tr("&show/hide"), this);
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
  
  int serverSock;
  serverSock = socket(AF_INET, SOCK_STREAM, 0);
  if (serverSock < 0) {
    printf ("%s \n", "error: could not create server-socket");
  }
  struct sockaddr_in client;
  
  client.sin_family = AF_INET;
  client.sin_addr.s_addr = inet_addr("127.0.0.1");
  client.sin_port = htons(1235);

  int yes = 1;
  setsockopt(serverSock, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));
  printf ("%s \n", "start binding...");
  if (::bind(serverSock, (struct sockaddr*)&client, sizeof(client)) < 0) {
    printf ("%s \n", "error: could not bind");
  } else {
    printf ("%s \n", "binded");
  }
  ::listen(serverSock, 5);
  int partnerSock;
  socklen_t clientlen;
  clientlen = sizeof(client);
  
  while (true) {
    partnerSock = ::accept(serverSock, (struct sockaddr*)&client, &clientlen);
    
    int recvBufferLength = 1023;
    char recvBuffer[1023];
    // receiving
    int readBytes;
    QString msg;
    readBytes = ::recvfrom(partnerSock, recvBuffer, recvBufferLength, 0, NULL, NULL);
    
    // first three chars encode the total number of chars of the message
    int numberOfChars1 = recvBuffer[0] - '0';
    int numberOfChars2 = recvBuffer[1] - '0';
    int numberOfChars3 = recvBuffer[2] - '0';
    
    int numberOfChars = numberOfChars1 * 100 + numberOfChars2 * 10 + numberOfChars3;
    // special case: first three bits are 0 is the termination signal
    if (numberOfChars == 0) {
      ::close(partnerSock);
      ::close(serverSock);
      QCoreApplication::exit(0);
    } else {
      // handle received message
      for (int i = 3; i < numberOfChars + 3; i++) {
	msg += recvBuffer[i];
      }
      trayIcon->showMessage("TotalBackup" , msg, QSystemTrayIcon::Information, 5000);
      
      ::close(partnerSock);
    }
  }
  ::close(serverSock);
}

void TotalBackupTray::startSocket() {
  // start socket
  sock = socket(AF_INET, SOCK_STREAM, 0);
  if (sock < 0) {
    printf ("%s \n", "error: could not create client-socket");
  }
  // connect to server (TotalBackup)
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
  ::close(sock);
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
  ::close(sock);
}

TotalBackupTray::~TotalBackupTray()
{}

#include "TotalBackupTray.moc"
