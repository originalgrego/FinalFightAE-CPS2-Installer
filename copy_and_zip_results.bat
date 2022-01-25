mkdir results
mkdir results\mister
mkdir results\darksoft
mkdir results\darksoft\FFAE
mkdir results\mame



copy data\sz3.11m build1234abcd\mister\snd\ffae.11
copy "data\Final Fight AE CPS2 Patch.mra" "results\mister\Final Fight AE CPS2 Patch.mra"

java -jar RomMangler.jar zipdir build1234abcd\mister\gfx results\mister\ffightae_cps2_gfx.zip
java -jar RomMangler.jar zipdir build1234abcd\mister\snd results\mister\ffightae_cps2_smp.zip


copy data\sz3.11m build1234abcd\darksoft\ffae.03
copy data\key build1234abcd\darksoft\key
copy data\NAME build1234abcd\darksoft\NAME

copy build1234abcd\darksoft\*.* results\darksoft\FFAE


copy data\sz3.11m build1234abcd\mame\sz3.11m
copy data\phoenix.key build1234abcd\mame\phoenix.key

java -jar RomMangler.jar zipdir build1234abcd\mame results\mame\ffightae_cps2.zip
