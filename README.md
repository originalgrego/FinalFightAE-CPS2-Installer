# FinalFightAE-CPS2-Installer

!!Beta installer, windows+MacOS+Linux only!!

**Windows:**
1. Install jdk 17, copy the installer to a directory in the root of a drive for example c:\ffaeinstaller, double click FinalFightAE-CPS2-Installer.jar<br><br>

**MacOS**
1. Install jdk 17 (18 will work, but will print errors), copy the installer to a directory in your home drive, for example ~/ffaeinstaller<br> 
From the terminal:
<pre>
cd ~/ffaeinstaller
java src/com/feverbrainstudios/ffae_installer/FFAEInstaller_MacOS.java
</pre><br>

**Linux**
1. Install jdk 17 (18 will work, but will print errors), copy the installer to a directory in the root of a drive for example ~/ffaeinstaller
from the terminal
<pre>
cd ~/ffaeinstaller
java src/com/feverbrainstudios/ffae_installer/FFAEInstaller_Linux.java
</pre>

2. Choose a Final Fight rom and Street Fighter Alpha 3 rom zip, merged or split roms should work, and click patch 
  * If there is an issue with the roms you selected you should be alerted by the tool, if it reports ALL of the crcs being missing for a rom set it is because the zip file is not compatible with the java zip handling library
  * Rezip your Final Fight or street fighter alpha rom using the built in windows "send to compressed (zipped) folder" option of the file browser and try again 
3. If the process is successful you will have a "results" folder in the installers directory with folders for each of the different targets, mame, mister and darksoft containing each rom

For mister please copy the zips to your games\mame folder and the mra to your normal mra location for cps2 games.

You need the lastest build of Jotego's cps2 core with decryption support.

For Darksoft CPS2 Multi simply copy the FFAE directory to a freshly formatted SD Cards games directory. 

Enjoy!
