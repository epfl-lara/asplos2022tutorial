# Verifying Programs with Stainless

## Location and date
**Location:** Zoom (link available to participants), and physically in [Swiss Tech Convention Center](https://www.stcc.ch/) near the campus of EPFL in Lausanne, Switzerland ([M1 Metro stop EPFL](https://goo.gl/maps/A3Sm4VGxWsoPrzX27))

**Time:** Tuesday, March 1st, at 09:00am-13:00 Lausanne (and Paris and Zurich) time zone

## About the tutorial
[Stainless](https://stainless.epfl.ch/) is an [open-source system](https://github.com/epfl-lara/stainless) for constructing formally-verified software
that is guaranteed to meet specifications for all inputs.
The primary input format to Stainless is a subset of [Scala](https://www.scala-lang.org/).
In addition, programs designed to run with pre-allocated memory (e.g., on an embedded system)
can be translated to C and processed using conventional C compilers.

This tutorial will provide a hands-on introduction to Stainless through a
series of guided examples. We will assume only basic programming skills; no
particular background in verification or Scala is required, though a basic
understanding of functional programming concepts will be helpful.


To get the basic flavor of Stainless, you can also consult the documentation including [this basic tutorial](https://epfl-lara.github.io/stainless/tutorial.html) or [watch a keynote from Lambda Days](https://www.youtube.com/watch?v=dkO59PTcNxA). The tutorial will also cover examples not presented previously that illustrate some of the recent features.

## Program Schedule


| Time      | Topic                                      |
|----------:|--------------------------------------------|
|  9:00-9:45| Introduction & setup                       |
| 9:45-11:00| Basics of Stainless & examples             |
|11:00-11:30| Break                                      |
|11:30-12:15| How Stainless works                        |
|12:15-12:50| An Extended Verification Example           |
|12:50-13:00| Conclusion                                 |

# More in-depth resources:

- [Stainless documentation](https://epfl-lara.github.io/stainless/)
- [System FR: Formalized Foundations for the Stainless Verifier](http://lara.epfl.ch/~kuncak/papers/HamzaETAL19SystemFR.pdf), OOPSLA 2019
- [Verified Functional Programming](http://dx.doi.org/10.5075/epfl-thesis-9479), EPFL PhD thesis of Nicolas Voirol, 2019
- [verified-qoi](https://github.com/epfl-lara/verified-qoi), the full version of the QOI case study

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
- Create a folder `stainless` under `asplos2022tutorial` and unzip the downloaded Stainless 0.9.3 zip file into it. The script `asplos2022tutorial/stainless/stainless.sh` invokes stainless verification tool.
- To test the installation, navigate to `hello-stainless` and run the `verify.sh` script.
You should obtain the following output:
```
[  Info  ] Starting verification...
[  Info  ] Verified: 1 / 1
[  Info  ]   ┌───────────────────┐
[  Info  ] ╔═╡ stainless summary ╞══════════════════════════════════════════════════════════════════════╗
[  Info  ] ║ └───────────────────┘                                                                      ║
[  Info  ] ║ HelloStainless.scala:5:7:      nonEmptyListSize    postcondition    valid   U:smt-z3   0.4 ║
[  Info  ] ╟┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄╢
[  Info  ] ║ total: 1    valid: 1    (0 from cache) invalid: 0    unknown: 0    time:     0.4           ║
[  Info  ] ╚════════════════════════════════════════════════════════════════════════════════════════════╝
[  Info  ] Shutting down executor service.
```

If symbols like ╚,═ or colors do not work in your terminal, just use option ``--no-colors`` on all stainless invocations. This will emit pure ASCII output on Linux (adapt to Windows using \ instead of / , as needed):
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
