__author__ = 'bingo4508'

from model.social_network import SocialNetwork
import sys

graph = sys.argv[1]  # data/public_edges.txt

m = SocialNetwork(graph)

# Degree distribution
degrees = m.G.degree()  # dictionary node:degree
values = sorted(set(degrees.values()))
hist = [degrees.values().count(x) for x in values]

x = graph.split('.')
with open(x[0]+'_degree_distribution'+x[1], 'w') as f:
    for i in range(len(hist)):
        f.write('%d\t%d\n' % (values[i], hist[i]))
