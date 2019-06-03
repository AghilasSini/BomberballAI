#!/bin/bash

read -r -d '' CLASSES <<EOLIST
1	RandomAI
2	VanillaAI
3	Alpha
4	Bravo
5	Charlie
6	Delta
7	Echo
8	Foxtrot
9	Golf
10	Hotel
11	India
12	Juliett
13	Kilo
14	Lima
15	Mike
16	November
17	Oscar
18	Papa
EOLIST

j1=`echo "$CLASSES" | grep -P "^$1" | cut -f2`
j2=`echo "$CLASSES" | grep -P "^$2" | cut -f2`

echo "Joueur 1 : $j1"
echo "Joueur 2 : $j2"

java -cp "lib/aparapi.jar:lib/json-simple-1.1.1.jar:bin" Twixt $j1 $j2
