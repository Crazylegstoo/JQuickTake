# JQuickTake - Apple QuickTake Manager v1.4

***“I think that this situation absolutely requires a really futile and stupid gesture be done on somebody’s part.”*** - Otter (*Animal House - 1978*)

Back in the late 1990's someone gave me a hunk of grey plastic that had an eyepiece, an Apple logo, and a 'QuickTake 100' label. It kind of looked like Luke Skywalker's binoculars, and I had basic sense that it was a camera. There was no manual, no cable, no software. The donor just handed it to me because (1) it was surplus equipment from their workplace and (2) I was an IT Guy and maybe I could "do something with it". I pretty much put it on a shelf with a vague notion of figuring things out sometime down the road.
![Apple QuickTake 100](https://www.dpreview.com/files/p/articles/3774104701/quicktake100.jpeg)
A few years later, the Internet was able to [teach me all about Apple's mid-1990's experiment in the digital camera market](https://en.wikipedia.org/wiki/Apple_QuickTake). I also found a few smart people online who had posted instructions for building a cable and a few sources for the original QuickTake software for Windows. I built the cable, I got the software running on my WinXP computer, and I had some fun with the camera for awhile.  Back onto the shelf the camera went, where it would (again) be left waiting while time marched forward.

A couple of years ago, I got the urge to play with the camera again. Despite the 20+ years that had passed, some fresh batteries were all it took to bring my QuickTake 100 back to life. The QuickTake software had not aged as gracefully. It simply would not work on a Windows 10 machine. And because Old School serial ports were no longer in fashion on modern motherboards, my homebrew QuickTake cable was left dangling. Being a goal-driven nerd, I found myself a USB-to-Serial adapter cable, installed WinXP inside a VirtualBox VM, and I was once more able to manage truly stunning 640x480 pictures via the original QuickTake software.

Rather than put the camera back on its shelf for another 20 years, I wondered if it would be possible to write my own software to replace the aging Apple software - a solution I could run natively on a modern Windows PC (or any other machine for that matter). It was something in the back of my mind for a couple of years, but I never really found the time to actually *do something*. Once I retired (yeah, I'm that old), I had no excuses, so I went to work on a solution. 

The result is **JQuickTake** - the Really Futile And Stupid Gesture that no one asked for. I offer it up to anyone who loves their weird, old QuickTake camera and understands the joy of doing something just because you can.

# The Solution
JQuickTake is a Java Swing/AWT application that runs on a desktop. Once installed, it will connect to a serial (COM/TTY) port on the computer and talk to an Apple QuickTake camera plugged into that port. It works in much the same way as the original Apple software. 

**Why Java?** While I'm an old, retired IT Guy, I haven't written a substantial desktop application in many years, so I wanted to choose technology that was somewhat comfortable for me. Also, I wanted to develop something that had a hope of working on Windows, MacOS, and even Linux. I did a lot of Java application stuff way back when Java 2 was a big deal, so I had some (ancient) familiarity. I considered Python but decided against it because I don't have any experience with Python GUI development. That would have been one more thing to learn for a project filled with many things to learn. Also, I happened to stumble across a very nice Java-based Serial Port API called [jSerialComm](https://fazecast.github.io/jSerialComm/). So, Java FTW!

I won't talk about the development process - too much :)  As far as I could tell, Apple never published technical docs about QuickTake. I actually reached out to Apple via email to see if they would do something under their Open Source program or maybe offer up some old source code along the lines of what they've already done with the [Computer History Museum](https://computerhistory.org/playlists/source-code/). Alas, Apple gave no response. The lack of docs means there is no published material detailing the critical request/response instruction-set for QuickTake. In other words, I knew the camera talked over a serial port, but I didn't know the language of the camera. For the layman, it's like knowing that human communication involves a mouth sending sound waves to someone else's ears, yet having no clue how to speak German.

Learning the language of QuickTake would require a very tedious exercise of running the [OG QuickTake software](https://archive.org/details/Apple-Quicktake-PC-Software) on a WinXP VM while using serial port analyzer software to inspect the communications flowing across the serial cable. There might even be just a smidge of de-compiling the original Apple QuickTake binaries.  The effort would require disciplined analysis, sustained focus, and a little bit of luck. 

I doubted I had all those qualities, but off I went. While I was making progress, it was definitely not going to be a quick exercise for my brain. But it turns out that I did, in fact, have luck - a lot of it. In the magical world of Reddit's [r/VintageApple](https://www.reddit.com/r/VintageApple/), I bumped into [Colin](https://www.reddit.com/user/Colin-McMillen/), who was WAY ahead of my efforts to decode the QuickTake Protocol, albeit for a very different project than mine. And being the very generous soul that he is, he gave me [everything he documented](https://www.colino.net/wordpress/en/archives/2023/10/29/the-apple-quicktake-100-150-serial-communication-protocol/). Were it not for Colin, I would likely still be spinning my wheels decoding serial comms and it's quite possible I'd have grown bored and shut down my own project. All of this is to say, "Thank-you, Colin!". Honestly, check out [Colin's QuickTake project](https://www.colino.net/wordpress/en/quicktake-for-apple-ii/). It's pretty amazing.

## What The JQuickTake Software Does

The software will run on Windows and Mac computers and offers the following capabilities.

1 - It will connect to an Apple QuickTake 100 or 150 camera and display its metadata, including: camera name, pictures taken, pictures remaining, Flash mode, Quality mode, etc.

Connect screen:
![Connect to Camera](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Win-Connect.JPG)

2 - It will allow you to save Selected or All pictures to local storage as an Apple QTK picture file, using a customizable file-naming format.

Save screen:
![Save Pictures](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Win-Save.JPG)

3 - It provides an interface to 'remote control' the camera - patterned after the LCD screen on the QuickTake 100/150 camera. This includes changing the Flash Mode and Quality Mode, taking a picture, setting a 5-second timer to take a picture, deleting all pictures on the camera, updating the camera name, and updating the date/time on the camera.

Control screen:
![Control the Camera](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/Win-Control.JPG)
## What The JQuickTake Software Does Not Do

JQuickTake v1.4 does lack a few features:

1. **It does not display picture thumbnails or full pictures.** It only reads pictures from the camera and writes them to QTK files. However, any [imaging software based on dcraw](https://en.wikipedia.org/wiki/Dcraw) will easily convert Apple QTK files to JPG or TIFF pictures. For my development and testing, I used a nice utility called [RawDrop](http://www.wizards.de/rawdrop/) to convert my pictures - more on that below. At some point I plan to create QuickTake v2.0 to include thumbnail/picture display, but I don't have a timeline for that yet.

2. **This software does not work for the QuickTake 200 camera.** It has been developed and tested for QuickTake 100 and 150 camera models. However, it definitely will not work with a QuickTake 200 camera. I would say that the majority of JQuickTake code will work with that model, but there are differences related to the 200, and I currently do not have access to that camera for development/testing purposes. Maybe someday...

# Installing and Running JQuickTake

The fully-built solution has been tested successfully on Windows and MacOS platforms - specifically Windows 10, Windows 11, MacOS Catalina 10.x, and MacOS Sonoma 14.x . Linux is not specifically supported at this time, but it is definitely possible if there is a demand for it.

Since the application is Java-based, it requires a minimum of a Java 17 runtime on the local computer. It will not run on any Java runtime previous to Java 17. Note that JQuickTake has been tested on both Java 17 and Java 22. 

The installation options below allow JQuickTake to use the Java runtime already installed on your computer or use a Java 17 runtime bundled with the application. Both install options are part of  the [current JQuickTake Release](https://github.com/Crazylegstoo/JQuickTake/releases/tag/v1.4)

**NOTE:** If you are upgrading from an older version of JQuickTake, please ensure you uninstall it first before installing a newer version of the software.

## Option 1 - All-In-One Solution

The *easiest path* is to simply download and execute the provided installer:

**Windows** - Download and run ***JQuickTake-1.3.exe*** . This will install JQuickTake as well as a bundled Java Java 17 runtime all in one package. All software will install into ***c:\Program Files\JQuickTake***, create a Start Menu entry, and put a nice retro icon on your Windows desktop.

**MacOS** - Download and run ***JQuickTake-1.3.dmg***. This will install JQuickTake as well as a bundled Java 17 runtime all in one package. It will install just like any other Mac application and can be started up from Finder or Launchpad via clicking on a nice retro icon.

## Option 2 - Use Your Own Java Runtime

The slightly-more-work path is to download the ***app.zip*** (or ***app.tar***) file and unzip it into a directory/folder of your choosing. To fire up JQuickTake, you need to do the following:

**Windows** - Run ***Your Folder*\app\app\bin\app.bat**. The benefit of this option is that the application will run on top of whatever Java runtime you already have installed. Note that JQuickTake has been tested with Java 17 and Java 22 runtimes.

**MacOS** - Run ***Your Folder*\app\bin\app**. The benefit of this option is that the application will run on top of whatever Java runtime you already have installed. Note that JQuickTake has been tested with Java 17 and Java 22 runtimes.

**Note:** To quickly check what version of Java is installed on your PC, open a command line session (Windows via CMD and Mac via Terminal) and type *`java -version`.* The results will tell you which version is installed on your PC.

## You Can Roll Your Own

The Github repo includes all the bits and pieces to build the application yourself if you really want to:

**src/main** contains all the .java source and resources (jpg and gif images) for the application

**libs** contains the fully-built app JAR ***JQuickTake-1.2.jar*** and the jSerialComm JAR ***jSerialComm-2.11.0.jar*** that provides all the serial port comms support.

**classes/java/main/com/crazylegs/JQuickTake** contains all the app .class files compiled on Java 17. Note that these have been tested with Java 17 and Java 22 runtimes. These .class files will not run on anything previous to Java 17.

# Other Things You May Need

## My QuickTake camera doesn't have a cable!

Back in the mid-90's, a brand new QuickTake was packaged with a serial cable that would let you plug the camera into your computer's serial port and talk to the included QuickTake software. For reasons I don't quite understand, the gently-used QuickTake cameras I've seen for sale online almost never include a cable. I'm guessing all those cables are hanging out in some intergalactic lost-and-found with a billion odd socks and clothes hangers.

But fear not! You can make your own cable (like I did) with a [female DB9](https://a.co/d/1TWCMJX) serial cable, a [male DIN8](https://a.co/d/0Mkaqjt) cable, and some basic wiring/soldering skills. You can also find cables that already have the required female DB9 and male DIN8 plugs, which you cut and re-wire for your QuickTake 100/150 camera - examples: [here](https://www.pccables.com/Products/70810) and [there](https://a.co/d/4wgH4qr) 

No matter how you source your cables, they will require some simple re-wiring to support a QuickTake camera. Here's an [overview of the cable](https://hackaday.com/2013/03/26/diy-6-serial-cable-for-vintage-apple-quicktake-cameras/), although the  link to the build instructions is long broken. The following diagram shows how you need to modify the wiring of your serial cable:
![QuickTake 100/150 Wiring](https://github.com/Crazylegstoo/JQuickTake/blob/main/Images/QT100-150%20Wiring.JPG) 

The best approach to identifying which wire is connected to which pin is using a multi-meter with a Continuity testing function. Put one probe on a pin, and then use the other probe to touch the (bare) wires to determine which wire goes to which pin. With all the wires identified for each plug, you can go about connecting wires according the diagram above.

One last word: While it can be satisfying to build your own cable, it is possible with a bit searching to find pre-built QuickTake serial cables. Purchased cables should be validated, however, to ensure they are built according to the wiring diagram above.

## What's a serial port?

This is a [9-pin male plug](https://en.wikipedia.org/wiki/Serial_port) you might find on your PC (on the back) or on your laptop (on the side). The thing is, you quite possibly do not have one. While still widely used in industrial/commercial devices, serial ports have become nearly extinct on personal computers in favour of USB ports.

## How can I tell if I have serial port?

Beyond physically inspecting the outside of your PC, there are software tools on your computer that let you see if there is a serial port defined. 

**Windows** - Use the *Device Manager* utility to see if there is anything listed under *Ports (COM & LPT)* such as COM1, COM2, etc. 

**MacOS** - Open a *Terminal* session, type `ls /dev/tty*`, and in the resulting list look for entries that start with something like */dev/tty.serial* or */dev/tty.usbserial*. 

**Alternatively**, you can fire up JQuickTake and see if the *Serial Ports* drop-down on the **Connect to Camera** tab has anything in it. If it is empty, there are no serial ports defined on your PC.

The absence of these kinds of entries means no serial ports are defined on your PC, so it is likely you will need a USB-to-Serial adapter as described in the next section. 

If JQuickTake is, indeed, showing entries in the *Serial Ports* drop-down on the **Connect to Camera** tab, you could be in good shape. You will need to select one of those entries and press *Connect* to see if the application can talk to your camera. If the connection fails, a pop-up will provide possible reasons. If there are multiple ports in the drop-down, you will need to select the proper entry through trial-and-error.

## Well, how do I get a serial port?

Assuming your PC does not have a physical serial port, Serial-to-USB adapters are very common and fairly cheap. One end plugs into your computer's USB port while the other end of the adapter gives you a standard serial port plug. Once plugged into your computer (and once you install any required driver software), your computer (and JQuickTake) will think your machine has a serial port. You can then connect your QuickTake cable to your camera and to your shiny new computer serial port and everything should 'just work'. 

However.... There are a multitude of usb-to-serial adapters that can be bought very inexpensively from various online retailers. Furthermore, there appear to be 3 types of adapters on the market, with each type using a different chip technology to provide serial port behaviour. Knowing which is which is important, and it is generally beneficial to buy a 'name brand' when possible (even if it costs a few dollars more). The 3 types are as follows:

**Prolific (PL2303) adapters** - All of my development of JQuickTake has been with a Prolific-based adapter. The one I use is a [StarTech adapter](https://www.startech.com/en-ca/cards-adapters/icusb232v2), which can be [bought on Amazon](https://www.amazon.ca/StarTech-com-USB-Serial-Adapter-Prolific/dp/B00GRP8EZU?th=1) and other places. It has drivers that support Windows, Linux, and MacOS. You will need to install the drivers manually. If the drivers are not referenced in your adapter cable documentation, you can download them manually from the Prolific website. Windows drivers [are here](https://www.prolific.com.tw/us/showproduct.aspx?p_id=225&pcid=41), while MacOS drivers can be [found here](https://www.prolific.com.tw/us/showproduct.aspx?p_id=229&pcid=41).

**FTDI (FT232RL) adapter** - I have tested JQuickTake on both Windows and MacOS using this type of adapter, and the one that has worked for me is a [DTech adapter](https://www.amazon.ca/dp/B09BVM6TDS?ref=ppx_yo2ov_dt_b_fed_asin_title&th=1), which can be bought on Amazon. On Windows, the drivers should be automatically installed. On MacOS, the FTDI drivers are built into MacOS. However, if you want/need to manually install the drivers for this type of adapter cable, you can find the FTDI VCP drivers for [WIndows here](https://ftdichip.com/drivers/vcp-drivers/), while the MacOS install instructions for VCP drivers [can found here](https://www.ftdichip.com/Support/Documents/InstallGuides/Mac_OS_X_Installation_Guide.pdf).

**CH340 adapter** - If an adapter is not based on the PL2303 or FT232RL chipset, then it is likely based on the CH340 chipset. I have had absolutely no luck in getting a CH340 adapter cable to work properly on Windows or MacOS. While they are recognized by the computer as a serial port, they do not appear to properly support the full serial port behavior. Specifically, they do not pass along any bytes sent from a QuickTake camera to a computer. I have received similar feedback from JQuickTake users who have tried to use this type of adapter. 

Based on my ownb experience plus feedback from JQuickTake users:

 - **Prolific (PL2303)** adapter cables work well.
 - **FTDI (FT233RL)** adapters can also work well, but there are 'generic' versions of these adapters on the market that do not work properly (based on user feedback)
 - **CH340-based** adapter cables *should be avoided!*

As if we haven't talked enough about usb-to-serial adapters, one last topic: One way to get a hint if you have a genuine Prolific or FTDI chip in your adapter is to check the chip's Vendor ID (VID) and Product ID (PID). The values you would look for are:

**Prolific PL2303** - VID = 067B and PID = 2303 (sometimes 0609)

**FTDI FT232RL** - VID = 0403 and PID = 6001

To check these values (with the adapter plugged into a USB port):

**Windows**
 - Go to Device Manager
 - Expand 'Universal Serial Bus Controllers'
 - Find the adapter device in the resulting list and right-click Properties
 - Select the Details tab
 - Using the drop-down, select Hardware IDs or Device Instance Path
 - You will VID and PID in the format "USB\\VID_XXXX&PID_YYYY...".

**MacOS**
 - Click the Apple logo and then About This Mac
 - Hot the button to generate a System Report
 - Select USB from the left-hand hardware menu
 - Find the adapter device in the resulting list
 - The VID and PID will be displayed in the detailed information for the selected device

## So what do I do with all these QTK files I just saved off my camera?

These files are pictures saved in the old Apple-specific picture file format that your computer probably does not understand. To actually see those pictures, you will want to convert them to JPG files that your computer can actually work with. Luckily, that's an easy process. There is an amazing command-line utility call *dcraw* that can convert RAW picture files from just about any camera into a JPG or TIFF.  For our purposes, *dcraw* supports Apple QTK files! 

Using the command-line version of *dcraw* can be complex and intimidating, but there are many applications that provide a nice graphical  interface while using *dcraw* under-the-covers. If you look on the [dcraw website](https://github.com/ncruces/dcraw), you will see a whole list of those applications. On Windows, [I like to use RawDrop](https://www.dechifro.org/dcraw/) for its dead-simple drag-and-drop UI. I recommend it!

# This README Is Too Long

There are just a few more things I want to mention:

## Be Gentle

If you decide to crawl through the source code, maybe cut me a wee bit of slack. Like I said earlier, I haven't done serious desktop app coding in many years. I know the code could be structured better, leverage event-driven processes more effectively, definitely benefit from optimizations, and very definitely handle exception conditions better than it does today. I'll even agree that the UI could be a bit slicker. But the code seems to work and I included some comments, so there's that.

Also, I forced myself to learn a few new things - most notably [Gradle](https://gradle.org/) and [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html). I'm not sure I'm using them to full effect but, you know how it is with Old Dogs and New Tricks. And if anyone is wondering what IDE I used,, it's called Notepad++ coupled with a lovely black CMD window. Baby steps, man. Baby steps.

## There Are Some Major Thank-You's Required!

 - [Colin Leroy-Mira](https://www.colino.net/wordpress/) - Again, thank you for sharing your excellent work on the QuickTake Protocol.  It made my little project much easier.
 - [Dave Coffin](https://www.dechifro.org/dcraw/), for creating *dcraw* and making sure it supports Apple QKTK and QKTN compression.
 - [Fazecast Inc](https://fazecast.github.io/jSerialComm/) for creating and maintaining the excellent jSerialComm library.
 - [Frank Siegert](http://www.wizards.de/rawdrop/) for writing the lean, mean, and dead-simple RawDrop app that made my dev/test efforts that much easier.
 

## Where You Can Find Me

If you have a question, or just want to virtually point and laugh:

Reddit - https://www.reddit.com/user/Crazylegstoo/

Email - jquicktake@gmail.com

Many Thanks, and I truly hope JQuickTake actually works. ;)

**K. Godin** - London, Ontario, Canada (2024)


