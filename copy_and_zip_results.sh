mkdir -pv ./results/darksoft/FFAE
mkdir -pv ./results/mister
mkdir -pv ./results/mame

cp -v ./data/sz3.11m ./build1234abcd/mister/snd/ffae.11
cp -v "./data/Final Fight AE CPS2 Patch.mra" "./results/mister/Final Fight AE CPS2 Patch.mra"

zip -v -j ./results/mister/ffightae_cps2_gfx.zip ./build1234abcd/mister/gfx/* 
zip -v -j ./results/mister/ffightae_cps2_smp.zip ./build1234abcd/mister/snd/*

cp -v ./data/sz3.11m ./build1234abcd/darksoft/ffae.03
cp -v ./data/key ./build1234abcd/darksoft/key
cp -v ./data/NAME ./build1234abcd/darksoft/NAME

cp -rv ./build1234abcd/darksoft/* ./results/darksoft/FFAE
cp -v ./data/sz3.11m ./build1234abcd/mame/sz3.11m
cp -v ./data/phoenix.key ./build1234abcd/mame/phoenix.key

zip -v -j ./results/mame/ffightae_cps2.zip -v ./build1234abcd/mame/*
