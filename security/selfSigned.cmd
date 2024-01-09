keytool -genkey -alias server-alias -keyalg RSA -keypass changeit -storepass changeit -keystore ./keystore.jks
keytool -export -alias server-alias -storepass changeit -file ./server.cer -keystore ./keystore.jks
keytool -import -v -trustcacerts -alias server-alias -file ./server.cer -keystore ./cacerts.jks -keypass changeit -storepass changeit
