i=1;

while read line; 
do ssh yoda cd 2IMA/SC/Projet/Shitdoop/hidoop/src/;
java ordo.DaemonImpl i "&";
i=i+1;
done < ../config/daemons.txt
