#!/bin/bash

# Copyright 2020 Peter Akey
# Script to initiate PROG with a begin and end range
# Optional: supply the number of instances to kick off

PROG="AESbruteforce"
export CLASSPATH="$(pwd)/commons-lang3-3.9.jar:$(pwd)/"
printf "Compiling the program..."; javac ${PROG}.java && printf "Done\n";

# If $1 has a value let's use it; otherwise, default to 6
[[ ! -z $1 ]] && THREADS=$1 || THREADS=6

# Get the BIG number
BIG=$(echo 2^37 | bc);
# Divide by the number of times you want to run the subordinate program
DIV=$(($BIG / $THREADS))

for ((i=0;i<=THREADS;i++)); do
  lower=$((i*DIV))
  upper=$((i*DIV+(DIV-1)));
  if [[ $upper -gt $BIG ]]; then
    upper=$BIG;
  fi
  printf "Starting brute force in range $lower - $upper\n";
  java $PROG $lower $upper &
done