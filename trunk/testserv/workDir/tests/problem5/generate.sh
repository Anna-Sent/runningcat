if [ $1 -a $2 ]
then
	begin=$1
	end=$2
else
	exit
fi
#i=$begin
#while [ $i -le $end ]
for ((i=$begin;i<=$end;i++))
do
	`touch in$i`;
	`touch out$i`;
	#i=`expr $i + 1`
done
