#!/bin/bash

dayNum=$(date +'%-d')
dayNumPadded=$(date +'%0d')

packageName="day$dayNumPadded"
filePathName="src/$packageName/Day$dayNumPadded"
mainFile="$filePathName.kt"
runFile=".run/Day${dayNumPadded}.run.xml"

# Make package
mkdir "src/$packageName"

# Copy template file and create input files
cp "template/Day.tmp" $mainFile
cp "template/run.tmp" $runFile
touch "$filePathName.txt"
touch "${filePathName}_test.txt"

# Replace placeholders in templates
sed -i '' "s/%PACKAGE/$packageName/g" $mainFile
sed -i '' "s/%DAY/$dayNum/g" $mainFile
sed -i '' "s/%DAY_NUM/$dayNumPadded/g" $runFile
sed -i '' "s/%CLASS_NAME/day${dayNumPadded}.Day${dayNumPadded}Kt/g" $runFile
