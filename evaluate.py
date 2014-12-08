__author__ = 'bingo4508'

import networkx as nx

from model.social_network import SocialNetwork
import sys
from math import *
import matplotlib.pyplot as plt

EPSILON = 0.00001
BIN = [[1, 1], [2, 2], [3, 3], [4, 6], [7, 10], [11, 15], [16, 21], [22, 28], [29, 36], [37, 45], [46, 55],
       [56, 70], [71, 100], [101, 200], [201]]


def sum_to_one(li):
    l = []
    y = sum(li)
    for k in li:
        l.append(float(k) / y)

    return l


def bin_hist(d, bin):
    hist = [EPSILON] * len(bin)
    for k, v in d.items():
        for i, e in enumerate(bin):
            if len(e) == 2:
                if e[0] <= k <= e[1]:
                    hist[i] += v
            elif len(e) == 1:
                if e[0] <= k:
                    hist[i] += v
    return sum_to_one(hist)


def entropy(p, q):
    s = 0
    if type(p) is list:
        for i in range(len(p)):
            s += p[i] * log(float(p[i]) / q[i])
    elif type(p) is dict:
        for k, v in p.items():
            try:
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
    y = sum(d.values())
    for k in d:
        d[k] = float(d[k]) / y
    return d


def draw_hist(bin, od, sd, KL, rotation=50):
    fig, ax = plt.subplots()

    index = range(len(bin))
    bar_width = 0.35
    opacity = 0.5

    plt.bar(index, od, bar_width,
            alpha=opacity,
            color='b',
            label='Original')

    plt.bar([x+bar_width for x in index], sd, bar_width,
            alpha=opacity,
            color='r',
            label='Sampled')

    plt.ylabel('Proportion')
    plt.title("KL-divergence: %f" % KL)
    plt.xticks([x+bar_width for x in index], bin, rotation=rotation)
    plt.legend()

    plt.tight_layout()
    plt.show()


def output_final_degree_hist(fn):
    sd = load_dist(fn)
    sd = bin_hist(sd, BIN)

    x = fn.split('.')
    with open(x[0]+'_final.'+x[1], 'w') as f:
        for i, e in enumerate(sd):
            start = BIN[i][0]
            end = 0 if len(BIN[i]) == 1 else BIN[i][1]
            f.write('%d %d %f\n' % (start, end, e))


def output_final_attr_hist(fn, range_start, range_end):
    sd = load_dist(fn)
    td = [EPSILON] * (range_end-range_start+1)
    r = range(range_start, range_end+1)
    for k, v in sd.items():
        td[r.index(k)] = v
    sd = sum_to_one(td)

    x = fn.split('.')
    with open(x[0]+'_final.'+x[1], 'w') as f:
        for i, e in enumerate(sd):
            f.write('%d %f\n' % (r[i], e))


def evaluate(method, ori, spl, plot=False):
    if method == '-d':
        # Degree distribution of original graph
        od = load_dist(ori)
        od = bin_hist(od, BIN)

        # Degree distribution of sampled graph
        sd = load_dist(spl)
        sd = bin_hist(sd, BIN)

        # KL-divergence
        print("Degree KL divergence: %f" % KL_divergence(od, sd))

        if plot:
            draw_hist(BIN, od, sd, KL_divergence(od, sd))
    elif method == '-n':
        od = load_dist(ori)
        sd = load_dist(spl)

        bin = list(od.keys())

        ok = [k for k in od.keys()]
        od = sum_to_one([e for e in od.values()])

        td = [EPSILON] * len(od)
        for k, v in sd.items():
            if k in ok:
                td[ok.index(k)] = v
        sd = sum_to_one(td)

        # KL-divergence
        print("Node KL divergence: %f" % KL_divergence(od, sd))

        if plot:
            draw_hist(bin, od, sd, KL_divergence(od, sd), rotation=90)
    elif method == '-c':
        ol = load_rank(ori)
        sl = load_rank(spl)
        R = float(sum([ol.index(e) for e in sl if e in ol])) / len(sl)

        # Average true rank
        print("Average true rank: %f" % R)


if __name__ == '__main__':
    try:
        method = sys.argv[1]  # degree dist:-d, closeness: -c, node: -n
        ori = sys.argv[-2]
        spl = sys.argv[-1]
        evaluate(method, ori, spl, "--plot" in sys.argv)
    except:
        print("-------------------- Manual -------------------")
        print("\nNAME\n\tevaluate - evaluate sample graph with original graph")
        print("\nSYNOPSIS\n\tevaluate OPTION ORINAL_FILE SAMLE_FILE")
        print("\nDESCRIPTION")
        print("\tEPSILON: 0.00001")
        print("\t-d [--plot]\n\t\tdegree distribution")
        print("\t-n\n\t\tnode attribute distribution")
        print("\t-c\n\t\tcloseness centrality")
        print("\nFILE FORMAT")
        print("\tformat in a line")
        print("\t-d")
        print("\t\tORINAL_FILE: degree  degree_count\\n")
        print("\t\tSAMPLE_FILE: degree  degree_count\\n")
        print("\t-n")
        print("\t\tORINAL_FILE: attr_id attr_count\\n")
        print("\t\tSAMPLE_FILE: attr_id attr_count\\n")
        print("\t-c")
        print("\t\tORINAL_FILE: node_id (rank i in line i)\\n")
        print("\t\tSAMPLE_FILE: node_id (rank i in line i)\\n")
        sys.exit()

