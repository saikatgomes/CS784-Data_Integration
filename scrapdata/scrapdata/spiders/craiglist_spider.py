import scrapy

class CraglistSpider(scrapy.Spider):
    name = "craiglist"
    allowed_domains = ["*.craigslist.org"]
    start_urls = [
        "http://madison.craigslist.org/cto/"
    ]

    def parse(self, response):
        for sel in response.xpath('//p[/li'):
            title = sel.xpath('a/text()').extract()
            link = sel.xpath('a/@href').extract()
            desc = sel.xpath('text()').extract()
            print title, link, desc
