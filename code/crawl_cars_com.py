#!/usr/bin/env python

import requests
import lxml.html
import pprint
import re
import urlparse
from collections import defaultdict

DOMAIN = 'http://www.cars.com'
DETAILS_URL = DOMAIN + '/vehicledetail/detail/{0}/overview/'
PHOTOS_URL = DOMAIN + '/vehicledetail/detail/{0}/photo/'
MAPS_URL = DOMAIN + '/vehicledetail/detail/{0}/map/'
SEP = ' |#| '

SAMPLE_CAR1 = DETAILS_URL.format('618072722')
SAMPLE_CAR2 = DETAILS_URL.format('608012227')
SAMPLE_CAR3 = DETAILS_URL.format('615714268')
SAMPLE_CAR4 = DETAILS_URL.format('608055462')

def get_id(url):
    path = urlparse.urlparse(url).path.split('/')
    assert len(path) > 3
    return path[3]

def item_one(items):
    """
    >>> item_one(['a', 'b', 'c'])
    'a'
    >>> item_one(['a'])
    'a'
    >>> item_one([])
    ''
    """
    return items[0] if len(items) > 0 else ''

def item_join(items, sep=' '):
    """
    >>> item_join(['a', 'b', 'c'])
    'a b c'
    >>> item_join(['a', 'b', 'c '], sep=',')
    'a,b,c'
    """
    return sep.join(items).strip()

def item_re(string, pat=''):
    """
    >>> item_re('2014 X Y Z', pat='(\d{4}) .*')
    '2014'
    >>> item_re('014 X Y Z', pat='(\d{4}) .*')
    ''
    >>> item_re('12124 mi.', pat='(\d*) mi\.')
    '12124'
    >>> item_re('12124 mi.', pat='\d* mi\.')
    ''
    """
    match = re.match(pat, string)
    if match:
        groups = match.groups()
        return groups[0] if len(groups) > 0 else ''
    else:
        return ''

def get_car_details(tree):
    details = {}
    if len(tree) == 0:
        return details
    tree = tree[0]
    names = tree.xpath('li/strong/text()')
    values = tree.xpath('li[@class="alt"]/text()')
    for name, value in zip(names, values):
        details[name.strip()] = value.strip()
    return details

DETAILS = { 'Body Style': 'body_style',
            'Doors': 'doors',
            'Drivetrain': 'drivetrain',
            'Engine': 'engine',
            'Exterior Color': 'color',
            'Fuel': 'fuel',
            'Interior Color': 'interior_color',
            'Stock #': 'stock_number',
            'Transmission': 'transmission',
            'VIN': 'vin' }

def process_details(car, details):
    extra = {}
    known = {}
    for key, value in details.iteritems():
        if DETAILS.has_key(key):
            car[DETAILS[key]] = value
        else:
            extra[key] = value
    for key, value in known.iteritems():
        car[key] = value
    if len(extra) > 0:
        car['extra_details'] = extra

def process_title(car, title):
    title = ' '.join(title).strip()
    match = re.match('(\d{4}) (.*)', title)
    if match:
        car['name'] = match.group(2)
        car['year'] = match.group(1)
    else:
        car['name'] = title

def set_if_valid(car, attr, value):
    if value:
        car[attr] = value

def get_details(id):
    page = requests.get(DETAILS_URL.format(id))
    html = lxml.html.fromstring(page.text)
    car = {}
    process_title(car, map(str.strip, html.xpath(
        '//div[@class="row dealer-info"]//h1[@class="title"]/text()')))
    car['price'] = item_re(item_join(map(str.strip, html.xpath(
        '//div[@class="row dealer-info"]//h1[@class="price"]/text()')))
        .replace(',', ''), pat='\$?(\d*)')
    car['miles'] = item_re(item_join(map(str.strip, html.xpath(
        '//div[@class="row dealer-info"]//p[@class="miles"]/text()')))
        .replace(',', ''), pat='(\d*) mi\.')
    process_details(car, get_car_details(html.xpath(
        '//ul[@class="vehicle-details list"]')))
    car['vehicle_history'] = item_one(map(lambda u: DOMAIN + u.strip() \
            if len(u.strip()) > 0 else '', html.xpath(
                '//p[@class="vehicle-history"]/following-sibling::a/@href')))
    equipment = item_join(map(str.strip, html.xpath(
        '//div[@id="page"]//ul[@class="st-equipment list"]//li/text()')),
        sep=SEP)
    if equipment:
        car['equipment'] = equipment
    features = item_join(map(str.strip, html.xpath(
        '//div[@id="page"]//ul[@class="features list"]//li/text()')),
        sep=SEP)
    if features:
        car['features'] = features
    set_if_valid(car, 'seller_notes', item_join(
        html.xpath('//div[@id="page"]//span[@class="seller-notes"]/text()')))
    return car

def get_maps(id):
    page = requests.get(MAPS_URL.format(id))
    html = lxml.html.fromstring(page.text)
    car = {}
    base = '//div[@id="map-and-direction"]//div[@class="data-points"]/'
    set_if_valid(car, 'latitude', item_one(html.xpath(base + '@data-dlat')))
    set_if_valid(car, 'longitude', item_one(html.xpath(base + '@data-dlng')))
    set_if_valid(car, 'address', item_one(html.xpath(base + '@data-daddress')))
    set_if_valid(car, 'zip', item_one(html.xpath(base + '@data-dzip')))
    set_if_valid(car, 'city', item_one(html.xpath(base + '@data-dcity')))
    set_if_valid(car, 'state', item_one(html.xpath(base + '@data-dstate')))
    set_if_valid(car, 'phone', item_one(html.xpath(base + '@data-dstate')))
    return car

def get_photos(id):
    page = requests.get(PHOTOS_URL.format(id))
    html = lxml.html.fromstring(page.text)
    photos = html.xpath(
            '//div[@id="photo-video"]//img[@class="photo"]/@data-def-src')
    cars = {}
    if len(photos) > 0:
        cars['photos'] = ' '.join(photos)
    return cars

def get_car(url):
    id = get_id(url)
    car = {'id': id}
    car.update(**get_details(id))
    car.update(**get_photos(id))
    car.update(**get_maps(id))
    pprint.pprint(car)

def test_cars():
    get_car(SAMPLE_CAR1)
    get_car(SAMPLE_CAR2)
    get_car(SAMPLE_CAR3)
    get_car(SAMPLE_CAR4)

SEARCH_URL = (DOMAIN + '/for-sale/searchresults.action?dlId=&dgId=&AmbMkNm=&'
        'AmbMdNm=&AmbMkId=&AmbMdId=&searchSource=ADVANCED_SEARCH&rd={miles}&'
        'zc={zipcode}&uncpo=2&cpo=&stkTyp=U&VType=&mkId=&mdId=&prMn=&prMx=&'
        'clrId=&yrMn=&yrMx=&drvTrnId=&mlgMn=&mlgMx=&transTypeId=&kw=&kwm=ANY'
        '&ldId=&rpp=250&slrTypeId=')
def crawl_pages(zipcode, miles):
    print SEARCH_URL.format(zipcode=zipcode, miles=miles)

if __name__ == '__main__':
    #crawl_pages(zipcode='53715', miles='100')
    test_cars()
