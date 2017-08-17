#! /bin/bash
#
# br, Apr 24, 2017
#
# Output a list of different fields between a reference property file and
# a given one.
#
# SYNOPSIS
#   propdiff.sh [-a][-b|-i][-d][-r][-s][-w][-R REFFILE] [PROPFILE...]
#
# DESCRIPTION
#   Shows missing lines in a property file (java style) vis-à-vis a reference
#   file (default: messages.properties in current directory).
#   PROPFILE are the files to be compared with reference file. If no PROPFILE is
#   given, all files (in current directory) named "messages_*.properties"
#   will be used. In that case, "###### PROPFILE" will be output between each
#   file (except if -w option is used - see below).
#
# OPTIONS
#   -b
#       Outputs lines missing in both files (with "+" when missing in PROPFILE,
#       and "-" when missing in REFFILE. See -w option.
#       Cannot be used with -i option.
#   -d
#       Output debug information on stderr.
#   -i
#       Inverts output: Only PROPFILE lines not present in REFFILE are displayed.
#       See -w option.
#       Cannot be used with -b option.
#   -R REFFILE
#       Uses REFFILE instead of default ./messages.properties
#   -r
#       Removes lines in PROPFILE which are not in REFFILE. A backup file (with
#       tilde (~) appened to filename) will be created before the changes.
#       This implies the -i or -b option.
#   -s
#       Output will include full missing source lines (a=b instead of a).
#       This option is time consuming with current implementation.
#   -w
#       Creates ".TODO" and/or ".TOREMOVE" files, depending on other options
#       used (-b, -i) instead of stdout output.
#       This option should typically be used with -s.

# default values
REFFILE=./messages.properties
SHOWALL=n
DEBUG=n
SHOWEXTRATAGS=n
REMOVEEXTRA=n
SHOWFULL=n
WRITEDIFF=n
CURDIR=.
# temp files
REFTMP=$(mktemp)
PROPTMP=$(mktemp)
DIFF1=$(mktemp)
DIFF2=$(mktemp)

function cleanup() {
	rm -f ${REFTMP} ${PROPTMP} ${DIFF1} ${DIFF2}
}

function usage() {
	echo "usage: $0 [-a][-r reffile] propfile"
	cleanup
	exit 1
}

function debug () {
	if [ ${DEBUG} = y ]; then
		>&2 echo $@
	fi
}

function fatal () {
	>&2 echo $@
	cleanup
	exit 1
}

# parse arguments
while getopts bD:diR:rsw todo
do
	case "${todo}" in
		b) SHOWALL=y;;
		D) CURDIR="$OPTARG";;
		d) DEBUG=y;;
		i) SHOWEXTRATAGS=y;;
		R) REFFILE="$OPTARG";;
		r) REMOVEEXTRA=y;;
		s) SHOWFULL=y;;
		w) WRITEDIFF=y;;
        *) usage;;
    esac
done

if [ ${SHOWALL} = y -a ${SHOWEXTRATAGS} = y ]; then
	fatal "Options -b and -i are mutually exclusive"
fi

if [ ! -d $CURDIR ]; then
	fatal "$CURDIR is not a directory"
fi
cd ${CURDIR}

shift $(($OPTIND - 1))
if [ $# -ge 1 ]; then
	PROPFILE=($*)
else
	PROPFILE=(messages_[a-z]*.properties)
fi
debug PROPFILE=${PROPFILE[*]}


# Fill temp file with reference sorted keywords
sed -nr 's/^[[:space:]]*([[:alnum:]\.]+)[[:space:]=]+([[:alnum:]\.]+).*$/\1/p' ${REFFILE} | sort | uniq > ${REFTMP}

# main loop
for propfile in ${PROPFILE[*]}; do
	# as a precaution we backup file first
	if [ $WRITEDIFF = y ]; then
		echo backuping ${propfile} to ${propfile}"~"
	fi

	# Fill temp file with sorted keywords
	sed -nr 's/^[[:space:]]*([[:alnum:]\.]+)[[:space:]=]+([[:alnum:]\.]+).*$/\1/p' ${propfile} | sort | uniq > ${PROPTMP}

	# find missing lines in both files
	comm -23 ${REFTMP} ${PROPTMP} > ${DIFF1}
	comm -13 ${REFTMP} ${PROPTMP} > ${DIFF2}

	if [ ${SHOWFULL} = y ]; then
		# we get the full values of keywords
		for key in $(cat ${DIFF1}); do
			VAL=$(sed -nr "s/^[[:space:]]*${key}[[:space:]*=]+([[:alnum:]].*)$/\1/p" ${REFFILE} | head -1)
			debug "value(${key}) -> ${VAL}"
			# we need to escape special characters: &, / and \ in subst string
			VALESC=$(sed 's/[&/\]/\\&/g' <<<"${VAL}")

			# we add the escaped value
			sed -i "/^${key}$/ s/\$/=${VALESC}/" ${DIFF1}
		done
	fi

	# now, we decide what to do with our data: Create TODO and TOREMOVE
	# files, or direct output
	if [ ${WRITEDIFF} = y ]; then
		rm -f ${propfile}.TODO ${propfile}.TOREMOVE
		[ -s ${DIFF1} ] && cp ${DIFF1} ${propfile}.TODO
		[ -s ${DIFF2} ] && cp ${DIFF2} ${propfile}.TOREMOVE
	else
		sed 's/^/+/' ${DIFF1}
		sed 's/^/-/' ${DIFF2}
	fi
done


cleanup
exit 0

