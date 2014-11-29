#!/usr/bin/python

import os, sys, json
from collections import defaultdict
from cdifflib import difflib
get_close_matches = difflib.get_close_matches

synonyms = {
    'chevy': 'chevrolet'  
    }

def get_uniq_w_list(s):
    A = list(set(y.lower()
            for x in s
            for y in x.split()))
    
    for t, r in synonyms.items():
        try:
            A[A.index(t)] = r
        except ValueError:
            continue
    return list(set(A))

def hash_kbb( fname ):
    with open(fname) as f:
        D = json.load(f)['table']['tuples']
        H = {'make':defaultdict(list),
             'model':defaultdict(list),
             'year':defaultdict(list)
             }
        for t in D:
            for k,v in H.items():
                for y in str(t[k]).split():
                    v[y.lower()].append(t['id'])
                for m,val in v.items():
                    val = list(set(val))
                    v[m] = val

    # for k in H:
    #     print len(H[k].keys())
    json.dump(H, 
              open('blocking_data/kbb-model_make_year_to_id.json', 'w'),
              indent=2,
              sort_keys=True)
    

    return H
   # print json.dumps(H, sort_keys=True, indent=2)

def match_with_craiglist(fname, H):
    D = {}
    with open(fname) as f:
        D = json.load(f)['table']['tuples']
    # match_keys = dict((t, list(set(y.lower() for x in H[t].keys()
    #                                for y in x.split())))
    #                   for t in ['make', 'model', 'year'])
    match_keys = dict((t, H[t].keys())
                      for t in ['make', 'model', 'year'])
    # add some synonyms
    # match_keys['make'].extend(synonyms['make'].keys())

    Match_Craiglist_KBB = {}

    for i,tp in enumerate(D):
        bag = get_uniq_w_list([tp['make'], 
                               tp['attr_title'],
                               tp['title'].split('-')[0]]
                              )
        # try:
        #     bag.remove('%s' % t['year'])
        # except ValueError:
        #     print t['id'], bag, t['year']
        cutoff = {
            'make': 0.9,
            'model': 0.9,
            'year': 1.0}
        match = filter(lambda x: x[1], 
                       [(t, get_close_matches(b, match_keys[t], cutoff=cutoff[t]))
                        for b in bag
                        for t in ['make', 'model', 'year']
                        ])
        # print "Craiglist: >>>>>", bag, tp['id']
        # print "KBB: >>>> ", match,'\n'
        if i%400==0: print (i+1), 'done'
        X = {'model': [], 'make':[], 'year': []}
        for t in match:
            X[t[0]].extend(t[1])
        Match_Craiglist_KBB[tp['id']] = X
    json.dump(Match_Craiglist_KBB, 
              open('blocking_data/craiglist-kbb-keywords.json', 'w'),
              indent=2,
              sort_keys=True)


def final_blocking():
    craig_kbb_list = json.load(open('blocking_data/craiglist-kbb-keywords.json'))
    kbb_index = json.load(open('blocking_data/kbb-model_make_year_to_id.json'))

    # Play with this. Like a kid -- 6 year old
    weights = {'make': 3.0, 'model': 2.0, 'year': 6.0} 
    threshold = 8.0;
    # --- play ends ---
 
    tuple_pair = {}
    for k,v in craig_kbb_list.items()[:]:
        IDs = defaultdict(int)
        for m, vals in v.items():
            for val in vals:
                for i in kbb_index[m][val]:
                    IDs[i] += weights[m]
        tuple_pair[k] = [i for i,j in IDs.items()
                         if j>=threshold]
    #print '\n'.join(str(s)+":  "+str(v) for s,v in tuple_pair.items())
    # .table format
    # pairId:INTEGER,bowker.id:TEXT,walmart.id:TEXT,
    # 1,9780226156439,4086892,

    with open('blocking_data/blocks.table', 'w') as f:
        f.write("pairID:INTEGER,craiglist.id:INTEGER,kbb.id:INTEGER\n")
        pairid = 1
        for k,v in tuple_pair.items():
            f.write('\n'.join("%d,%d,%d" % (pairid+i, int(k), x)
                              for i,x in enumerate(v)))
            f.write('\n')
            pairid += len(v)

    largest =  max(tuple_pair.items(), key=lambda x: len(x[1])) 
    total =  sum(len(x)for x in tuple_pair.values())
    print "Largest:", largest
    print len(largest[1])
    print "Total:",  total


if __name__ == '__main__':
    H = hash_kbb(sys.argv[1])
    match_with_craiglist(sys.argv[2], H)
    final_blocking()


##############################################################################
###################################  TODO ####################################
# -> Extending to sub_cat for kbb, e.g. 330ci
# -> 
##############################################################################
