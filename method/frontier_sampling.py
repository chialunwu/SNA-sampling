__author__ = 'bingo4508'

# from query_test import *
from query import *
import networkx as nx
import random as rd
from core import save_subgraph
from bisect import bisect


def get_cdf(degrees):
    sum_degree = sum([e for e in degrees])
    prob = [float(e)/sum_degree for e in degrees]
    cdf = [prob[0]]
    for i in xrange(1, len(prob)):
        cdf.append(cdf[-1] + prob[i])
    return cdf


def get_cdf_for_rw(degrees, p_degree):
    x = []
    for e in degrees:
        x.append((1.0/e)*min(1, float(p_degree)/e))
    return get_cdf(x)


def frontier_sample(budget, num_seed, add_all_neighbor=False, debug=False):
    g = nx.Graph()
    s = get_subgraph()

    # Build initial graph from subgraph
    save_subgraph(g, s)

    # Select seeds
    seeds = sorted(g.nodes(data=True), key=lambda (a, dct): dct['degree'], reverse=True)[:num_seed]
    # seeds = rd.sample(g.nodes(data=True), num_seed)

    x = {}
    for e in seeds:
        x[e[0]] = e[1]
    seeds = x

    for i in range(budget):
        if debug:
            print("Budget left: %d" % (budget-i))

        # Select a node from seeds list with probability of degree
        seeds_id = seeds.keys()
        cdf = get_cdf([e['degree'] for e in seeds.values()])
        u = seeds_id[bisect(cdf, rd.random())]

        s = get_node(u)
        neighbors = {}
        for e in s['neighbors']:
            id = e.pop('id')
            neighbors[id] = e
        neighbors_id = neighbors.keys()

        # Select an outgoing edge of u, (u, v), uniformly at random
        # cdf = get_cdf([e['degree'] for e in neighbors.values()])
        # cdf = get_cdf_for_rw([e['degree'] for e in neighbors.values()], s['degree'])
        # v = neighbors_id[bisect(cdf, rd.random())]
        v = rd.choice(neighbors_id)

        # Replace u by v in seeds list and add (u, v) to sequence of sampled edges
        seeds.pop(u)
        seeds[v] = neighbors[v]

        # Add edges
        if add_all_neighbor is True:
            for n in neighbors_id:
                g.add_edge(u, n)
                g.add_node(n, degree=neighbors[n]['degree'], attr=neighbors[n]['attr'])
        else:
            g.add_edge(u, v)
            g.add_node(v, degree=neighbors[v]['degree'], attr=neighbors[v]['attr'])

    return g


