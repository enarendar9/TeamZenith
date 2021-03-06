#!/bin/bash

# some variables
# refactoring the script such that all these values are
# passed from the outside as arguments should be easy
pbsFileName=PBS_Script_1454381486563.pbs
email=`cat $pbsFileName |grep "#PBS -M"|cut -d " " -f3`
jobName=`cat $pbsFileName |grep "#PBS -N"|cut -d " " -f3`
cd ..
outputFiles=`ls $jobName*`
from="atumohan@h4.karst.uits.iu.edu"
to=$email
subject="Karst execution result for job"$jobName
boundary="ZZ_/afg6432dfgkl.94531q"
body="This is the body of our email"
declare -a attachments
attachments=( $outputFiles )

# Build headers
{

printf '%s\n' "From: $from
To: $to
Subject: $subject
Mime-Version: 1.0
Content-Type: multipart/mixed; boundary=\"$boundary\"

--${boundary}
Content-Type: text/plain; charset=\"US-ASCII\"
Content-Transfer-Encoding: 7bit
Content-Disposition: inline

$body
"

# now loop over the attachments, guess the type
# and produce the corresponding part, encoded base64
for file in "${attachments[@]}"; do

  [ ! -f "$file" ] && echo "Warning: attachment $file not found, skipping" >&2 && continue

  mimetype=$(get_mimetype "$file")

  printf '%s\n' "--${boundary}
Content-Type: $mimetype
Content-Transfer-Encoding: base64
Content-Disposition: attachment; filename=\"$file\"
"

  base64 "$file"
  echo
done

# print last boundary with closing --
printf '%s\n' "--${boundary}--"

} | sendmail -t -oi   # one may also use -f here to set the envelope-from

