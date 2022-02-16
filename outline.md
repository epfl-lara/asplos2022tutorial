# Stainless tutorial @ ASPLOS 2022: Outline

## Intro (Georg)

- Splash page
  - Name of tutorial
  - Tutorial page link
  - Note that that page has installation instructions
- What is Stainless (a verifier with ...)
- What is Stainless good for (some use cases)
  - filesystem (thanks to GenC!)
  - data structures (functional programming is good!)
  - actor systems (a good way to do concurrency)
  - blockchain (verified Solidity contracts, and lightclient)
  - various soundness proofs (like System F)
- Theme for the day: QOI
  - "A neat image compression algo"
  - What one might want to verify about it
    - The usual: memory/OOB safety
    - A strong property: encode and decode are inverse of another
- Installation of Stainless
  - Explain what's part of the package
  - Point to releases for Linux and OSX (use WSL for Windows)
  - Step-by-step:
    - Download archive
    - Unpack in one folder
    - Run hello world
  - *Say we'll take a 5 minute break to catch up with everyone who wants to install*
- Basic operation of Stainless
  - Basic data in QOI example, e.g., RGBA value manipulation and bit-twiddling
  - Prove some simple properties about `fromRGBA` and `incremented`

## break 15 min, around 10am

## More examples? (Viktor)
  - examples from FMCAD
      * diffs (fold version)
      * amortized queue
      * binary search and google bug
  - current strengths of Stainless and comparison to other tools

## official BREAK at 11am

## How Stainless works (Nicolas)

*TBD*, see Google slides

## Bigger development (Mario): at noon
  - Small demonstration with GenC: conversion from .png to .qoi and .qoi to .png
    - Briefly mention that the GenC version is ~15-20% slowing in decoding and ~35-40% in encoding compared to the reference implementation.
  - Quick overview of the structure of the project
  - Show the main result: `decode . encode = id`
    - Have an overview of the proofs (e.g. lemmas, and that functions related to encoding contain properties about decoding, but not vice-versa)
  - Example of a property of `encodeNoRun`
    - Note: for the tutorial, we should have a stripped-down version of that function and remove properties outside of the example.<br/>
    **/!\\** We also must be sure it verifies with Z3 (and not rely on CVC4)
    - Property: essentially the "inverse" of `doDecodeNext`, and we are guaranteed to not get a "run chunk" back
    - With a small amount of annotations, we can get that to verify

## Conclusion?

*TBD*