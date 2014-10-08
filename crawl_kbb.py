#!/usr/bin/python

import requests
from lxml import html
import json, time, datetime

base_url = "http://www.kbb.com"
used = "/used-cars/"
D =[]
D2 =[]
D3 =[]
D4 =[]

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
    print(getTime(1)+"|> fetching makes for "+tp) 
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    car_makes = []
    car_makes_links = []
    for a in tree.xpath("//ul[@class='contentlist by-make']/li"):
        car_makes.extend(a.xpath('a/text()'))
        car_makes_links.extend(a.xpath('a/@href'))
    #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(car_makes,car_makes_links))
    D.append({"makes":car_makes,"type":tp,"url":car_makes_links})
    print '\n              |>> '.join(car_makes)
    time.sleep(1)

def get_category():
    global D
    print(getTime(1)+"|> fetching car categories ...") 
    #THIS IS SHIT
    for x in range(0,len(D)):
        if D[x].get('type')=='used':
            X=D[x]
    #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(X.get('makes'),X.get('url'))) 
    makes=X.get('makes')
    url=X.get('url')
    for x in range(0,len(url)):
    #for x in range(0,1):
        newUrl=base_url+url[x]
        print(getTime(1)+"|>> crawling for categories of "+makes[x]+" ["+newUrl+"]")
        new_page=requests.get(newUrl)
        tree = html.fromstring(new_page.text)
        car_cat=[]
        car_cat_url=[]
        for a in tree.xpath("//ul[@class='browse-category']/li/span"):
            car_cat.extend(a.xpath('a/text()'))
            car_cat_url.extend(a.xpath('a/@href'))
        D2.append({'company':makes[x],'cat':car_cat,'url':car_cat_url})
        #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(car_cat,car_cat_url))
        print '\n              |>>> '.join(car_cat)
        time.sleep(1)

def get_model():
    global D2
    print(getTime(1)+"|> fetching car models ...")
    for x in range(0,len(D2)):
    #for x in range(0,1):
        X=D2[x]
        c_name=X.get('company')
        cat=X.get('cat')
        url=X.get('url')
        print(getTime(1)+"|>> crawling for models of "+c_name)
        for y in range(0,len(cat)):
            newUrl=base_url+url[y]
            print(getTime(1)+"|>>> crawling for models of "+c_name+" category="+cat[y]+" ["+url[y]+"]")
            new_page=requests.get(newUrl)
            tree=html.fromstring(new_page.text)
            model =[]
            model_url=[]
            for a in tree.xpath("//div[@id='Make-category']/div[@class='collapse']/div/div/span[@class='left']"):
                model.extend(a.xpath('a/text()'))
                model_url.extend(a.xpath('a/@href'))
            D3.append({'company':c_name,'cat':cat[y],'model':model,'url':model_url})
            #print '\n'.join("%s -->  %s" %(a,b) for a,b in zip(model,model_url)) 
            print '\n              |>>>> '.join(model)
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
    get_all_makes(base_url,"new")
    get_all_makes(base_url+used,"used");
    ts=getTime()
    fileName = 'data/kbb_dict_'+ts+'.json'
    with open(fileName,"w") as f:
        json.dump({"car_makes_link": D},f,indent=2)
    get_category()
    fileName = 'data/kbb_dict2_'+ts+'.json'
    with open(fileName,"w") as f2:
        json.dump({"car_cat_link": D2},f2,indent=2)
    get_model()
    fileName = 'data/kbb_dict3_'+ts+'.json'
    with open(fileName,"w") as f3:
        json.dump({"car_cat_model_link": D3},f3,indent=2)
    get_year()
    fileName = 'data/kbb_dict4_'+ts+'.json'
    with open(fileName,"w") as f4:
        json.dump({"car_cat_model_year_link": D4},f4,indent=2)

