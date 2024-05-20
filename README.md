## JQuickTake - Apple QuickTake Manager

***“I Think That This Situation Absolutely Requires A Really Futile And Stupid Gesture Be Done On Somebody’s Part.”*** - Otter (*Animal House - 1978*)

Back in the late 1990's someone gave me a hunk of grey plastic with an eyepiece, an Apple logo, and a 'QuickTake 100' label. It kind of looked like Luke Skywalker's binoculars, and I had basic sense that it was a camera. There was no manual, no cable, no software. The donor just handed it to me because (1) it was surplus equipment from their workplace and (2) I was an IT Guy and maybe I could "do something with it". I pretty much put it on a shelf with a vague notion of figuring things out sometime down the road.

A few years later, the Internet was able to [teach me all about Apple's mid-1990's experiment in the digital camera market](https://en.wikipedia.org/wiki/Apple_QuickTake). I also found a few smart people online who had posted instructions for building a cable and a few sources for the original QuickTake software for Windows. I built the cable, I got the software running on my WinXP computer, and I played around with the camera for awhile. I had my fun and then I put the camera back on its shelf, where it would (again) be left waiting while time marched forward.

A few years ago, I got the urge to play with the camera again. Despite the 20+ years that had passed, some fresh batteries were all it took to bring my QuickTake 100 back to life. The QuickTake software had not aged as gracefully. It simply would not work on my Windows 10 machine. And, of course, Old School serial ports were no longer in fashion on modern motherboards, so my homebrew QuickTake cable had nothing to plug into. Being a goal-driven nerd, I found myself a USB-to-Serial adapter cable, installed WinXP inside a VirtualBox VM, and I was once more able to manage stunning 640x480 images via the QuickTake software.

Rather than put the camera back on its shelf for another 20 years, I wondered if it would be possible to write my own software to replace the aging Apple software - a solution I could run natively on a Windows PC (or any other machine for that matter). It was something in the back of mind for a couple of years, but I never really found the time to actually *do something*. Once I retired (yeah, I'm that old), I had no excuses, so I went to work on a solution. 

As a result, JQuickTake is the Really Futile And Stupid Gesture that no one asked for, and I offer it up to anyone who loves their weird, old QuickTake camera and understands the joy of doing something just because you can.
## The Solution
JQuickTake is a Java Swing/AWT application that runs on a desktop. Once installed, it will connect to a serial (COM) port and talk to an Apple QuickTake camera in much the same way as the original Apple software. 

**Why Java?** While I'm an old, retired IT Guy, I haven't written a substantial desktop application in many years, so I wanted to chose technology that I was somewhat comfortable using. Also, I wanted to develop something that had a hope of working on Windows, Linux, and MacOS. I did a lot of Java application stuff way back when Java 2 was a big deal, so I had some (ancient) familiarity. I considered Python but decided against it because I don't not have any experience with Python GUI development. Also, I happened to stumble across a very nice Java-based Serial Port API called JSerialComm. So, Java FTW!

I won't talk much about the development process. I'd like to, but I won't - too much. To my knowledge Apple never published technical docs about QuickTake. I actually reached out to Apple via email, but never got a response. The lack of docs meant there was no published material detailing the critical request/response instruction-set for QuickTake. In other words, I knew the camera talked over a serial port, but didn't know the language of the camera. 

Learning the language of tQuickTake would require a very tedious exercise of running the OG QuickTake software on a WinXP VM while using serial port analyzer software to inspect the communications flowing across the wire. The effort would require disciplined analysis, a little bit of luck, and lot of sustained focus. I doubt I had all those qualities, but off I went. I made progress, but it was definitely a time-suck. It turns out that I did, in fact, have luck - a lot of it. In the magical world of Reddit's [r/VintageApple](https://www.reddit.com/r/VintageApple/), I bumped into Colin, who was WAY ahead of my efforts to decode the QuickTake protocol, albeit for a very different project than mine. And being the very generous soul that he is, he gave me [everything he documented](https://www.colino.net/wordpress/en/archives/2023/10/29/the-apple-quicktake-100-150-serial-communication-protocol/). Were it not for Colin, I would still be spinning my wheels decoding serial comms and it's quite possible I'd have grown bored and shut down my own project. Honestly, check out [Colin's QuickTake project](https://www.colino.net/wordpress/en/quicktake-for-apple-ii/). It's pretty amazing.

**What the software does:**

1. It will connect to an Apple QuickTake 100 or 150 camera and display its metadata - including, camera name, pictures taken, pictures remaining, Flash mode, Quality mode, etc.
![Connect to Camera](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Connect.JPG)

2. It will allow you to save selected or All images to local storage as an Apple QTK image file, using the naming convention IMAGE01.qtk, IMAGE0.qtk, etc.
![Save Images](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Save.JPG)

3. It provides an interface to 'remote control' the camera - patterned after the LCD screen on the QuickTake camera. This includes changing the Flash Mode and Quality Mode, taking a picture, setting a 5-second timer to take a picture, deleting all pictures on the camera, updating the camera name, and updating the date/time on the camera.
![Control the Camera](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Control.JPG)

**What the software does NOT do:**

This is JQuickTake v1.0, so it does lack a few features:

1. *It does not display image thumbnails or full images.* It only reads images from the camera and writes them to QTK files. However, any [imaging software based on dcraw](https://en.wikipedia.org/wiki/Dcraw) will easily convert Apple QTK files to JPG or TIFF images. For my development and testing, I used a nice utility called [RawDrop](http://www.wizards.de/rawdrop/) to convert my pictures - more on that below. At some point I plan to create QuickTake v2.0 to include thumbnail/image display, but I don't have a timeline for that yet.

3. *This software does not work for all QuickTake camera models*. It has been thoroughly tested on a QuickTake 150 camera. It has been somewhat tested with a QuickTake 100. Unfortunately my original QuickTake 100 experienced a hardware failure during development, so I could not do a complete round of testing. That said, I am reasonably confident that the software will work with the 100 and QuickTake 100 Plus. However, it definitely will not work with a QuickTake 200 camera. I would say that 95% of the code will work with that model, but there are few small differences and I do not have access to a 200 for development/testing purposes. Maybe someday...

## Installing and Running JQuickTake

The Github repo includes all the bits and pieces to build the application yourself (if you want to):

**src/main** contains all the .java source and resources (jpg and gif images) for the application

**libs** contains the built app JAR (JQuickTake-1.0.jar) and the JSerialComm JAR (jSerialComm-2.10.4.jar) that provides all the serial port comms support.

**classes/java/main/com/crazylegs/JQuickTake** contains all the app .class files compiled on JDK 17.0. Note that these have been tested with Java 17 and Java 22 runtimes.

However, the built solution is only for Windows machines. I am confident that it can work on Linux and MacOS if you feel like compiling and configuring. I plan to do a Linux build soon once I get some time to focus on that. As for MacOS - I don't have a machine handy, but feel free to give it a try if you like.

To install JQuickTake on a Windows 10 or 11 machine, there are 2 options:
1. The easiest path is to simply download and execute the provided Windows installer ***JQuickTake-1.0.exe*** . This will install JQuickTake as well as a bundled Java JRE 17.0 runtime all in one package. JQuickTake will install into ***c:\Program Files\JQuickTake***, create a Start Menu entry, and put a nice icon on your Windows desktop.

2. The slightly-more-work path is to download **distributions/app.zip** and unzip the file onto a Windows directory of your choosing. To fire up JQuickTake, you need to run ***Your Directory*\app\bin\app.bat**. The benefit of this option is that the application will run on top of whatever Java runtime you already have installed. Note the JQuickTake has been tested with Java 17 and Java 22 runtimes.

## Other Things You May Need

**My QuickTake camera doesn't have a cable!**

A brand new QuickTake was packaged with a cable that would let you plug the camera into your computer's serial port so that your QuickTake software could talk to your camera. For reasons I don't quite understand, the QuickTake cameras I've for sale online almost never include a cable. I'm guessing all those cables are hanging out with all of the lost socks and closet hangers.

But fear not! You can make your own cable (like I did) with a surplus DB9 serial cable, a Mac DIN8 (aka S-Video) cable/plug. and some basic wiring/soldering skills. Here's an [overview of the cable](https://hackaday.com/2013/03/26/diy-6-serial-cable-for-vintage-apple-quicktake-cameras/), although the  link to the build instructions is long broken. But if you scroll all the way to the [bottom of this page](https://www.colino.net/wordpress/en/archives/2023/10/29/the-apple-quicktake-100-150-serial-communication-protocol/), you'll see a nice picture showing which pins are wired to which pins so you can build your own. And you can find pre-built cables for sale [here](https://www.pccables.com/Products/70810) and [there](https://www.amazon.ca/Uxcell-a15101400ux0036-Female-Converter-Cable/dp/B01DKFB3PW/ref=sr_1_3?crid=30OXQJCYCR0V4&dib=eyJ2IjoiMSJ9.BpQMbzcIZ_5k8XpGGeJROobPsqXDV7Xkb3NQAe4V4qrkonszHC4ONxMpwz3bN3MRKf5FEwFHfSxebNoW6z47s2AZge0GcVPj1uQtIOZFMwbOIkwvwJI9TZ8WELHzu3D7yNt4Qd1WD8scHrDhec8Y4x-WxMTmrPncNslX3Z9XoakO3x8GM-t--XVrLaztL9zL_q0mDKD1WXZx4DmQK4Dhe-zTx1zxYrG7eMW_jLHvH6toR1pNvjU6YExegf4yHInOH9HNBQDBaVKawCZDjdjcH6oErBrbPJsgdj6qUYDMh98.ceGdz_T2HcNSVUBRmBe0Eo-yGKPdt3BxijeUaEb0mDk&dib_tag=se&keywords=db8%20din8%20cable&qid=1716229003&sprefix=db8%20din8%20cable,aps,111&sr=8-3) if you search around.

**What's a serial port?** 

This is a 9-pin male plug you might find on your PC (on the back) or on your laptop (on the side). The thing is, you likely don't have one. While still widely used in industrial/commercial devices, serial ports have become extinct on personal computers in favour of USB ports.

**Well, how do I get a serial port?** 

Serial-to-USB adapters are very common and very cheap. One end plugs into your computer's USB port while the other end of the adapter provides you a standard serial port plug. Once plugged into your computer (and once you install the provided driver software), your computer (and JQuickTake) will think your machine has a serial port. Finally, you can connect your QuickTake cable to your camera and shiny new computer serial port and everything should 'just work'. For my efforts, I used a [StarTech adapter](https://www.startech.com/en-ca/cards-adapters/icusb232v2), which can be [bought on Amazon](https://www.amazon.ca/StarTech-com-USB-Serial-Adapter-Prolific/dp/B00GRP8EZU?th=1) and other places. It supports Windows, Linux, and MacOS.

**So what do I do with all these QTK files I just saved off my camera?**

These files are pictures saved in the old Apple-specific image file format. To actually see those pictures, you want to convert them JPG files that your computer can actually understand. Luckily, that's an easy process. There is an amazing utility call dcraw that can convert image files from just about any camera into a JPG, TIFF, etc. For our purposes, dcraw support Apple QTK files! If you look on the [dcraw website](https://github.com/ncruces/dcraw), you will see a whole list of easy-to-use applications that leverage dcraw. On Windows, [I like to use RawDrop](https://www.dechifro.org/dcraw/) for it's very simply drag-and-drop UI. I recommend it!



