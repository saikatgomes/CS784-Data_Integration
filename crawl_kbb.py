#!/usr/bin/python

import requests
from lxml import html
import json, time, datetime, random
from collections import defaultdict
import itertools, os, sys

base_url = "http://www.kbb.com"
used = "/used-cars/"
D = defaultdict(dict)
kbb_dump_filename = "kbb_cars_data.json"

# TESTING PURPOSE
lim = 2
# make -> [new/used] -> category -> model -> year
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

def sanity_check():
    global D
    keys_essentials = ['used', 'new']
    for k, v in D.items():
        for key in keys_essentials:
            if key not in v:
                print "Key '%s' not for %s" % (key, k)
    return

parse_dict = {
    'make' : ["//ul[@class='contentlist by-make']/li"],
    'category': ["//ul[@class='browse-category']/li/span"],
    'model': ["//div[@id='Make-category']/div[@class='collapse']/div/div/span[@class='left']"],
    'year' : ["//div[@class='model-year']/div/div[@class='left']", 
              "span[@class='model-year-name']/text()", 
              "a[@class='section-title']/@href"
              ]
    }
level = ['make', 'category', 'model', 'year']
    
def parse_page(url, xpath_head, xpath_values = ['a/text()', 'a/@href']):
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    ret = []
    for a in tree.xpath(xpath_head):
        ret.extend(zip(*(a.xpath(v) for v in xpath_values)))
    return ret[:lim]


def is_url(url):
    return type(url) == str

def start_getting_data(fname, keys=[]):
    global D
    if os.path.exists(fname):
        D = json.load(open(fname))
    else: 
        #get_all_makes(base_url,"new")
        get_all_makes(base_url+used,"used");
    print json.dumps(D, indent=4, sort_keys=True)

    for k in keys:
        print "Trying for:", k 
        if k not in D: continue

        D[k] = get_all_data(D[k], l=1)
    json.dump(D, open(fname, 'wb'), 
              indent=True, sort_keys=True)


def get_all_data(d, l):
    if l>=len(level): return
    typ = level[l]
    if type(d) == str:
        return dict(parse_page(base_url+d, parse_dict[typ][0],
                               parse_dict[typ][1:]))
    for k,v in d.items():
        if type(v) == str:
            d[k] = dict(parse_page(base_url+v, parse_dict[typ][0],
                                   parse_dict[typ][1:]))
            get_all_data(d[k], l+1)
    return 

def get_all_makes(url,tp="used"):
    if tp!='used':
        print "Probably I dont care about 'new' cars!"
        return
    global D
    print(getTime(1)+"|> fetching makes for "+tp) 
    for m,l in parse_page(url, "//ul[@class='contentlist by-make']/li"):
        D[m] = l
    time.sleep(1)

def get_category():
    global D
    print(getTime(1)+"|> fetching car categories ...") 
    for make,values in D.items():
        u = values.get('used', '')
        if not u: continue
        newUrl=base_url + u
        print(getTime(1)+"|>> crawling for categories of "+ make + " ["+newUrl+"]")
        values['categories'] = dict(parse_page(newUrl, "//ul[@class='browse-category']/li/span"))
        if random.randint(0,10)>8:
            time.sleep(1)

def get_model():
    global D
    print(getTime(1)+"|> fetching car models ...")
    for make, v in D.items():
        print(getTime(1)+"|>> crawling for models of "+make)
        categories = v.get('categories', '')
        if not categories:
            print "'categories' not found in --> ", make, ':', v
            continue
        for cat, cat_url in categories.items():
            newUrl=base_url+ cat_url
            print(getTime(1)+"|>>> crawling for models of "+make+" category="+cat+" ["+cat_url+"]")
            new_page=requests.get(newUrl)
            tree=html.fromstring(new_page.text)
            for a in tree.xpath("//div[@id='Make-category']/div[@class='collapse']/div/div/span[@class='left']"):
                            v['categories'][cat] = dict(zip(a.xpath('a/text()'), a.xpath('a/@href')))
        if random.randint(0,10)>8:
            time.sleep(1)

def get_year():
    global D3
    print(getTime(1)+"|> fetching car years ...")
    for x in range(0,len(D3)):
        X=D3[x]
        c_name=X.get('company')
        cat=X.get('cat')
        model=X.get('model')
        url=X.get('url')
        print(getTime(1)+"|>> crawling for company="+c_name+" category="+cat)
        for y in range(0,len(model)):
            newUrl=base_url+url[y]
            print(getTime(1)+"|>>> crawling for company="+c_name+" category="+cat+" model="+model[y]+" ["+url[y]+"]")
            new_page=requests.get(newUrl)
            tree=html.fromstring(new_page.text)
            year = []
            year_url = []
            for a in tree.xpath("//div[@class='model-year']/div/div[@class='left']"):
                year.extend(a.xpath("span[@class='model-year-name']/text()"))
                year_url.extend(a.xpath("a[@class='section-title']/@href"))
            D4.append({'company':c_name,'cat':cat,'model':model[y],'year':year,'url':year_url})
            #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(year,year_url))
            print '\n              |>>>> '.join(year)
            time.sleep(1)

if __name__ == "__main__":
    start_getting_data(kbb_dump_filename, ['Acura'])
