i=1;
chemin=`pwd`;
while read line; 
do
	xterm -e ssh $line "cd $chemin; echo $line $i; java ordo.DaemonImpl $i" &
	echo "Daemon $i lancé";
	let i=$i+1;
done < ../config/daemons.txt
