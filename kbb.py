#!/usr/bin/python

import requests,json,time,datetime,os.path,socket,random
from lxml import html
from random import shuffle

base_url = "http://www.kbb.com"
used = "/used-cars/"
MAKES =[]

C_COUNT=0

# current time for logging
def getTime(f=1):
    ts = time.time()
    fmt=""
    if f==1:
        fmt='%Y-%m-%d--%H-%M-%S-%f'
    else:
        fmt='%Y%m%d%H%M%S%f'
    dt = datetime.datetime.fromtimestamp(ts).strftime(fmt)+"--["+str(C_COUNT)+"]"
    return dt

def crawl_one_page(url,main_str, text_str, url_str):
    global C_COUNT
    C_COUNT=C_COUNT+1
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    text_list = []
    url_list=[]
    redir_url=url
    if main_page.history:
        redir_url=main_page.url
    else:
        for a in tree.xpath(main_str):
            text_list.extend(a.xpath(text_str))
            url_list.extend(a.xpath(url_str))
        time.sleep(1)
    return text_list,url_list,redir_url

def get_all_makes(url,tp):
    global MAKES
    print(getTime(1)+"|> Getting a list of car makes: TYPE="+tp)
    makeFileName="data/makes.json" 
    if os.path.isfile(makeFileName):
        print(getTime(1)+"|> List of Car Makes found! [no additional crawing needed]") 
        MAKES=[]
        json_data=open(makeFileName)
        data=json.load(json_data)
        MAKES=data.get('car_makes_link')
        json_data.close()
    else:
        car_makes, car_makes_links, redir_url = crawl_one_page(url,"//ul[@class='contentlist by-make']/li",'a/text()','a/@href')
        MAKES.append({"makes":car_makes,"type":tp,"url":car_makes_links})
        with open(makeFileName,"w") as f:
            json.dump({'car_makes_link':MAKES},f,indent=2)
        time.sleep(1)

def schedule_crawl():
    global MAKES
    make_list=[]
    url_list=[]
    for x in range(0,len(MAKES)):
        if MAKES[x].get('type')=='used':
            make_list=MAKES[x].get('makes')
            url_list=MAKES[x].get('url')
    idx=range(0,len(make_list))
    shuffle(idx)
    for i in range(0,len(make_list)):
    #for i in range(19,20):
        rnd_idx=idx[i]
        make=make_list[rnd_idx]
        url=url_list[rnd_idx]
        print(getTime(1)+"|> Scheduling crawl for MAKE="+make)
        crawl_make(make,url) 
    #done

def get_info_from_url(url):
    #print url
    X=url.split('/')
    sub_cat=X[6]
    Y=X[8].split('&')
    Z=Y[0].split('=')
    car_id=Z[1]
    year = X[5]
    year =year[:4]
    return car_id, sub_cat, year

def crawl_make(make,url):
    print(getTime(1)+"|> Starting crawling for MAKE="+make)
    if os.path.isfile('data/'+make+'.json'):
        print(getTime(1)+"|> "+make+" has already been crawled! [no additional crawing needed]")
        return
    if os.path.isfile('data/'+make+'.lock'):
        print(getTime(1)+"|> "+make+" is currently been crawled by someone else!")
        return
    print(getTime(1)+"|\tMAKE="+make)
    with open('data/'+make+'.lock',"w") as lk:
        lk.write("Currently proccessed by "+socket.gethostname())
    DATA=[]
    try_last=[]
    car_cat, car_cat_url, redir_url = crawl_one_page(base_url+url,"//ul[@class='browse-category']/li/span","a/text()","a/@href") 
    for x in range(0,len(car_cat)):
        this_cat=car_cat[x]
        this_cat=this_cat[len(make)+1:]
        print(getTime(1)+"|\t\tCAT="+this_cat)
        model,model_url, redir_url = crawl_one_page(base_url+car_cat_url[x],"//div[@id='Make-category']/div[@class='collapse']/div/div/span[@class='left']","a/text()","a/@href")
        for y in range(0,len(model)):
            this_model=model[y]
            this_model=this_model[len(make)+1:]
            print(getTime(1)+"|\t\t\tMODEL="+this_model)
            year, year_url, redir_url = crawl_one_page(base_url+model_url[y],"//div[@class='model-year']/div/div[@class='left']","span[@class='model-year-name']/text()","a[@class='section-title']/@href")
            if len(year)==0:
                #print(getTime(1)+"|\t\t\tURL="+base_url+model_url[y])
                #print(getTime(1)+"|\t\t\tRE_URL="+redir_url)
                #car_id, xSub_cat, xYear = get_info_from_url(redir_url)
                #need year!
                #DATA.append({'make':make,'category':this_cat,'model':this_model,'year':xYear,'sub_cat':xSub_cat,'url':redir_url,'car_id':car_id})
                #print(getTime(1)+"|\t\t\t\t\t\tmake="+make+",cat="+this_cat+",mode="+this_model+",year="+xYear+",sub="+xSub_cat+",id="+car_id)
                try_last.append(redir_url)
            for z in range(0,len(year)):
                this_year=year[z]
                this_year=this_year[:4]
                print(getTime(1)+"|\t\t\t\tYEAR="+this_year)
                sub_cat, sub_cat_url, redir_url = crawl_one_page(base_url+year_url[z],"//div[@class=\"mod-content expanded-content\"]/div[starts-with(@class,'vehicle-styles-container')]/div[starts-with(@class,'vehicle-styles-head')]","div[@class=\"style-name section-title\"]/text()","a/@href")
                if len(sub_cat)==0:
                    print(getTime(1)+"|\t\t\t\tURL="+base_url+year_url[z])
                    print(getTime(1)+"|\t\t\t\tRE_URL="+redir_url)
                    car_id, xSub_cat, xYear = get_info_from_url(redir_url)
                    DATA.append({'make':make,'category':this_cat,'model':this_model,'year':this_year,'sub_cat':xSub_cat,'url':redir_url,'car_id':car_id})
                    print(getTime(1)+"|\t\t\t\t\t\tmake="+make+",cat="+this_cat+",mode="+this_model+",year="+this_year+",sub="+xSub_cat+",id="+car_id)
                for i in range(0,len(sub_cat)):
                    one_cat=sub_cat[i].strip()
                    one_url=sub_cat_url[i]
                    print(getTime(1)+"|\t\t\t\t\tSUB="+one_cat) #+"\t\t["+one_url+"]")
                    car_id, xSub_cat, xYear = get_info_from_url(base_url+one_url)
                    DATA.append({'make':make,'category':this_cat,'model':this_model,'year':this_year,'sub_cat':one_cat,'url':one_url,'car_id':car_id})
                    print(getTime(1)+"|\t\t\t\t\t\tmake="+make+",cat="+this_cat+",mode="+this_model+",year="+this_year+",sub="+one_cat+",id="+car_id)
    fileName="data/"+make+".json"
    with open(fileName,"w") as f:
        json.dump({'car':DATA},f,indent=2)
    os.remove('data/'+make+'.lock')

if __name__ == "__main__":
    #get_all_makes(base_url,"new")
    get_all_makes(base_url+used,"used");
    schedule_crawl()

    
