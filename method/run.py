__author__ = 'bingo4508'

from degree_distribution import output_degree_hist
from node_attr_distribution import output_attr_hist
import matplotlib.pyplot as plt
from evaluate import evaluate

from method.sample_edge_count import sample_edge_count
from method.frontier_sampling import frontier_sample
from method.power_law import power_law

if __name__ == '__main__':
    METHOD = 'TEST'
    
    g = frontier_sample(150, 5, add_all_neighbor=True)
    # g = sample_edge_count(300, add_all_neighbor=True)
    # g = power_law(100, add_all_neighbor=True)


    # Output degree distribution
    output_degree_hist(g, '../output/%s_degree_distribution.txt' % METHOD, real=False, fit_degree=True)


    # Output node attribute distribution
    d = [[] for i in xrange(len(g.nodes(data=True)[0][1]['attr']))]
    for e in g.nodes(data=True):
        for i, a in enumerate(e[1]['attr']):
            d[i].append(a)
    output_attr_hist(d, '../output/%s' % METHOD)

    # Output graph edge list
    with open('../output/%s_edges.txt' % METHOD, 'w') as f:
        for e in g.edges():
            f.write('%d %d\n' % (e[0], e[1]))

    ####################################################################################################################
    # Show results on screen
    print("\nNumber of nodes: %d" % len(g.nodes()))
    print("Number of edges: %d" % len(g.edges()))

    # evaluate('-d', '../original/public_edges_degree_distribution.txt', '../output/%s_degree_distribution.txt' % METHOD)
    # for i in range(1, 6):
    #     evaluate('-n', '../original/public_nodes_distribution_attr_%d.txt' % i, '../output/%s_attr_distribution_%d.txt' % (METHOD, i))