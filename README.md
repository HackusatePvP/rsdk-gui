# RSDK-GUI
This is a GUI version of RSDK. This is currently a demo/rough draft of how I want it to work. This looks terrible and is designed terrible.

## Automation
The application will automate the install of RenJava projects, similar to RSDK. This takes it a step further by automatically downloading, extracting, and compiling RenJava. It will also setup your development environment and even compile a running jar of your project.

THIS IS NOT AN OFFLINE INSTALLER! Internet connection is required for the automated parts of RenJava. No data is collected, sold, or used by me. GitHub may collect data when using the cloning function of the site.

## Requirements
You need to have java 21 installed on your pc to run the jar file.

Double-click jar file or run java command in command prompt.
`java -jar rsdk-gui.jar`

## Linux
Not supported on linux as it uses windows commands. Linux will have support later.

## RenEngine
This is built with [RenEngine](https://github.com/HackusatePvP/RenEngine). RenEngine is the engine from RenJava that was yanked out. Pulling out the engine caused most of its awesome functionality to be stripped away.
This is because the engine was tied with the api. There are no listeners even though the class still exists. Events have to be manually called and handled. It runs on hope glue and tears.


## Issues
Only report serious bugs. Refrain from requesting features or giving suggestions. This whole thing will be re-coded at some point with some effort into the design.