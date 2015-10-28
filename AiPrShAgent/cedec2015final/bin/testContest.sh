#!/usr/bin/bash


base_dir="/home/tori/prog/wolf/cedec2015final/"
data_dir=${base_dir}"/data"
files=${data_dir}"/*.csv"
archive_dir=${base_dir}"/archive/"
ws_dir=${base_dir}"/workspace/"
lib_dir=${base_dir}"/lib"

contest_lib=${lib_dir}"/Contest2015.jar"
tool_lib=${lib_dir}"/tolib.jar"
inb_lib=${lib_dir}"/InabaPlayerV3.jar"
sql_lib=${lib_dir}"/mysql-connector-java-5.1.15-bin.jar"

test_property_file=${base_dir}"/test.contest.property"
player_file=${ws_dir}"player.dat"
web_dir=${base_dir}"/result"
media_dir="/media/sf_Agents/"

cd $ws_dir
pwd
cp ${media_dir}/*.jar ${lib_dir}
/usr/bin/java -Xmx4g -cp $contest_lib:$tool_lib:$sql_lib:$inb_lib org.aiwolf.contest.bin.FinalStarter $test_property_file $data_dir $ws_dir $web_dir 

