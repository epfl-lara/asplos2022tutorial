# Quite OK Image case study

For this last exercise, we will interest ourselves in verifying some properties on a Scala implementation of the "Quite OK Image" (QOI) lossless compression algorithm.

Furthermore, we will run the GenC pipeline of Stainless to transpile the Scala code into C. We will then compare the performance of the generated C to the reference implementation.

## Preamble: the algorithm
Dominic Szablewski invented QOI and had presented the first version in a [blog post](https://phoboslab.org/log/2021/11/qoi-fast-lossless-image-compression) in November 2021.
In December 2021, with many people on GitHub, he revised [QOI and brought some simplifications](https://phoboslab.org/log/2021/12/qoi-specification).

We describe here the gist of QOI (the exact details of the format are not needed).

The compression algorithm maintains the following values:
- The previous pixel (initialized to R=G=B=0, A=255).
- `index`, an array of 64 pixels, all initialized to 0.
- A `run` variable, indicating the number of consecutive equal pixels.

We iterate over all pixels and encode a visited pixel in one of the following four ways (**A**, **B**, **C** or **D**):
- **Case A**. If the visited pixel is the same as the previous one, we increment the `run` variable. The `run` value may only go up to 62. Should we attain 62, we encode the `run` value and reset `run` to 0.
- Otherwise, we compute a position `i` based on the pixel's value and check if we find it in `index`.
	- **Case B**. If so, we encode `i` and proceed with the next pixel.
	- If not, we write the visited pixel into `index` at position `i`.\
	Then, we check if the previous pixel and the current pixel are "close enough".
    If they are, we encode their difference (**case C**), otherwise we write the full RGBA value (**case D**).

Decompression is single-pass as well. It maintains the same values as the compression counterpart, and iterates over all encoded chunks and applies the reverse transformation.

## Directory structure
**TODO**

## Exercise: verifying the reciprocity of cases B, C and D
**TODO**

## Emitting C from Scala code with GenC
**TODO**

## Aside: fully verified?

As you may have guessed, the case study here does not contain all proof annotations needed to conclude that the algorithm is correct (namely, that decoding is the inverse of encoding).
Besides, if we run Stainless on all functions, it will report counter-examples for most of them because their contracts (in particular, preconditions) are not entirely specified.

We however do have a version with all proof annotations:
https://github.com/epfl-lara/verified-qoi

We have stripped the full version from all of its annotations for the needs of the tutorial, and have adjusted it to not use Scala-specific features.