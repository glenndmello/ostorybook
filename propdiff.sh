#! /bin/bash
#
# br, Apr 24, 2017
#
# Output a list of different fields between a reference property file and
# a given one.
#
# SYNOPSIS
#   propdiff.sh [-a][-r REFFILE] PROPFILE
#
# DESCRIPTION
#   Shows missing lines in a property file (java style) vis-à-vis a reference
#   file (default: messages.properties in current directory)
#
# OPTIONS
#   -a
#       Outputs lines missing in both files (with "+" when missing in PROPFILE,
#       and "-" when missing in REFFILE.
#   -r REFFILE
#       Uses REFFILE instead of default ./messages.properties
#   PROPFILE: The file to be compared with REFFILE

REFFILE=./messages.properties
SHOWALL=n

usage() {
	echo "usage: $0 [-a][-r reffile] propfile"
	exit 1
}


# parse arguments
while getopts ar: todo
do
	case "${todo}" in
		a)  SHOWALL=y;;
		r)  REFFILE="$OPTARG";;
        *)  usage;;
    esac
done

shift $(($OPTIND - 1))
[ $# -ne 1 ] && usage
PROPFILE=$1

#echo SHOWALL=$SHOWALL
#echo REFFILE=$REFFILE
#echo PROPFILE=$PROPFILE

# create temp files
REFTMP=$(mktemp)
PROPTMP=$(mktemp)

# Fill temp files with sorted keywords

sed -nr 's/^[[:space:]]*([[:alnum:]\.]+)[[:space:]=]+([[:alnum:]\.]+).*$/\1/p' ${REFFILE} | sort > ${REFTMP}
sed -nr 's/^[[:space:]]*([[:alnum:]\.]+)[[:space:]=]+([[:alnum:]\.]+).*$/\1/p' ${PROPFILE} | sort > ${PROPTMP}

if [ ${SHOWALL} = y ]
then
	# output missing lines in both files
	comm -23 ${REFTMP} ${PROPTMP} | sed 's/^/+/'
	comm -13 ${REFTMP} ${PROPTMP} | sed 's/^/-/'
else
	comm -23 ${REFTMP} ${PROPTMP}
fi

rm ${REFTMP} ${PROPTMP}

exit 0
