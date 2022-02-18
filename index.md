# Verifying Programs with Stainless

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

## Location and date
**Location:** [Swiss Tech Convention Center](https://www.stcc.ch/) near the campus of EPFL in Lausanne, Switzerland ([M1 Metro stop EPFL](https://goo.gl/maps/A3Sm4VGxWsoPrzX27))

**Time:** Tuesday, March 1st, morning

## Setup
- Make sure you have the following installed:
    - JDK8 or higher
    - GCC (needed later on)
        - For macOS users: note that `gcc` is (usually) an alias for `clang`. One of our last example requires a crucial optimization that sadly the versions of `clang` we tried could not perform.\
        If you have Homebrew, you can install `gcc` with `brew install gcc` (which may take some time).
- Clone the tutorial repository anywhere you would like:
    ```bash
    git clone https://github.com/epfl-lara/asplos2022tutorial
    ```
- Download Stainless 0.9.2:
    - [For Linux](https://github.com/epfl-lara/stainless/releases/download/v0.9.2/stainless-dotty-standalone-0.9.2-scala3-linux.zip)
    - [For macOS](https://github.com/epfl-lara/stainless/releases/download/v0.9.2/stainless-dotty-standalone-0.9.2-scala3-mac.zip)
    - [For Windows](https://www.youtube.com/watch?v=dQw4w9WgXcQ)
- Unzip the downloaded file and move the folder within the `asplos2022tutorial` folder, previously cloned.\
Rename `stainless-dotty-standalone-0.9.2-XYZ` to `stainless-standalone`.
- To test the installation, navigate to `hello-stainless` and run the `verify` script.\
You should obtain something like:
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

- If so, you are ready to go. Otherwise, do not hesitate to call us to figure out what went wrong!
- (Optional) If you use VS Code, you may install the [Scala syntax highlighting plugin](https://marketplace.visualstudio.com/items?itemName=scala-lang.scala).\
IntelliJ users may install the Scala plugin if they wish. However, as we do not use SBT (Scala Build Tool) for this tutorial, IntelliJ may struggle importing the projects.

## Program Schedule

| Time   | Topic                            |
|--------|----------------------------------|
|morning | Basics of Stainless              |
|        | Verifying imperative code        |
|        | Proof control                    |
|        | An Extended Verification Example |
|        | Features, Tips, and Case Studies |
