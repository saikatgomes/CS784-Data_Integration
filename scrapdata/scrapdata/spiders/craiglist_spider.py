import scrapy
from scrapy.http import Request

class CraglistSpider(scrapy.Spider):
    name = "craiglist"
    allowed_domains = ["madison.craigslist.org"]
    start_urls = [
        "http://madison.craigslist.org/search/cta"
    ]
    link_list = []
    def parse(self, response):
        link_list = self.link_list
        already_done = []
        for sel in response.xpath('//p[@class="row"]'):
            link = sel.xpath('a/@href').extract()
            link_list.append(link)
            url = response.url
            if url.find("?s=")<0:
                url = "%s?s=0" % url
            url.split('?s=')
            u = url.split('?s=')[1]
            re.sub('
            if c<1000:
                url = url.replace(str(c), str(c+100))
                yield Request(self.start_urls[0]+"?s=100&")
                
