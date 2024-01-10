"$JAVA_HOME/bin/keytool" -genkey -alias server-alias -keyalg RSA -keypass changeit -storepass changeit -keystore ./keystore.jks
echo press ENTER to execute next step
read DONE
"$JAVA_HOME/bin/keytool" -export -alias server-alias -storepass changeit -file ./server.cer -keystore ./keystore.jks
echo press ENTER to execute next step
read DONE
"$JAVA_HOME/bin/keytool" -import -v -trustcacerts -alias server-alias -file ./server.cer -keystore ./cacerts.jks -keypass changeit -storepass changeit
echo DONE
