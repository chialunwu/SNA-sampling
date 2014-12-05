__author__ = 'bingo4508'
import math as m

def save_subgraph(g, subgraph):
    for n in subgraph['nodes']:
        g.add_node(n['id'], degree=n['degree'], attr=n['attr'])
    for n in subgraph['edges']:
        g.add_edge(n[0], n[1])


# degrees = g.degree(n)
# all_nodes = g.nodes()
def get_alpha(num_nodes, all_degrees, xmin=1):
    return 1+num_nodes*m.pow(sum([m.log(float(d)/xmin, m.e) for d in all_degrees]), -1)