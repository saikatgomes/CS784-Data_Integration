mkdir logs
for X in 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20
do
    Y=adelie-$X
    Z=galapagos-$X
    ssh $USER@$Y 'bash -s' < kbb_crawler.sh `pwd` > logs/remote_log_$Y.txt &
    sleep 1
    ssh $USER@$Z 'bash -s' < kbb_crawler.sh `pwd` > logs/remote_log_$Z.txt &
    sleep 1
done

