#!/bin/bash

source $HOME/.bashrc
if [ -e ../dataintegration/ ];
then
    echo "I am here";
else
    echo "Sorry take to the directory where the script is"
fi

#cd $HOME/Acads/DataModels_784/dataintegration/;

if [ "$1" = "push" ]
then
    echo "Going to push"
    while [ 1 ];
    do 
	git add gdata/result.txt
	git commit -m "AUTO COMMIT: result.txt"
	git pull
	git push origin master
	sleep 5;
    done
elif [ "$1" = "pull" ]
then
    echo "I will pull only"
    while [ 1 ];
    do 
	git pull
    done
else
    echo "Sorry command not recognised"
fi


