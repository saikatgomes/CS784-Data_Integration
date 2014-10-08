#!/usr/bin/python

import requests
from lxml import html
import json, time, re
import shutil

base_url = "http://madison.craigslist.org"
base_search_url = base_url + "/search/cta"
IMGDIR = 'car_images/'
cars = []
def get_all_links(url, limmit_hop = 100, fromFile=None):
    if fromFile:
        with open(fromFile) as f:
            links = [x.strip() for x in f.readlines()]
            return links
    first_page = requests.get(url)
    tree = html.fromstring(first_page.text)
    links = []
    links.extend(tree.xpath("//p[@class='row']/a/@href"))
    last_len = 0
    for i in xrange(1,limmit_hop+1):
        full_url = url+"?s=%d&" % (i*100)
        first_page = requests.get(full_url)
        tree = html.fromstring(first_page.text)
        links.extend(tree.xpath("//p[@class='row']/a/@href"))
        print "Hop count:", i, full_url
        if last_len == links:
            break # probably done with crawling the links
        last_len = len(links)
        time.sleep(1)
    print links
    with open('list_links.txt', 'w') as f:
        f.write('\n'.join(links))
    return links

def get_car_info(links):
    for i,l in enumerate(links):
        url = "%s%s" % ( base_url, l)
        print "Downloading:", url
        if i%100==0:
            print ">>>>>>   Done processing %d cars!!!" 
        car_info(url)
        if i%5==0:
            time.sleep(1)
    with open('car_data.json', 'wb') as fp:
        json.dump(cars, fp, indent=2)

def car_info(url):
    global cars
    page = requests.get(url)
    tree = html.fromstring(page.text)
    _id_ = re.match(r".*/(\d+)\.html", url).group(1)
    try:
        title = tree.xpath("//h2[@class='postingtitle']/text()")[1].strip()
    except IndexError:
        return None
    c = {"id": _id_, "title": title}
    title_parse = re.compile(r"(\d{4})(.*)\$(\d+) \((.*)\)")
    try:
        c["year"], c["make"], c["cost"], c["location"] = title_parse.match(title).groups()
    except AttributeError:
        c["year"], c["make"], c["cost"], c["location"] = "", "", "", ""

    attr = {}
    for attrib in tree.xpath("//p[@class='attrgroup']/span/text()"):
        k = attrib.split(':')
        if len(k)<2:
            k,v = 'title', k[0]
        else:
            k, v = k[0], ','.join(k[1:])
        v = v.strip()
        k = k.strip()
        attr[k] = v
    c['attr'] = attr
    c['body'] = ''.join(tree.xpath('//section[@id="postingbody"]/text()'))
    # get images
    c['img'] = []
    for img in tree.xpath("//div[@id='thumbs']/a"):
        imgid = img.xpath('@data-imgid')[0]
        fname = imgid + ".jpg"
        # l = img.xpath('@href')[0]
        # with open(IMGDIR+fname, 'wb') as f:
        #     shutil.copyfileobj(requests.get(l, stream=True).raw, f)
        c['img'].append(fname)

    for k in c.keys():
        if type(c[k]) == str:
            c[k] = c[k].strip()
    cars.append(c)

if __name__ == "__main__":
    links = get_all_links(base_search_url, 100, fromFile='list_links.txt')
    print links
    get_car_info(links)
    
