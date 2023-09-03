This repository holds scaffolding for the "jminesweeter" interview assignment. 

Fork it to start working on your interview assignment.

# Working on the assignment

Please carefully read everything in this document as well as in the ![Requirements document](https://github.com/Voipfuture-GmbH/jminesweeper/blob/master/REQUIREMENTS.md) before beginning to work on the assignment as this will make sure that all applicants are facing the same challenges and solutions are comparable.

> [!WARNING]
> If anything in the provided documents is unclear to you, do not hesitate to ask me (tobias.gierke@voipfuture.com) for guidance.  

1. Assignment solutions that alter **any** of the files provided as part of this template project **except** for the ones listed below will be considered as INVALID unless those modifications have been accepted by Voipfuture Gmbh beforehand.

    The **only** files that are part of this template project and **may** be altered are 
    
        - run_server.sh
        - run_client.sh
        - server/src/main/resources/log4j2.xml
        - client/src/main/resources/log4j2.xml
        - server/src/main/java/com/voipfuture/jminesweep/server/Server.java

    Adding additional files to the 'server' submodule is of course permitted (and in fact required unless you want to cram all of your code into the com.voipfuture.jminesweep.server.Server class which I do not recommend unless you come up with a really short solution).

2. Total time spent on this assignment should be between 2-4 hours max (with 2 hours being more likely than 4 hours).
3. **Make sure to thoroughly read this whole file as well as the requirements before starting to work on the assignment**
4. While testing dependencies (JUnit, AssertJ, Mockito) are available to all submodules, you do **not** have to write any unit tests
unless you want to or need them for debugging 
5. Any non-testing code you're writing as part of your solution should be what you would consider "production-grade code" (as you would write it when working on the job,fully expecting that some unlucky colleagues or your future self years from now have to touch it again)
6. The main point of this assignment is to gauge applicant skill levels and have something to talk about during the interview process. 
This is not about whether braces go on the same or next line or writing the fastest implementation on earth (though being aware of performance tradeoffs made during development **is** part of software development best practices so questions related to this may come up)

# Build/development environment

Building **and** running this project requires 

- Java 20 (Eclipse Temurin has been used during development) as it relies on the "Project Panama" FFI 
to call POSIX termios functions which is still in preview stage.
- a Linux/POSIX environment (not sure if macOS is POSIX-compliant enough, so I suggest just firing up a Linux VM if weird
errors come up when running the console client application)
- Maven >= 3.9.1 for building

# Template project repository

This repository lays out the overall project structure, providing the necessary files to build and run both
the client and the server application. The assignment is about developing a server that is compatible
with the existing client.

# Template project files

The template project is a multi-module Maven project that has three modules:

- a client module
- a server module
- a shared module that holds code shared between client and server 

The Maven parent POM contributes all necessary settings (compiler level, compiler flags, version numbers etc.) 
as well as the following dependencies to every submodule:

1. Log4j2 for logging
2. Apache commons-lang v3 for general utility methods
3. JUnit 5 for testing
4. AssertJ for testing
5. Mockito 5 for testing

```
.
├── client (the console client)
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── voipfuture
│           │           └── jminesweep
│           │               └── client
│           │                   ├── Client.java (client entry point with main() function)
│           │                   ├── ConsoleHelper.java
│           │                   └── nativelinux (code generated using the "Project Panama" jextract tool) 
│           │                       ├── constants$0.java
│           │                       ├── constants$1.java
│           │                       ├── constants$2.java
│           │                       ├── Constants$root.java
│           │                       ├── __fsid_t.java
│           │                       ├── RuntimeHelper.java
│           │                       ├── termios_h.java
│           │                       └── termios.java
│           └── resources
│               └── log4j2.xml (log4j configuration used when the client is running)
├── pom.xml (parent POM that provides common project settings like dependencies, compiler options etc.)
├── README.md (this file)
├── run_client.sh (simple Bash script to run the console client)
├── run_server.sh (simple Bash script to run the game server)
├── server (the game server, this is where your work needs to go)
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── voipfuture
│           │           └── jminesweep
│           │               └── server
│           │                   └── Server.java (game server entry point with main() function)
│           └── resources
│               └── log4j2.xml
└── shared (code shared between client and server that should be used in your solution)
    ├── pom.xml
    └── src
        └── main
            └── java
                └── com
                    └── voipfuture
                        └── jminesweep
                            └── shared
                                ├── Constants.java (holds the TCP port the server is listening on) 
                                ├── Difficulty.java (enum of supported game difficulty levels)
                                ├── Direction.java (enum of cursor movement directions)
                                ├── NetworkPacketType.java (enum of supported network packet types)
                                ├── terminal (holds code to render the game screen using either ANSI sequences)
                                │   ├── IScreenRenderer.java (abstraction to be used in server)
                                │   ├── ANSIScreenRenderer.java (renders screen using ANSI sequences)
                                │   └── PlainTextScreenRenderer.java  (debugging only, renders screen using plain-text)
                                └── Utils.java (a few utility functions that may be of use)                                
```

# The assignment

It's your very first day as software engineer at Voipfuture. You eagerly open your email client's inbox and see only a single e-mail.

This is what it reads:

```
To: you <you@voipfuture.com>
From: Tobias Gierke <tobias.gierke@voipfuture.com>
Subject: Urgent !!!1

Hi,

I know that it's your first day and you're not up to speed yet but we have this REALLY SUPER IMPORTANT new project that needs just a few tiny finishing touches before it's released. Tobias was working on it and said its "almost complete" but he's on a 4-week vacation and the customer demands a first version RIGHT NOW. 

Can you please check the existing source code (just fork https://github.com/Voipfuture-GmbH/jminesweeper.git) for the missing parts and implement those ?
The requirements can be found here: https://github.com/Voipfuture-GmbH/jminesweeper/blob/master/REQUIREMENTS.md

Thank you very much !!!
