#!/bin/bash
if [ -z "$1" ]
then
  echo "Usage:"
  echo ""
  echo "self_signed.sh key_password [java_home] [hostname] [store_password] [certificates_directory_path] [cacerts_password]"
  echo ""
  echo "  - java_home is defaulted to $JAVA_HOME"
  echo "  - hostname is defaulted to $HOSTNAME"
  echo "  - store_password is defaulted to key_password"
  echo "  - certificates_directory_path is defaulted to current diretory"
  echo "  - cacerts_password is defaulted to changeit"
  echo ""
  echo "Sample:"
  echo "./self_signed.sh \"secr3!\" \"C:/Java/jdk1.8.0_281\" \"bravo-ch4mp\""
  echo ""
  exit 1
else

  echo "#------------------------------------------"
  echo "# This is a no-op script"
  echo "# Copy / paste output to:"
  echo "#   - generate certificate files"
  echo "#   - import certificates into cacerts file"
  echo "#------------------------------------------"
  
  KEY_PASSWORD="${1}"
  echo "# key password: $KEY_PASSWORD"
  
  if [ -z "$2" ]
  then
	if [ -z "$JAVA_HOME" ]
    then
      echo "ERROR: could not locate java home"
	  exit 1
    else
      JAVA=$JAVA_HOME
    fi
  else
    JAVA=$2
  fi
  JAVA=$(echo $JAVA | sed 's/\\/\//g')
  echo "# java home: $JAVA"
  
  if [ -f "${JAVA}/lib/security/cacerts" ]
  then
    # recent JDKs and JREs style
    CACERTS="${JAVA}/lib/security/cacerts"
  elif [ -f "${JAVA}/jre/lib/security/cacerts" ]
  then
    # legacy JDKs style (1.8 and older)
    CACERTS="${JAVA}/jre/lib/security/cacerts"
  else
    echo "ERROR: could not locate cacerts under ${JAVA}"
    exit 1
  fi
  echo "# cacerts path: $CACERTS"
  
  if [ -z "${3}" ]
  then
    HOST="$HOSTNAME"
  else
    HOST="${3}"
  fi
  echo "# host (certificate CN): $HOST"
  
  if [ -z "${4}" ]
  then
    STORE_PASSWORD="$KEY_PASSWORD"
  else
    STORE_PASSWORD="${4}"
  fi
  echo "# store password : $STORE_PASSWORD"
  
  if [ -z "${5}" ]
  then
    CERTIF_DIR="."
  else
    CERTIF_DIR="${5}"
  fi
  echo "# certificates directory path: $CERTIF_DIR"
  CERTIF_DIR=$(echo $CERTIF_DIR | sed 's/\\/\//g')
  
  if [ -z "${6}" ]
  then
    CACERTS_PASSWORD="changeit"
  else
    CACERTS_PASSWORD="${6}"
  fi
  echo "# cacerts password: $CACERTS_PASSWORD" 
  echo "#------------------------------------------"
fi

echo ""

rm -f ${HOST}_self_signed.config;
sed 's/\[hostname\]/'${HOST}'/g' "${CERTIF_DIR}/self_signed_template.config" > "${CERTIF_DIR}/${HOST}_self_signed.config"

echo openssl req -config \"${CERTIF_DIR}/${HOST}_self_signed.config\" -new -keyout \"${CERTIF_DIR}/${HOST}_self_signed_key.pem\" -out \"${CERTIF_DIR}/${HOST}_self_signed_cert.pem\" -reqexts v3_req
echo ""

echo openssl x509 -req -days 365 -extfile \"${CERTIF_DIR}/${HOST}_self_signed.config\" -in \"${CERTIF_DIR}/${HOST}_self_signed_cert.pem\" -extensions v3_req -signkey \"${CERTIF_DIR}/${HOST}_self_signed_key.pem\" -out \"${CERTIF_DIR}/${HOST}_self_signed.crt\"
echo ""

echo openssl pkcs12 -export -in \"${CERTIF_DIR}/${HOST}_self_signed.crt\" -inkey \"${CERTIF_DIR}/${HOST}_self_signed_key.pem\" -name ${HOST}_self_signed -password pass:${KEY_PASSWORD} -out \"${CERTIF_DIR}/${HOST}_self_signed.pfx\"
echo ""

echo \"${JAVA}/bin/keytool\" -importkeystore -srckeystore \"${CERTIF_DIR}/${HOST}_self_signed.pfx\" -srcstorepass \"${STORE_PASSWORD}\" -srcstoretype pkcs12 -srcalias ${HOST}_self_signed -destkeystore \"${CERTIF_DIR}/${HOST}_self_signed.jks\" -deststoretype PKCS12 -deststorepass ${STORE_PASSWORD} -destalias ${HOST}_self_signed
echo ""

echo \"${JAVA}/bin/keytool\" -importkeystore -srckeystore \"${CERTIF_DIR}/${HOST}_self_signed.pfx\" -srcstorepass \"${STORE_PASSWORD}\" -srcstoretype pkcs12 -srcalias ${HOST}_self_signed -destkeystore \"${CACERTS}\" -deststorepass ${CACERTS_PASSWORD} -destalias ${HOST}_self_signed
echo ""
