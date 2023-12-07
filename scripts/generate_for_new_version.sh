# this script attempts to do some of the semi-manual work
# of version updates
# what it does:
#   copies over the relevant files
#   changes the spigot version in the new pom
#   changes the package artifactId and name in the new pom
#   moves the new package to correct folder
#   fixes the package in the new class
# though some things:
#   new obfuscated names
#   new version enum
#   new version usage (in NMSResolver)
#   add to main pom
# need to still be handled manually

# the script expects three inputs:
#  the copy-from folder name (e.g v1_20_R2)
#  the copy-to folder name (e.g v1_20_R3)
#  the spigot-version tag (e.g 1.20.2-R0.1-SNAPSHOT)
# PS: The expectation is that the target folder name
#     also matches the required package name
COPY_FROM=$1
COPY_TO=$2
SPIGOT_TAG=$3

echo "Copying what's in "$COPY_FROM" into "$COPY_TO

cp -r $COPY_FROM $COPY_TO

echo "Updating the spigot version in pom to "$SPIGOT_TAG

sed -i "s|<used.spigot.version>.*</used.spigot.version>|<used.spigot.version>$SPIGOT_TAG</used.spigot.version>|g" $COPY_TO/pom.xml

echo "Updating the package artifactId and name in the new pom"

sed -i "s/$COPY_FROM/$COPY_TO/g" $COPY_TO/pom.xml

echo "Moving the new package to correct folder"

mv $COPY_TO/src/main/java/dev/ratas/aggressiveanimals/aggressive/nms_$COPY_FROM/ $COPY_TO/src/main/java/dev/ratas/aggressiveanimals/aggressive/nms_$COPY_TO/

echo "Fixing package in new module's class"

sed -i "s/$COPY_FROM/$COPY_TO/g" $COPY_TO/src/main/java/dev/ratas/aggressiveanimals/aggressive/nms_$COPY_TO/NMSAggressivitySetter.java
