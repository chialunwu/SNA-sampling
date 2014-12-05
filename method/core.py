__author__ = 'bingo4508'


def save_subgraph(g, subgraph):
    for n in subgraph['nodes']:
        g.add_node(n['id'], degree=n['degree'], attr=n['attr'])
    for n in subgraph['edges']:
        g.add_edge(n[0], n[1])