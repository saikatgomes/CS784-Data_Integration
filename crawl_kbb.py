#!/usr/bin/python

import requests
from lxml import html
import json, time, datetime

base_url = "http://www.kbb.com"
used = "/used-cars/"
D =[]


# current time for logging
def getTime(f=1):
    ts = time.time()
    fmt=""
    if f==1:
        fmt='%Y-%m-%d--%H-%M-%S--%f'
    else:
        fmt='%Y%m%d%H%M%S%f'
    dt = datetime.datetime.fromtimestamp(ts).strftime(fmt)
    return dt

# current date for logging
def getDate():
    ts = time.time()
    dt = datetime.datetime.fromtimestamp(ts).strftime('%d%m%Y')
    return dt

def get_all_makes(url,tp):
    global D
    print(getTime(1)+" fetching makes for "+tp) 
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    car_makes = []
    car_makes_links = []
    for a in tree.xpath("//ul[@class='contentlist by-make']/li"):
        car_makes.extend(a.xpath('a/text()'))
        car_makes_links.extend(a.xpath('a/@href'))
    #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(car_makes,car_makes_links))
    D.append({"makes":car_makes,"type":tp,"url":car_makes_links})
    time.sleep(1)




if __name__ == "__main__":
    get_all_makes(base_url,"new")
    get_all_makes(base_url+used,"used");
    fileName = 'data/kbb_dict_'+getTime()+'.json'
    with open(fileName,"w") as f:
        json.dump({"car_makes_link": D},f,indent=2)



