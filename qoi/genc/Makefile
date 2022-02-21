.PHONY: genc

CC=gcc # Clang seems to not be able to eliminate tail-calls present in decodeLoop and encodeLoop
       # For, macOS users, gcc is an alias to clang, make sure to have gcc installed!
CFLAGS=-O3

all: genc genc-qoibench genc-qoiconv

stainless.c:
	../../stainless/stainless.sh ../scala/common.scala ../scala/decoder.scala ../scala/encoder.scala --genc

genc: stainless.c

genc-qoibench: stainless.c genc-qoibench.c genc-bridge.c

genc-qoiconv: stainless.c genc-qoiconv.c genc-bridge.c

clean:
	rm -f genc-qoibench genc-qoiconv stainless.c stainless.h