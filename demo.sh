#!/usr/bin/bash

duree=$1

while true ; do
	j1=$(( ( RANDOM % 13 )  + 1 ))
	j2=$(( ( RANDOM % 13 )  + 1 ))
	bash lancer-partie.sh $j1 $j2
done
