## Introduction to OOP
### <a href='https://www.ole.bris.ac.uk/webapps/blackboard/content/contentWrapper.jsp?course_id=_252884_1&displayName=Mediasite%20Collections&href=%2Fwebapps%2Fblackboard%2Fexecute%2Fblti%2FlaunchPlacement%3Fblti_placement_id%3D_3169_1%26course_id%3D_252884_1%26mode%3Dcpview%26wrapped%3Dtrue' target='_blank'> Weekly Briefing ![](../../resources/icons/briefing.png) </a>
### Task 1: Introduction
 <a href='01%20Introduction/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='01%20Introduction/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

Welcome to this first practical workbook for the Object Oriented Programming with Java unit.
The aim of these workbooks is to present key theoretical concepts that underlie the unit and 
give you the opportunity to apply these in practical exercises.

Where you see the blue "Slides" and "Video" buttons at the top of a section, 
you should view these _before_ attempting the practical activities described in that section. 
You should do this now for this current task - this will present an introduction to
Java and provide you with a high-level understanding of the nature of Object Orientation.
Note that it is easier to "clone" these workbooks and view them locally on your own computer
(rather than trying to browse them online via the GitHub website).

One of the reasons we focus on Java in this unit is that it is a very popular and marketable
programming language. Before moving on to the main tasks in this workbook, you might like to watch 
<a href="https://www.youtube.com/watch?v=Og847HVwRSI" target="_blank">this animation</a>
to see how Java's popularity has changed over the years.
  


# 
### Task 2: Object Orientation
 <a href='02%20Object%20Orientation/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='02%20Object%20Orientation/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

In the previous teaching block, you learnt to program in C (to varying degrees of success ;o). In this unit however, you will be using **Java** and as such the _organisation_ of your code _should_ be very different. There are a number of key concepts that are fundamental to **Object Oriented** code. Before we progress to any hands-on practical exercises, it is important that you have an appreciation of these concepts. View the slides and video above to gain an understanding of these characteristics of Object Oriented code. At this stage, we only introduce the concepts at a very high level - we will revisit them again in later workbooks.  


# 
### Task 3: IntelliJ IDEA


To support the efficient development of code in this unit, we will be using the IntelliJ Integrated Development Environment (IDE).
This is installed on the lab machines for your convenience, however you may wish to install it on your own computer.
Now is a good time to attempt this - before we get too busying understanding the features and concepts of Java.
Some of the installation steps are platform-specific, so we have provided separate videos for getting started with IntelliJ on
<a href="https://web.microsoftstream.com/video/608b2c4c-1834-4429-9c86-bf19530c7f3a" target="_blank">Ubuntu</a>, 
<a href="https://web.microsoftstream.com/video/382a7600-3940-4415-a680-002de6960b99" target="_blank">Windows</a> and
<a href="https://mediasite.bris.ac.uk/Mediasite/Play/8822c0d46676424497d55a11ac01f8e21d" target="_blank">Mac OSX</a>.
  


**Hints & Tips:**  
Note: In the videos we illustrate the installation and project creation process with a particular version of Java. You may have different versions of Java installed on your computer - just pick the most recent version (version 17 is typical, but anything 11 or greater is fine !)  


# 
### Task 4: Hello World
 <a href='04%20Hello%20World/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='04%20Hello%20World/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

The first program that you write in _most_ languages is "Hello World". Take a look at the slides and video above that describe "Hello World" in Java.
Once you understand the description of the code, create a new project in IntelliJ and then add a new Java Class called `HelloWorld` to the `src` folder
(by right-clicking on the `src` folder in the project view and selecting `New > Java Class`).
Now add some code to your class by copy-and-pasting the code from the above slide into your new Java Class (make sure you save it !).
Finally run the code you have added by clicking on the `Run` top-bar menu and selecting `Run...`.
After you have selected your main class (there should only be one to choose from !), there will be a short delay as IntelliJ compiles and runs your code.
If everything is successful you should see something like the screenshot below.
Make sure your code compiles and runs correctly before moving on to the next task in this workbook.  


![](04%20Hello%20World/images/hello-world.jpg)

# 
### Task 5: Build Systems
 <a href='05%20Build%20Systems/slides/maven.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a>

Now that you have some experience of creating IntelliJ projects, it is worth spending a bit of time exploring the related topic of *build systems*.
Introducing this concept very early on in the unit may seem a bit odd, but they are a core part of most major software projects (be they Open Source projects or in-house proprietary development work). Build systems are so important that in many big projects there is a dedicated engineer who handles build and release related tasks (look up "DevOps" if you don't believe us !).

You may have used *GNU Make* previously on this degree programme: this software saves you from having to type out the compile/link commands every time you want to build your software. `make` takes care of running all the required build commands (as specified in the `Makefile`) and intelligently works out what needs to be done (by checking the modification times of all the files involved).

`make` however isn't commonly used for Java development. This is partly due to the fact that building Java projects usually involves much more complicated activities than just running a set of commands. In addition to this, Java is all about creating cross-platform software - so we need to avoid the use of platform specific commands often used by `make`. For these reasons, we will instead be making use of the **Maven** build system. Take a look at the PDF document linked to above to find out more about Maven.  


# 
### Task 6: Template Project


Rather than you having to create a new project each time we start a workbook or assignment,
we will often provide you with an existing Maven template project (in order to help you get started).
These templates will contain all of the rules and dependencies required to build a project. One of the main benefits is that these projects can be imported directly into Intellij (or any other IDE for that matter). This will save us a lot of time installing required libraries and setting up the build environment.

For the next couple of workbooks, we will be using the <a href="IntelliJ Template/" target="_blank">shapes</a> template project.
You should copy the entire `cw-shapes` directory into your work folder (wherever you want to keep your code for this unit). In order to open an existing project in IntelliJ, simply click on the `Open` button on the IntelliJ startup screen (shown below) and pick the `cw-shapes` folder using the file dialog. When you have opened the project once, IntelliJ will remember it and show it in the list of recent projects.  


![](06%20Template%20Project/images/open.jpg)

# 
### Task 7: Command Line


In this workbook, we have spent the majority of the time exploring the use of IntelliJ.
At this stage however, it is worth spending a little bit of time taking a look at how we can build and run
Maven projects on the command line. This is important because the assessed exercises later on in the
unit will be marked on the command line and you need to be able to make sure that your code will actually build and run.

On your command line, change into the project root folder (the directory where the `pom.xml` file resides) and type one of the following commands (depending on your platform):

    Linux/OSX:   ./mvnw clean compile
    Windows:     mvnw clean compile

**Note**: On some unix systems, you _might_ need to make `mvnw` executable first
(i.e. by running `chmod +x mvnw`)

The command is doing 2 separate things in sequential order:

1. `clean` ensure that everything will be freshly built by cleaning out (deleting) all previously generated files
2. `compile` compiles and builds all files and resources (stopping if any build errors occur)

Note that you can learn more about these and other build tasks in the <a href="https://maven.apache.org/guides/getting-started/index.html">Maven documentation</a>.

The first time you run this command, it might take a while to complete so be patient. This is because Maven may need to download project dependencies from the web (obviously if you haven't got a working internet connection Maven may well fail at this point !)
If successful, the output should looks something like this:

    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 42 s
    [INFO] Finished at: 2022-01-01T00:00:00Z
    [INFO] ------------------------------------------------------------------------

If you get a `BUILD FAILURE`, it means the project failed to complete some of the specified tasks.
If this is the case, it might make sense to go back into IntelliJ and make sure no code issues are detected
(i.e. no code is highlighted in red and the project can still be built and run).
Fix any problems in IntelliJ and then return to the command line.

Once you have successfully built your code on the command line, you can run your program using:

    Linux/OSX:   ./mvnw exec:java
    Windows:     mvnw exec:java

Note that if you run the above command inside this week's project folder, Maven will run the `main` method from the `Shapes` class. Note that if you want to run the main method of _another_ class,
you can edit the project's `pom.xml` file and change the `mainClass` to be the desired class.
  


**Hints & Tips:**  
If when attempting to compile your code, you get a warning about version 17 source code,
you will probably need to update the `POM.xml` file in the project folder.
Run `java -version` on the command line to find out which version of Java you have installed
on your operating system. The template project was created assuming you have Java version 17
installed on your computer, if your version number differs, update the `maven.compiler.source`
and `maven.compiler.target` lines of the `POM.xml` file with your Java version number
(then try compiling again !)  


# 
### Task 8: Final Thoughts


This has been a relatively short and straight-forward workbook.
The main was to get you settled in before we start digger deeper into Java and OOP.
Make sure you finish all the practical activities in this workbook - you are going to
need to be able to use all of the tools and techniques in order to actually build
software in Java.  


# 
