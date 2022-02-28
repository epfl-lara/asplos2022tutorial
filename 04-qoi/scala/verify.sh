#!/bin/bash

# kill subprocesses on exit or kill
trap '[ -n "$(jobs -pr)" ] && kill -9 $(jobs -pr)' SIGINT SIGTERM EXIT

# For this exercise, we are only interested in verifying encodeNoRun (we also include encodeNoRunProp and doDecodeNext since encodeNoRun depends on them, to ensure they are well-formed)
../../stainless/stainless.sh common.scala decoder.scala encoder.scala --functions=encodeNoRun,encodeNoRunProp,doDecodeNext "$@"