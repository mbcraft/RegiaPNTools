# RegiaPNTools
Deployer application for RegiaPNPlayer.

This application was developed for running on Microsoft Windows. 
With some effort probably can be tuned and modified to run on linux too : 
it was engineered with portability patterns in its architecture, 
  there are config files for handling the OS dependencies (GoF "Driver" pattern).
This application is basically a GUI for helping flashing an image into 
an SD card with the application that can run into a RaspberryPi.
Then writes some custom files for handling the customization. On Windows 'usbit'
was chosen as SD card writer software tool.
It also contains an auto-updater for the player and this software too.

- Marco B.
