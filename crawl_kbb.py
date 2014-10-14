#!/usr/bin/python

import requests,json,time,datetime,os.path,socket,random
from lxml import html
from random import shuffle

base_url = "http://www.kbb.com"
used = "/used-cars/"
MAKES =[]

MAKE_MAIN_STR="//ul[@class='contentlist by-make']/li"
MAKE_TEXT_STR='a/text()'
MAKE_URL_STR='a/@href'

CAT_MAIN_STR="//ul[@class='browse-category']/li/span"
CAT_TEXT_STR="a/text()"
CAT_URL_STR="a/@href"

MODEL_MAIN_STR="//div[@id='Make-category']/div[@class='collapse']/div/div/span[@class='left']"
MODEL_TEXT_STR="a/text()"
MODEL_URL_STR="a/@href"

YEAR_MAIN_STR="//div[@class='model-year']/div/div[@class='left']"
YEAR_TEXT_STR="span[@class='model-year-name']/text()"
YEAR_URL_STR="a[@class='section-title']/@href"

SUB_MAIN_STR="//div[@class=\"mod-content expanded-content\"]/div[starts-with(@class,'vehicle-styles-container')]/div[starts-with(@class,'vehicle-styles-head')]"
SUB_TEXT_STR="div[@class=\"style-name section-title\"]/text()"
SUB_URL_STR="a/@href"

C_COUNT=0

def create_vodro_dir(_dir_, next):
    _dir_ = _dir_ + "/" + next.replace('/', '_')
    if not os.path.exists(_dir_):
        os.mkdir(_dir_)
    return _dir_

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

def crawl_one_page(url,main_str, text_str, url_str, save_path):
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
        with open(save_path,'w') as f:
            f.write(main_page.text.encode("ascii",errors="ignore"))
    return text_list,url_list,redir_url

def get_attributes(url,save_path):
    global C_COUNT
    C_COUNT=C_COUNT+1
    main_page=requests.get(url)
    tree = html.fromstring(main_page.text)
    redir_url=url
    att=[]
    if main_page.history:
        redir_url=main_page.url
        print "!!!!"
    else:
        for a in tree.xpath("//div[@class=\"options-container mod-single\"]/div[@class=\"expand options-group-container\"]"):
            sect_name=a.xpath("div[@class=\"accordion mod-head expandable\"]/span[@class='mod-title']/strong/text()")
            if (sect_name[0]=="Powertrain" or sect_name[0]=="Options"):
                b=a.xpath("div[@class=\"mod-content expanded-content options-list\"]/div")
                for c in b:
                    d=c.xpath("span[@class='options-title']/text()")
                    if len(d)>0:
                        #this is sub section heading
                        sub_sect=d[0]
                    else:
                        x=c.xpath("span[@class='type']/label/strong/text()")
                        attr=x[0]
                        id1=c.xpath("div/@id")
                        id2=c.xpath("span[@class='type']/label/@for")
                        id1=id1[0]
                        id2=id2[0]
                        id3=id1[6:]
                        att.append([sect_name[0],sub_sect,attr,id1,id2,id3])
        with open(save_path,'w') as f:
            f.write(main_page.text.encode("ascii",errors="ignore"))             
        time.sleep(1)
    return att, redir_url


def get_all_makes(url,tp):
    global MAKES
    global MAKE_MAIN_STR,MAKE_TEXT_STR,MAKE_URL_STR
    global CAT_MAIN_STR,CAT_TEXT_STR,CAT_URL_STR
    global MODEL_MAIN_STR,MODEL_TEXT_STR,MODEL_URL_STR
    global YEAR_MAIN_STR,YEAR_TEXT_STR,YEAR_URL_STR
    print(getTime(1)+"|> Getting a list of car makes: TYPE="+tp)
    makeFileName="kbb_data/makes.json" 
    if os.path.isfile(makeFileName):
        print(getTime(1)+"|> List of Car Makes found! [no additional crawing needed]") 
        MAKES=[]
        json_data=open(makeFileName)
        data=json.load(json_data)
        MAKES=data.get('car_makes_link')
        json_data.close()
    else:
        car_makes, car_makes_links, redir_url = crawl_one_page(url, MAKE_MAIN_STR, MAKE_TEXT_STR, MAKE_URL_STR,"kbb_data/html/page.html")
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

import re
def get_info_from_url(url):
    #print url
    vid = re.compile(r'.*kbb.com/[^/]+/[^/]+/(?P<year>\d{4})-[^/]*/(?P<sub_cat>[\w-]+)/.*/?vehicleid=(?P<vid>\d+).*')
    m = vid.match(url)
    if m:
        car_id = m.groupdict().get('vid', '')
        sub_cat= m.groupdict().get('sub_cat', '')
        year   = m.groupdict().get('year', '')
    else:
        print ">>>>>>>>>>>>>>>>>>>>>>", url
        car_id, sub_cat, year = '', '', ''
    return car_id, sub_cat, year

def crawl_make(make,url):
    print(getTime(1)+"|> Starting crawling for MAKE="+make)
    if os.path.isfile('kbb_data/'+make+'.json'):
        print(getTime(1)+"|> "+make+" has already been crawled! [no additional crawing needed]")
        return
    lockfile='kbb_data/'+make+'.lock'
    if os.path.isfile(lockfile):
        with open(lockfile,'r') as lk:
            msg=lk.read()
        print(getTime(1)+"|> "+make+" is "+msg)
        return
    print(getTime(1)+"|\tMAKE="+make)
    make_dir = create_vodro_dir('kbb_data/html', make)
    with open(lockfile,"w") as lk:
        lk.write("Currently proccessed by "+socket.gethostname())
    DATA=[]
    try_last=[]
    car_cat, car_cat_url, redir_url = crawl_one_page(base_url+url, CAT_MAIN_STR, CAT_TEXT_STR, CAT_URL_STR,make_dir+"/page.html") 
    for x in range(0,len(car_cat)):
        this_cat=car_cat[x]
        this_cat=this_cat[len(make)+1:]
        print(getTime(1)+"|\t\tCAT="+this_cat)
        cat_dir=create_vodro_dir(make_dir, this_cat)
        model,model_url, redir_url = crawl_one_page(base_url+car_cat_url[x], MODEL_MAIN_STR, MODEL_TEXT_STR, MODEL_URL_STR,cat_dir+"/page.html")
        for y in range(0,len(model)):
            this_model=model[y]
            this_model=this_model[len(make)+1:]
            print(getTime(1)+"|\t\t\tMODEL="+this_model)
            model_dir=create_vodro_dir(cat_dir, this_model)
            year, year_url, redir_url = crawl_one_page(base_url+model_url[y], YEAR_MAIN_STR, YEAR_TEXT_STR, YEAR_URL_STR,model_dir+"/page.html")
            if len(year)==0:
                try_last.append([redir_url,this_cat,this_model])
            for z in range(0,len(year)):
                this_year=year[z]
                this_year=this_year[:4]
                year_dir=create_vodro_dir(model_dir, this_year)
                print(getTime(1)+"|\t\t\t\tYEAR="+this_year)
                DATA=get_one_car(base_url+year_url[z],make,this_cat,this_model,this_year,DATA,year_dir)
    if len(try_last)>0:
        for s in range(0,len(try_last)):
            X=try_last[s]
            url=X[0]
            this_cat=X[1]
            this_model=X[2]
            try:
                car_id, xSub_cat, xYear = get_info_from_url(redir_url)
                if not (car_id and xSub_cat and xYear):
                    raise KeyError
                last_dir=create_vodro_dir("kbb_data/html/%s/%s/%s" % (make.replace('/', '_'), 
                                                                      this_cat.replace('/', '_'),
                                                                      this_model.replace('/', '_')), this_year)
                DATA = get_one_car(url,make,this_cat,this_model,'',DATA, last_dir)
            except KeyError:
                print(getTime(1)+"|\t\t\t\t()URL  ="+url)
                print(getTime(1)+"|\t\t\t\t()ERROR="+redir_url)
    fileName="kbb_data/"+make+".json"
    with open(fileName,"w") as f:
        json.dump({'car':DATA},f,indent=2)
    os.remove(lockfile)

def get_one_car(url,make,this_cat,this_model,this_year,DATA,_dir_):
    global SUB_MAIN_STR,SUB_TEXT_STR,SUB_URL_STR
    sub_cat, sub_cat_url, redir_url = crawl_one_page(url, SUB_MAIN_STR, SUB_TEXT_STR, SUB_URL_STR,_dir_+"/page.html")
    if len(sub_cat)==0:
        try:
            car_id, xSub_cat, xYear = get_info_from_url(redir_url)
            if not (car_id and xSub_cat and xYear):
                raise KeyError
            if this_year=='':
                this_year=xYear
            sub_cat_dir=create_vodro_dir(_dir_, xSub_cat)
            a,r = get_attributes(redir_url, sub_cat_dir + "/%s.html"%car_id)
            DATA.append({'make':make,'category':this_cat,'model':this_model,'year':this_year,'sub_cat':xSub_cat,'url':redir_url,'car_id':car_id,'att':a})
            print(getTime(1)+"|\t\t\t\t\t\tmake="+make+",cat="+this_cat+",mode="+this_model+",year="+this_year+",sub="+xSub_cat+",id="+car_id)
        except KeyError:
            print(getTime(1)+"|\t\t\t\tURL  ="+url)
            print(getTime(1)+"|\t\t\t\tERROR="+redir_url)
    for i in range(0,len(sub_cat)):
        one_cat=sub_cat[i].strip()
        one_url=sub_cat_url[i]
        sub_cat_dir = create_vodro_dir(_dir_, one_cat)
        print(getTime(1)+"|\t\t\t\t\tSUB="+one_cat)
        car_id, xSub_cat, xYear = get_info_from_url(base_url+one_url)
        a,r = get_attributes(base_url+one_url,sub_cat_dir+"/%s.html"%car_id)
        DATA.append({'make':make,'category':this_cat,'model':this_model,'year':xYear,'sub_cat':one_cat,'url':one_url,'car_id':car_id,'att':a})
        print(getTime(1)+"|\t\t\t\t\t\tmake="+make+",cat="+this_cat+",mode="+this_model+",year="+xYear+",sub="+one_cat+",id="+car_id)
    return DATA
 


if __name__ == "__main__":
    get_all_makes(base_url+used,"used");
    schedule_crawl()

    
