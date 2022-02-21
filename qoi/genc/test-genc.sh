#!/bin/bash

make genc-qoiconv
mkdir -p ../output

# Encoding
./genc-qoiconv ../images/Central_Bern_from_north.png ../output/Central_Bern_from_north.qoi
cmp -l ../images/Central_Bern_from_north.qoi ../output/Central_Bern_from_north.qoi

./genc-qoiconv ../images/Chocolate_Hills_overview.png ../output/Chocolate_Hills_overview.qoi
cmp -l ../images/Chocolate_Hills_overview.qoi ../output/Chocolate_Hills_overview.qoi

./genc-qoiconv ../images/Eyjafjallajokull_sous_les_aurores_boreales.png ../output/Eyjafjallajokull_sous_les_aurores_boreales.qoi
cmp -l ../images/Eyjafjallajokull_sous_les_aurores_boreales.qoi ../output/Eyjafjallajokull_sous_les_aurores_boreales.qoi


# Decoding
./genc-qoiconv ../images/Central_Bern_from_north.qoi ../output/Central_Bern_from_north.png
./genc-qoiconv ../images/Chocolate_Hills_overview.qoi ../output/Chocolate_Hills_overview.png
./genc-qoiconv ../images/Eyjafjallajokull_sous_les_aurores_boreales.qoi ../output/Eyjafjallajokull_sous_les_aurores_boreales.png
