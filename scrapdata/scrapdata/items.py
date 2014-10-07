# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy


class Contact(scrapy.Item):
    name = scrapy.Field()
    contact_type = scrapy.Field()
    phone= scrapy.Field()
    location = scrapy.Field()
    commission = scrapy.Field()

class Car(scrapy.Item):
    year    = scrapy.Field()
    name    = scrapy.Field()
    make    = scrapy.Field()
    miles   = scrapy.Field() # odo, mi_since_last_user
    color   = scrapy.Field() # interior, exterior
    VIN     = scrapy.Field()
    fuel    = scrapy.Field()
    door    = scrapy.Field()
    engine  = scrapy.Field()
    drive   = scrapy.Field()
    cost    = scrapy.Field()
    contact = scrapy.Field() # ?? TODO
    localtion = scrapy.Field()
    num_owners = scrapy.Field()
    body_style = scrapy.Field()
    transmission_type = scrapy.Field()
    


class ScrapdataItem(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    pass
