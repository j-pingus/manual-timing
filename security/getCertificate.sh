echo please read documentation before running this script
echo i.e.: https://certbot.eff.org/instructions?ws=other&os=ubuntufocal
sleep 5
echo /// or hit ENTER if you know what you are doing...
read KEY
certbot certonly --standalone
