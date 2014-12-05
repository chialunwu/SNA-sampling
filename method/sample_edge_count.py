__author__ = 'bingo4508'

import networkx as nx
from query import *
from core import save_subgraph


def max_dict_val(d, key):
    max = -1000
    r = d.keys()[0]
    for k, v in d.items():
        if v[key] > max:
            max = v[key]
            r = k
    return r


def sample_edge_count(budget, add_all_neighbor=False):
    g = nx.Graph()
    s = get_subgraph()

    # Build initial graph from subgraph
    save_subgraph(g, s)

    # Select seed
    u = sorted(g.nodes(data=True), key=lambda (a, dct): dct['degree'], reverse=True)[0]
    # u = rd.choice(g.nodes(data=True))[0]

    g.add_node(u[0], degree=u[1]['degree'], attr=u[1]['attr'])
    u = u[0]
    neighbors = {}

    for i in range(budget):
        print("Budget left: %d" % (budget-i))

        # Select highest degree n in N(S)
        s = get_node(u)
        neighbors_s = {}
        for e in s['neighbors']:
            id = e.pop('id')
            e['parent'] = s['id']
            if id not in g.nodes():
                neighbors[id] = dict(e)   # Candidate neighbors
            neighbors_s[id] = e
        neighbors_id = neighbors_s.keys()
        v = max_dict_val(neighbors, 'degree')

        # Add edges
        if add_all_neighbor is True:
            for n in neighbors_id:
                g.add_edge(u, n)
                g.add_node(n, degree=neighbors_s[n]['degree'], attr=neighbors_s[n]['attr'])
        else:
            g.add_edge(neighbors[v]['parent'], v)
            g.add_node(v, degree=neighbors_s[v]['degree'], attr=neighbors_s[v]['attr'])
        neighbors.pop(v, None)
        u = v
    return g