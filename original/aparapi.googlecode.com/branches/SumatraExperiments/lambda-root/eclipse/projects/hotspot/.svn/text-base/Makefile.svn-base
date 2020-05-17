TO=../../../lambda/build/linux-amd64/lib/amd64/server
FROM=../../../lambda/build/linux-amd64/hotspot/outputdir/linux_amd64_compiler2/fastdebug

all:
	#rm -f ${FROM}/libjvm.so ${FROM}/libjvm.diz ${FROM}/libjvm.debuginfo ${FROM}/libjvm_g.debuginfo
	LANG=C ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64 ALLOW_DOWNLOADS=true CORES=6 HOTSPOT_BUILD_JOBS=6 PARALLEL_COMPILE_JOBS=6 make -C ../../../lambda DEBUG_NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true hotspot
	rm -f ${TO}/libjvm.so ${TO}/libjvm.diz ${TO}/libjvm.debuginfo ${TO}/libjvm_g.debuginfo
	cp ${FROM}/libjvm.so ${FROM}/libjvm_g.so ${TO}
	unzip -d ${TO} ${FROM}/libjvm.diz 
	ls -l ${TO}/libjvm.debuginfo ${TO}/libjvm_g.debuginfo

clean:
	LANG=C ALT_BOOTDIR=/usr/lib/jvm/java-7-openjdk-amd64 ALLOW_DOWNLOADS=true CORES=4 HOTSPOT_BUILD_JOBS=4 PARALLEL_COMPILE_JOBS=4 echo make -C ../../../lambda _NAME=fastdebug NO_DOCS=true NO_IMAGES=true DEV_ONLY=true clean
