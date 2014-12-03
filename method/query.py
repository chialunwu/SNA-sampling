import httplib


def query(url):
    connection = httplib.HTTPConnection("140.112.31.186", 80)
    connection.request("GET", url)
    response = connection.getresponse()
    return response.read().decode("utf-8")


def get_node(node, team="4MdY9pZz6b"):
    assert(type(node) is int)
    url = "/SNA2014/hw3/query.php?team=" + team
    url += "&node=" + str(node)
    data = query(url)
    d = {}
    data = data.strip().split('\n')
    d['num_queries'] = int(data[1])
    x = [int(e) for e in data[2].strip().split()]
    assert(x[1] > 0)
    d['id'] = x[0]
    d['degree'] = x[1]
    d['attr'] = x[2:]
    d['neighbors'] = []
    for n in data[3:]:
        dd = {}
        x = [int(e) for e in n.strip().split()]
        dd['id'] = x[0]
        dd['degree'] = x[1]
        dd['attr'] = x[2:]
        d['neighbors'].append(dd)
    return d
