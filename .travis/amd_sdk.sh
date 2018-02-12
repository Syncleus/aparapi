#!/bin/bash

# Original script from https://github.com/gregvw/amd_sdk/

# Location from which get nonce and file name from
URL="https://developer.amd.com/amd-accelerated-parallel-processing-app-sdk/"
URLDOWN="https://developer.amd.com/amd-license-agreement-appsdk/"

NONCE1_STRING='name="amd_developer_central_downloads_page_nonce"'
FILE_STRING='name="f"'
POSTID_STRING='name="post_id"'
NONCE2_STRING='name="amd_developer_central_nonce"'

#AMD APP SDK v3.0:
if [[ $1 == "300" ]]; then
  echo "AMD APP SDK v3.0"
  FORM=`wget -qO - $URL | sed -n '/download-2/,/64-bit/p'`
else
#AMD APP SDK v2.9.1:
  echo "AMD APP SDK v2.9.1"
  FORM=`wget -qO - $URL | sed -n '/download-5/,/64-bit/p'`
fi

# Get nonce from form
NONCE1=`echo $FORM | awk -F ${NONCE1_STRING} '{print $2}'`
NONCE1=`echo $NONCE1 | awk -F'"' '{print $2}'`
echo $NONCE1

# get the postid
POSTID=`echo $FORM | awk -F ${POSTID_STRING} '{print $2}'`
POSTID=`echo $POSTID | awk -F'"' '{print $2}'`
echo $POSTID

# get file name
FILE=`echo $FORM | awk -F ${FILE_STRING} '{print $2}'`
FILE=`echo $FILE | awk -F'"' '{print $2}'`
echo $FILE

FORM=`wget -qO - $URLDOWN --post-data "amd_developer_central_downloads_page_nonce=${NONCE1}&f=${FILE}&post_id=${POSTID}"`

NONCE2=`echo $FORM | awk -F ${NONCE2_STRING} '{print $2}'`
NONCE2=`echo $NONCE2 | awk -F'"' '{print $2}'`
echo $NONCE2

wget --content-disposition --trust-server-names $URLDOWN --post-data "amd_developer_central_nonce=${NONCE2}&f=${FILE}" -O AMD-SDK.tar.bz2;
