__author__ = 'bingo4508'
import networkx as nx
from query import *
from core import *

PERFECT_ALPHA = 2.5


def max_power_law(neighbors, g):
    all_degree = [e[1]['degree'] for e in g.nodes(data=True)]
    num_nodes = len(g.nodes())

    minus_alpha = 10000
    for k, v in neighbors.items():
        all_degree.append(v['degree'])
        s = abs(get_alpha(num_nodes+1, all_degree) - PERFECT_ALPHA)
        if s < minus_alpha:
            minus_alpha = s
            r = k
        all_degree.remove(v['degree'])
    return r


def power_law(budget, add_all_neighbor=False):
    g = nx.Graph()
    s = get_subgraph()

    # Build initial graph from subgraph
    save_subgraph(g, s)

    # Select seed with highest degree
    u = sorted(g.nodes(data=True), key=lambda (a, dct): dct['degree'], reverse=True)[0]
    g.add_node(u[0], degree=u[1]['degree'], attr=u[1]['attr'])
    u = u[0]
    neighbors = {}
    for i in range(budget):
        print("Budget left: %d" % (budget-i))
        s = get_node(u)
        neighbors_s = {}
        for e in s['neighbors']:
            id = e.pop('id')
            e['parent'] = s['id']
            if id not in g.nodes():
                neighbors[id] = dict(e)   # Candidate neighbors
            neighbors_s[id] = e
        neighbors_id = neighbors_s.keys()

        # Choose the node that fits power law best
        v = max_power_law(neighbors, g)

        # Add edges
        if add_all_neighbor is True:
            for n in neighbors_id:
                g.add_edge(u, n)
                g.add_node(n, degree=neighbors_s[n]['degree'], attr=neighbors_s[n]['attr'])
        else:
            g.add_edge(neighbors[v]['parent'], v)
            g.add_node(v, degree=neighbors[v]['degree'], attr=neighbors[v]['attr'])
        neighbors.pop(v, None)
        u = v
    return g