__author__ = 'bingo4508'
import networkx as nx


class SocialNetwork():
    def __init__(self, edge_file):
        self.G = None
        self.load_graph(edge_file)

    # load graph from files
    def load_graph(self, edge_file):
        self.G = nx.Graph()
        # Edges
        with open(edge_file, 'r') as f:
            for l in f:
                l = l.strip().split(',')
                self.G.add_edge(int(l[0]), int(l[1]))