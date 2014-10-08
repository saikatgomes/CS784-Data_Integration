#!/usr/bin/python

import requests
from lxml import html
import json, time

base_url = "http://www.kbb.com"
used = "/used-cars/"
D =[]
#base_search_url = base_url + "/search/cta"

'''
def get_all_links(url, limmit_hop = 100):
    first_page = requests.get(url)
    tree = html.fromstring(first_page.text)
    links = []
    links.extend(tree.xpath("//p[@class='row']/a/@href"))
    for i in xrange(1,limmit_hop):
        first_page = requests.get(url+"?s=%d&" % (i*100))
        tree = html.fromstring(first_page.text)
        links.extend(tree.xpath("//p[@class='row']/a/@href"))
        time.sleep(1)
    print links

def car_info(url):
    page = request.get(url)
    tree = html.fromstring(page)
    _id_ = re.match(".*(\d+)\.html")
    c = {"id": }
'''


def get_all_makes(url,tp):
    global D
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    car_makes = []
    car_makes_links = []
    for a in tree.xpath("//ul[@class='contentlist by-make']/li"):
        car_makes.extend(a.xpath('a/text()'))
        car_makes_links.extend(a.xpath('a/@href'))
    print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(car_makes,car_makes_links))
    D.append({"makes":car_makes,"type":tp,"url":car_makes_links})


if __name__ == "__main__":
    get_all_makes(base_url,"new")
    get_all_makes(base_url+used,"used");
    with open("foo.json","w") as f:
        json.dump({"car_makes_link": D},f,indent=2)



