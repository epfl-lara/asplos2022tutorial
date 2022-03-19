# Verifying Programs with Stainless

[Stainless](https://stainless.epfl.ch/) is an [open-source system](https://github.com/epfl-lara/stainless) for constructing formally-verified software
that is guaranteed to meet specifications for all inputs.
The primary input format to Stainless is a subset of [Scala](https://www.scala-lang.org/).
In addition, programs designed to run with pre-allocated memory (e.g., on an embedded system)
can be translated to C and processed using conventional C compilers.

## About the tutorial

This tutorial provides a hands-on introduction to Stainless through a
series of guided examples. We will assume only basic programming skills; no
particular background in verification or Scala is required, though a basic
understanding of functional programming concepts will be helpful.

## Program Schedule

[Click here for the slides](https://docs.google.com/presentation/d/1Bw56NKnWJz-anuTDpYzGlMuxZS0wFB6GptDFrcQdfuY/edit?usp=sharing)

| Time      | Topic                                      |
|----------:|--------------------------------------------|
|  9:00-9:45| Part 1: Introduction (Georg S. Schmid, [video](https://tube.switch.ch/videos/3EithGWw56))                  |
| 9:45-11:00| Part 2: Using stainless (Viktor Kunčak, [video](https://tube.switch.ch/videos/vbYr4RfXgr))                |
|11:30-12:15| Part 3: How Stainless works (Nicolas Voirol, [video](https://tube.switch.ch/videos/fQGpcbxKcs))           |
|12:15-12:50| Part 4: An Extended Verification Example (Mario Bucev, [video](https://tube.switch.ch/videos/bFKnOEBa8Y)) |                    |

# More in-depth resources:

- [Stainless documentation](https://epfl-lara.github.io/stainless/) including the [basic mini tutorial](https://epfl-lara.github.io/stainless/tutorial.html)
- [Verified Functional Programming](http://dx.doi.org/10.5075/epfl-thesis-9479), EPFL PhD thesis of Nicolas Voirol, 2019
- [Formal Verification Course](https://tube.switch.ch/channels/f2d4e01d) taught by LARA group at EPFL
- [System FR: Formalized Foundations for the Stainless Verifier](http://lara.epfl.ch/~kuncak/papers/HamzaETAL19SystemFR.pdf), OOPSLA 2019
- [verified-qoi](https://github.com/epfl-lara/verified-qoi), the full version of the QOI case study!
- [On Verified Scala for STIX File System Embedded Code using Stainless](https://infoscience.epfl.ch/record/292424?&ln=en)

For high-level overview you can also [watch a keynote from Lambda Days](https://www.youtube.com/watch?v=dkO59PTcNxA) or check a different and shorter [tutorial at FMCAD 2021](https://github.com/epfl-lara/fmcad2021tutorial/) including [a video](https://tube.switch.ch/videos/bFOnl6Emmp).

## Setup
- Make sure you have the following installed:
    - JDK8 or higher
        - If you do not have Java installed, you can download the latest release (JDK 17) [here](https://www.oracle.com/java/technologies/downloads/)
    - For Windows users: [Cygwin](https://cygwin.com/install.html)\
    When arriving at the package selections, select `gcc-core` and `make`.
    Once installed, you may launch a Cygwin terminal. All listed commands should be run in a Cygwin terminal.
    - GCC and Make
        - For Windows users: already installed along with Cygwin.
        - For macOS users: note that `gcc` is (usually) an alias for `clang`. One of our last examples requires a crucial optimization that sadly the versions of Clang we tried could not perform.\
            With Homebrew, you can install GCC with `brew install gcc`.\
            With MacPorts, GCC can be installed with `sudo port install gcc11`.\
            In either case, the installation may take some time.
        - For Linux users: these should already be bundled.
- Clone the tutorial repository anywhere you would like:
    ```bash
    git clone https://github.com/epfl-lara/asplos2022tutorial
    ```
    If you do not use Git, you can instead download the [archive](https://github.com/epfl-lara/asplos2022tutorial/archive/refs/heads/main.zip).
- Download Stainless 0.9.3:
    - [For Linux](https://github.com/epfl-lara/stainless/releases/download/v0.9.3/stainless-dotty-standalone-0.9.3-linux.zip)
    - [For macOS](https://github.com/epfl-lara/stainless/releases/download/v0.9.3/stainless-dotty-standalone-0.9.3-mac.zip)
    - [For Windows](https://github.com/epfl-lara/stainless/releases/download/v0.9.3/stainless-dotty-standalone-0.9.3-win.zip)
- Create a folder `stainless` under `asplos2022tutorial` and unzip the downloaded Stainless 0.9.3 zip file into it (if the unzipping creates an intermediary folder, the content should be flattened). The script `asplos2022tutorial/stainless/stainless.sh` invokes stainless verification tool.\
You should have the following directory structure:
```
asplos2022tutorial
├── stainless
│   ├── lib/
│   ├── z3/
│   ├── stainless.sh
│   └── stainless.conf
├── 00-hello-stainless/
├── 01-zip/
├── 02-stack/
└── ...
```

- To test the installation, navigate to `00-hello-stainless` and run the `verify.sh` script or invoke `stainless.sh` on `HelloStainless.scala` (in this case with `--no-colors` option for 7-bit ASCII output):

    ```
    ~/asplos2022tutorial/00-hello-stainless$ ../stainless/stainless.sh --no-colors HelloStainless.scala
    Starting verification...
    Verified: 1 / 1

    stainless summary

    HelloStainless.scala:5:7:     nonEmptyListSize   postcondition   valid from cache      0.1
    ............................................................................................
    total: 1    valid: 1    (1 from cache) invalid: 0    unknown: 0    time:     0.1

    Shutting down executor service.
    ```

- (Optional) If you use VS Code, you may install the [Scala syntax highlighting plugin](https://marketplace.visualstudio.com/items?itemName=scala-lang.scala).\
IntelliJ users may install the Scala plugin if they wish. However, as we do not use SBT (Scala Build Tool) for this tutorial, IntelliJ may struggle to import the projects.

## Live tutorial for which this content was created

The tutorial was originally given as part of ASPLOS 2022 conference on Tuesday, March 1st, at 09:00am-13:00 Lausanne (and Paris and Zurich) time zone. It was available on zoom as well as physically in [Swiss Tech Convention Center](https://www.stcc.ch/) near the campus of EPFL in Lausanne, Switzerland ([M1 Metro stop EPFL](https://goo.gl/maps/A3Sm4VGxWsoPrzX27))
