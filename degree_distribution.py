__author__ = 'bingo4508'

from model.social_network import SocialNetwork
import sys


def output_degree_hist(g, file_name, real=True, fit_degree=False):
    # Degree distribution
    if real:
        degrees = g.degree().values()  # dictionary node:degree
    else:
        degrees = [e[1]['degree'] for e in g.nodes(data=True)]
    values = sorted(set(degrees))
    hist = [degrees.count(x) for x in values]

    if fit_degree:
        y = sum(hist)
        for i in range(len(hist)):
            hist[i] = (float(hist[i]) / y) / values[i]


    with open(file_name, 'w') as f:
        for i in range(len(hist)):
            f.write('%d\t%f\n' % (values[i], hist[i]))


if __name__ == "__main__":
    graph = sys.argv[1]  # data/public_edges.txt
    m = SocialNetwork(graph)
    x = graph.split('.')
    output_degree_hist(m.G, x[0]+'_degree_distribution'+x[1])


