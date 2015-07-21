
# $1 = ant build file name
# $2 = copy to folder name
# mkdir $2
ant -f $1
cp dist/libaparapi_x86_64.so dist.$2
