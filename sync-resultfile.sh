#!/bin/bash

source $HOME/.bashrc

git add gdata/result.txt
git commit -m "AUTO COMMIT: result.txt"
git pull
git push origin master

