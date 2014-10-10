# coding: utf8 
#!/usr/bin/python

import requests
from lxml import html
import json, time, re
import shutil, sys

def serialize_kbb(fname = 'data/all_makes.json'):
    D = json.load(open(fname))
    cat = set([x for t in D['kbb']\
                   for x in t.keys()])
    cat_att = set(x[1] for t in D['kbb']\
                 for x in t['att'] \
                 if x[0] == 'Powertrain')
    cat.discard('att')
    N = []
    for t in D['kbb']:
        d = dict((k,t.get(k, '')) for k in cat)
        d['id'] = int(d.pop('car_id'))
        d['year'] = int(d['year'])
        for k in cat_att:
            p = '&'.join("%s|%s" % (x[2], x[-1])\
                             for x in t['att'] if x[1] == k)
            d['att_' + k.lower()] = p
        N.append(d)
    Final = {
        'table':{
            'name': 'kbb',
            'description': "cars information from Kbb.com",
            'idAttribute': {
                "name": "id",
                "type": "INTEGER"
                },
            "attributes": [{"name": c, "type": "TEXT"} \
                               for c in cat \
                               if c not in ['year', 'id']] \
                + [ {"name": "year", "type": "INTEGER"},
                    {"name": "id", "type": "INTEGER"}]\
                + [{"name": 'att_' + c.lower(), "type": "TEXT"} \
                       for c in cat_att],
            "tuples": N
            }}
    
    json.dump(Final, open('all_makes_table_kbb.json', 'wb'),
              indent=True, sort_keys=True)
    

def serialize_craiglist(fname = 'craiglist/car_data_present.json'):
    add_name = 'craiglist/car_data_new.json'
    D = json.load(open(fname))
    cat = set([x for t in D\
                   for x in t.keys()])
    E = json.load(open(add_name))
    N = []
    for t,e in zip(D,E):
        N.append(dict((k,t.get(k, '')) for k in cat))
        N[-1]['id'] = int(N[-1]['id'])                      
        if N[-1]['year']:
            N[-1]['year'] = int(N[-1]['year'])
        N[-1]['body'] = e['body']
    cat.add('body')
    Final = {
        'table':{
            'name': 'craiglist',
            'description': "cars information from madison.craiglist.com",
            'idAttribute': {
                "name": "id",
                "type": "INTEGER"
                },
            "attributes": [{"name": c, "type": "TEXT"} \
                               for c in cat\
                               if c not in ['id', 'year']
                           ],
            "tuples": N
            }}
    json.dump(Final, open('all_makes_table_craiglist.json', 'wb'),
              indent=True, sort_keys=True)
    
if __name__ == "__main__":
    serialize_craiglist(sys.argv[1])
