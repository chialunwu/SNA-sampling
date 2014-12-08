import httplib
from model.social_network import SocialNetwork
import random as rd
import networkx as nx

m = SocialNetwork('../data/test_edges2.txt')


def get_node(node):
    assert(type(node) is int)
    d = {}
    d['id'] = node
    d['degree'] = m.G.degree(node)
    d['attr'] = [0, 0, 0, 0, 0]
    d['neighbors'] = []
    for n in m.G.neighbors(node):
        dd = {}
        dd['id'] = n
        dd['degree'] = m.G.degree(n)
        dd['attr'] = [0, 0, 0, 0, 0]
        d['neighbors'].append(dd)
    return d


def get_subgraph():
    seed = rd.choice(m.G.nodes())
    neighbors = []
    d = dict()
    d['nodes'] = []
    g = nx.Graph()
    for i in range(1, 100):
        # Seed
        dd = {}
        dd['id'] = seed
        dd['degree'] = m.G.degree(seed)
        dd['attr'] = [0, 0, 0, 0, 0]
        d['nodes'].append(dd)
        g.add_node(seed)

        dd = {}
        neighbors += [e for e in m.G.neighbors(seed) if e not in g.nodes()]
        n = rd.choice(neighbors)
        neighbors.remove(n)
        dd['id'] = n
        dd['degree'] = m.G.degree(n)
        dd['attr'] = [0, 0, 0, 0, 0]
        d['nodes'].append(dd)
        g.add_node(n)

        for e in m.G.neighbors(n):
            if e in g.nodes():
                g.add_edge(n, e)
            else:
                neighbors.append(e)
        seed = rd.choice(neighbors)
        neighbors.remove(seed)
    d['edges'] = g.edges()
    return d