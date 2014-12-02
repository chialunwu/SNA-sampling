__author__ = 'bingo4508'

import networkx as nx

from model.social_network import SocialNetwork
import sys
from math import *
from scipy import stats


def bin_hist(d, bin_size):
    hist = [0] * (max(d.keys()) / bin_size + 1)
    for k, v in d.items():
        hist[k / bin_size] += v
    return hist


def entropy(p, q):
    s = 0
    if type(p) is list:
        for i in range(len(p)):
            if p[i] != 0 and q[i] != 0:
                s += p[i] * log(float(p[i]) / q[i])
    elif type(p) is dict:
        for k, v in p.items():
            try:
                if v != 0 and q[k] != 0:
                    s += v * log(float(v) / q[k])
            except:
                pass
    else:
        s = None
    return s


def KL_divergence(p, q):
    return 0.5 * (entropy(p, q) + entropy(q, p))


def load_rank(file):
    li = []
    with open(file, 'r') as f:
        for l in f:
            if l.strip():
                li.append(int(l.strip()))
    return li


def load_dist(file):
    d = {}
    with open(file, 'r') as f:
        for l in f:
            l = l.strip().split()
            d[int(l[0])] = float(l[1])
    if d.values()[0] >= 1:
        y = sum(d.values())
        for k in d:
            d[k] = float(d[k]) / y
    return d

try:
    method = sys.argv[1]    # degree dist:-d, closeness: -c, node: -n
    ori = sys.argv[-2]
    spl = sys.argv[-1]
except:
    print "-------- Usage -------"
    print "\nNAME\n\tevaluate - evaluate sample graph with original graph"
    print "\nSYNOPSIS\n\tevaluate OPTION ORINAL_FILE SAMLE_FILE"
    print "\nDESCRIPTION"
    print "\t-d, [--bin]\n\t\tdegree distribution\n\t\t--bin: bin width"
    print "\t-n\n\t\tnode attribute distribution"
    print "\t-c\n\t\tcloseness centrality"
    print "\nFILE FORMAT"
    print "\tformat in a line"
    print "\t-d"
    print "\t\tORINAL_FILE: degree  degree_count\\n"
    print "\t\tSAMPLE_FILE: degree  degree_count\\n"
    print "\t-n"
    print "\t\tORINAL_FILE: attr_id attr_count\\n"
    print "\t\tSAMPLE_FILE: attr_id attr_count\\n"
    print "\t-c"
    print "\t\tORINAL_FILE: node_id (rank i in line i)\\n"
    print "\t\tSAMPLE_FILE: node_id (rank i in line i)\\n"
    sys.exit()

if method == '-d':
    try:
        if '--bin' in sys.argv:
            bin_size = int(sys.argv[sys.argv.index('--bin')+1])
        else:
            bin_size = 5
    except:
        bin_size = 5
    # Degree distribution of original graph
    od = load_dist(ori)
    od = bin_hist(od, bin_size)

    # Degree distribution of sampled graph
    sd = load_dist(spl)
    sd = bin_hist(sd, bin_size)
    sd += [0] * (len(od) - len(sd))

    # KL-divergence
    print "KL divergence: %f" % KL_divergence(od, sd)
elif method == '-n':
    od = load_dist(ori)
    sd = load_dist(spl)

    # KL-divergence
    print "KL divergence: %f" % KL_divergence(od, sd)
elif method == '-c':
    ol = load_rank(ori)
    sl = load_rank(spl)
    R = float(sum([ol.index(e) for e in sl]))/len(sl)

    # Average true rank
    print "Average tre rank: %f" % R
