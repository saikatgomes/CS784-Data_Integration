#!/usr/bin/python

import requests
from lxml import html
import json, time

base_url = "http://madison.craigslist.org"
base_search_url = base_url + "/search/cta"

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



if __name__ == "__main__":
    get_all_links(base_url, 100)
