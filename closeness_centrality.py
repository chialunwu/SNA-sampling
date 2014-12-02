__author__ = 'bingo4508'

import networkx as nx
from model.social_network import SocialNetwork
import sys

graph = sys.argv[1]  # data/public_edges.txt

m = SocialNetwork(graph)

cc = nx.closeness_centrality(m.G)

x = graph.split('.')
with open(x[0]+'_closeness_centrality.'+x[1], 'w') as f:
    for k, v in cc:
        f.write('%d\t%f\n' % (k, v))