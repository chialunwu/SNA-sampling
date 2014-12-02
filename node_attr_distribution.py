__author__ = 'bingo4508'

import sys

file = sys.argv[1]
# format: node_id,attr1,attr2,attr3...
attr_num = int(sys.argv[2])

xx = file.split('.')
d = [[] for i in xrange(attr_num)]
with open(file, 'r') as f:
    for l in f:
        l = [int(e) for e in l.strip().split(',')[1:]]
        for i, e in enumerate(l):
            d[i].append(e)

for i, e in enumerate(d):
    with open("%s_distribution_attr_%d.%s" % (xx[0], i+1, xx[1]), 'w') as f:
        values = sorted(set(e))
        hist = [e.count(x) for x in values]
        for j in range(len(hist)):
            f.write('%d\t%d\n' % (values[j], hist[j]))