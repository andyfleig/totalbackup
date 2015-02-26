cmake_minimum_required(VERSION 2.6)
project(totalbackuptray)
find_package(Qt4 REQUIRED)

include_directories(${QT_INCLUDES} ${CMAKE_CURRENT_BINARY_DIR})

set(TotalBackupTray_SRCS TotalBackupTray.cpp main.cpp)

qt4_automoc(${TotalBackupTray_SRCS})
add_executable(totalbackuptray ${TotalBackupTray_SRCS})
target_link_libraries(totalbackuptray ${QT_QTCORE_LIBRARY} ${QT_QTGUI_LIBRARY})

install(TARGETS totalbackuptray RUNTIME DESTINATION bin)
