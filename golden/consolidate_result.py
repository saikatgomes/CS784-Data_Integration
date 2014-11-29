
import os, sys
from collections import defaultdict
import itertools, operator

def most_common(L):
  if not L: 
    return -2
  # get an iterable of (item, iterable) pairs
  SL = sorted((x, i) for i, x in enumerate(L))
  # print 'SL:', SL
  groups = itertools.groupby(SL, key=operator.itemgetter(0))
  # auxiliary function to get "quality" for an item
  def _auxfun(g):
    item, iterable = g
    count = 0
    min_index = len(L)
    for _, where in iterable:
      count += 1
      min_index = min(min_index, where)
    # print 'item %r, count %r, minind %r' % (item, count, min_index)
    return count, -min_index
  # pick the highest-count/earliest item
  return max(groups, key=_auxfun)[0]
    
def consolidate(fname="gdata/results.txt"):
    D = defaultdict(list)
    started = False
    for l in open(fname):
      id_, val = [int(x) for x in l.split(',')]
      D[id_].append(val)
    for d, v in D.items():
      #print d, v
      print "%d,%d" % (d,most_common(v[1:]))

def fuck(fname_blocking, fname_result):
    D_blocking = {}
    with open(fname_blocking) as f:
      for l in f:
        l = l.strip()
        a = l.split(',');
        D_blocking[a[0]] = a[1:]
    res = {}
    with open(fname_result) as f:
      for l in f:
        l = l.strip()
        a = l.split(',');
        d = D_blocking[a[0]]
        print ','.join([a[0], d[0], d[1], a[1]])


if __name__ == "__main__":
  #consolidate(sys.argv[1])
  fuck(*sys.argv[1:])
