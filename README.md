## JQuickTake - Apple QuickTake Manager

***“I Think That This Situation Absolutely Requires A Really Futile And Stupid Gesture Be Done On Somebody’s Part.”*** - Otter from Animal House (1978)

Back in the late 1990's someone gave me this 'thing' with an Apple logo and a 'QuickTake 100' label. It kind of looked like a Star Wars-inspired set of binoculars, and I had no idea what it was. There was no manual, no cable, no software. The donor just handed to me because (1) it was surplus equipment from their workplace and (2) I was an 'IT Guy' and maybe I could "do something with it".

Thanks to the Internet, I learned all about Apple's mid-1990's experiment in the digital camera market. I also found a few smart people online who had posted instructions for building a cable and a few sources for the original QuickTake software for Windows. I built the cable, I got the software running on my WinXP computer, and I played around with the camera for awhile. I had my fun and then I put the camera back on its shelf, where it would sit unloved while time marched ahead.

A few years ago, I got the urge to play with the camera again. Despite the 20+ years that had passed, some fresh batteries were all it took to bring my QuickTake back to life. The QuickTake software had not aged as gracefully. It simply would not work on my Windows 10 machine. And, of course, Old School serial ports were no longer in fashionon modern motherboards, so my homebrew QuickTake cable had nothing to plug into. Being a goal-driven nerd, I found myself a USB-to-Serial adapter cable, installed WinXP inside a VirtualBox VM, and I was once more able to manage stunning 640x480 images via the QuickTake software.

Rather than put the camera back on its shelf for another 20 years, I wondered if it would be possible to write my own software to replace the aging Apple software - a solution I could run natively on a Windows PC (or any other machine for that matter). It was something in the back of mind for a couple of years, but I never really found the time to actually do something. Now that I'm retired (yeah, I'm that old), I had no excuses. JQuickTake is the result, and I offer it up to anyone who loves their weird, old QuickTake camera and understands the joy of doing something just because you can.

## The Solution
JQuickTake is a Java Swing/AWT application that runs on a desktop. Once installed, it will connect to a COM (serial) port and talk to an Apple QuickTake camera in much the same way as the original Apple software. 

**Why Java?** While I'm an old, retired IT Guy, I haven't written a substantial desktop application in many years. Also, I wanted to develop something that had a hope of working on Windows, Linux, and MacOS. I did a lot of Java application stuff way back when Java 2 was a big deal, so I had some (ancient) familiarity. I considered Python but decided against it because I did not have any experience with Python GUI development and I happened to stumble across a very nice Java-based Serial Port API called JSerialComm. So, Java FTW!

**What the software does:**

1. I*t will connect to an Apple QuickTake 100 or 150 camera* and display its metadata - including, camera name, pictures taken, pictures remaining, Flash mode, Quality mode, etc.
2. *It will allow you to save selected or all images to local storage* as an Apple QTK image file, using the naming convention IMAGE01.qtk, IMAGE0.qtk, etc.
3. I*t provides a interface to 'remote control' the camera* - patterned after the LCD screen on the QuickTake. This includes changing the Flash Mode and Quality Mode, taking a picture, setting a 5-second timer to take a picture, deleting all pictures on the camera, updating the camera name, and updating the date/time on the camera.

**What the software does NOT do:**

This is JQuickTake v1.0, so it does lack a few features:

1. *It does not display image thumbnails or full images.* It only reads images from the camera and writes them to QTK files. However, any imaging software based on dcraw will easily convert them to JPG or TIFF images. For my development and testing, I used a nice utility called RawDrop to convert my pictures - more on that below. At some point I plan to create QuickTake v2.0 to include thumbnail/image display, but I don't have a timeline for that yet.
2. *This software does not work for all QuickTake camera models*. It has been thoroughly tested on a QuickTake 150 camera. It has been somewhat tested with a QuickTake 100. Unfortunately my original QuickTake 100 experienced a hardware failure during development. That said, I am reasonably confident that the software will work with the 100 and QuickTake 100 Plus. However, it definitely will not work with a QuickTake 200 camera. I would say that 95% of the code will work with that model, but there are few small differences and I do not have access to a 200 for development/testing purposes. Maybe someday...

## Installing JQuickTake

The Github repo includes all the source code and resources. However, the built solution is only for Windows machines. I am confident that it can work on Linux and MacOS if you feel like compiling and configuring. I plan to do a Linux build soon once I get some time to focus on that. As for MacOS - I don't have a machine handy, but feel free to give it a try if you like.

To install JQuickTake on a Windows 10 or 11 machine, simply download and execute ***JQuickTake-1.0.exe*** to drive a standard Windows install. The installer will install JQuickTake as well as it's bundled Java JRE 17.0 runtime as one package. It will install into ***c:\Program Files***, create a Start Menu entry, and put an icon on your Windows desktop.

## Running JQuickTake


