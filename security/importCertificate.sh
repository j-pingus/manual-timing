echo if you see this message, press CTRL + C now and review the script
read KEY
# all values starting with '<' and ending with '>' should be replaced (without '<' and '>')
# you should know what to put in the next two lines if you ran getCertificate.sh or renewCertificate.sh
FC=/etc/letsencrypt/live/<your domain name>/fullchain.pem
PK=/etc/letsencrypt/live/<your domain name>/privkey.pem
PW=<password was here>
KS=letsencrypt.jks
ALIAS=simple-cert
rm -rf $KS
openssl pkcs12 -export -in $FC -inkey $PK -out $ALIAS.p12 -password pass:$PW
keytool -importkeystore -srckeystore $ALIAS.p12 -srcstoretype pkcs12 -srcstorepass $PW -destkeystore $KS -deststoretype jks -deststorepass $PW
