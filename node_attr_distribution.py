__author__ = 'bingo4508'

import sys


def output_attr_hist(d, prefix):
    for i, e in enumerate(d):
        with open("%s_attr_distribution_%d.txt" % (prefix, i+1), 'w') as f:
            values = sorted(set(e))
            hist = [e.count(x) for x in values]
            for j in range(len(hist)):
                f.write('%d\t%d\n' % (values[j], hist[j]))


if __name__ == "__main__":
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

    output_attr_hist(d, xx[0])